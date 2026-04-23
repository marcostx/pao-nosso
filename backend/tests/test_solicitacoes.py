"""
Tests for /api/solicitacoes including state transitions.
"""

from tests.conftest import _auth, _register_and_login


def _criar_doacao_solicitar_coleta(client, doador, titulo="Arroz"):
    r = client.post(
        "/api/doacoes",
        json={
            "titulo": titulo,
            "categoria": "NAO_PERECIVEL",
            "metodo_entrega": "SOLICITAR_COLETA",
            "endereco_retirada": "Rua X",
            "quantidade": "5kg",
        },
        headers=doador["headers"],
    )
    assert r.status_code == 201
    return r.get_json()


def _segunda_instituicao(client):
    user_id, token = _register_and_login(
        client, nome="ONG2", email="ong2@example.com", tipo="INSTITUICAO"
    )
    payload = {
        "nome_instituicao": "Maos que Alimentam",
        "tipo": "ONG",
        "endereco_completo": "Rua B",
        "telefone_contato": "1133335555",
        "bairro": "Centro",
    }
    r = client.post("/api/instituicoes", json=payload, headers=_auth(token))
    assert r.status_code == 201
    inst = r.get_json()
    return {"token": token, "headers": _auth(token), "instituicao": inst}


def test_criar_solicitacao_apenas_instituicao(client, doador):
    doacao = _criar_doacao_solicitar_coleta(client, doador)
    r = client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao["id"]},
        headers=doador["headers"],
    )
    assert r.status_code == 403


def test_criar_solicitacao_ok(client, doador, instituicao):
    doacao = _criar_doacao_solicitar_coleta(client, doador)
    r = client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao["id"]},
        headers=instituicao["headers"],
    )
    assert r.status_code == 201
    assert r.get_json()["status"] == "PENDENTE"


def test_criar_solicitacao_doacao_inexistente(client, instituicao):
    r = client.post(
        "/api/solicitacoes",
        json={"doacao_id": "nao-existe"},
        headers=instituicao["headers"],
    )
    assert r.status_code == 404


def test_aceitar_recusa_irmas(client, doador, instituicao):
    doacao = _criar_doacao_solicitar_coleta(client, doador)
    inst2 = _segunda_instituicao(client)

    r1 = client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao["id"]},
        headers=instituicao["headers"],
    )
    r2 = client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao["id"]},
        headers=inst2["headers"],
    )
    sol1 = r1.get_json()
    sol2 = r2.get_json()

    # Doador aceita a primeira
    r = client.put(f"/api/solicitacoes/{sol1['id']}/aceitar", headers=doador["headers"])
    assert r.status_code == 200
    assert r.get_json()["status"] == "ACEITA"

    # A segunda deve estar RECUSADA automaticamente
    r = client.get("/api/solicitacoes/recebidas", headers=doador["headers"])
    sols = {s["id"]: s for s in r.get_json()}
    assert sols[sol1["id"]]["status"] == "ACEITA"
    assert sols[sol2["id"]]["status"] == "RECUSADA"

    # E a doacao virou RESERVADA
    r = client.get(f"/api/doacoes/{doacao['id']}", headers=doador["headers"])
    assert r.get_json()["status"] == "RESERVADA"


def test_aceitar_apenas_dono_da_doacao(client, doador, instituicao):
    doacao = _criar_doacao_solicitar_coleta(client, doador)
    sol = client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao["id"]},
        headers=instituicao["headers"],
    ).get_json()
    # A propria instituicao nao pode aceitar
    r = client.put(f"/api/solicitacoes/{sol['id']}/aceitar", headers=instituicao["headers"])
    assert r.status_code == 404


def test_concluir_marca_doacao_coletada(client, doador, instituicao):
    doacao = _criar_doacao_solicitar_coleta(client, doador)
    sol = client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao["id"]},
        headers=instituicao["headers"],
    ).get_json()
    client.put(f"/api/solicitacoes/{sol['id']}/aceitar", headers=doador["headers"])
    r = client.put(f"/api/solicitacoes/{sol['id']}/concluir", headers=doador["headers"])
    assert r.status_code == 200
    assert r.get_json()["status"] == "CONCLUIDA"

    r = client.get(f"/api/doacoes/{doacao['id']}", headers=doador["headers"])
    assert r.get_json()["status"] == "COLETADA"


def test_agendamentos_filtro_status(client, doador, instituicao):
    doacao = _criar_doacao_solicitar_coleta(client, doador)
    client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao["id"]},
        headers=instituicao["headers"],
    )
    r = client.get("/api/solicitacoes/agendamentos?status=PENDENTE", headers=doador["headers"])
    assert r.status_code == 200
    items = r.get_json()
    assert len(items) == 1
    assert items[0]["status"] == "PENDENTE"
    assert items[0]["item"] == "Arroz"


def test_agendamentos_inclui_doacao_aguardando_para_doador(client, doador):
    """Doacao SOLICITAR_COLETA recem-criada (sem solicitacao) deve aparecer
    como item sintetico AGUARDANDO na agenda do doador, para que ele veja
    a doacao logo apos confirma-la."""
    doacao = _criar_doacao_solicitar_coleta(client, doador, titulo="Pao Frances")

    r = client.get("/api/solicitacoes/agendamentos", headers=doador["headers"])
    assert r.status_code == 200
    items = r.get_json()
    assert len(items) == 1
    assert items[0]["tipo"] == "doacao"
    assert items[0]["status"] == "AGUARDANDO"
    assert items[0]["item"] == "Pao Frances"
    assert items[0]["doacao_id"] == doacao["id"]
    assert items[0]["id"] == doacao["id"]  # id == doacao_id quando tipo=doacao


def test_agendamentos_oculta_doacao_apos_solicitacao_ativa(client, doador, instituicao):
    """Quando a instituicao envia uma solicitacao, a doacao some do bucket
    AGUARDANDO e passa a aparecer somente como item tipo=solicitacao."""
    doacao = _criar_doacao_solicitar_coleta(client, doador, titulo="Feijao")

    # Antes da solicitacao: 1 item AGUARDANDO
    r = client.get("/api/solicitacoes/agendamentos", headers=doador["headers"])
    assert len(r.get_json()) == 1
    assert r.get_json()[0]["status"] == "AGUARDANDO"

    # Instituicao solicita
    client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao["id"]},
        headers=instituicao["headers"],
    )

    r = client.get("/api/solicitacoes/agendamentos", headers=doador["headers"])
    items = r.get_json()
    assert len(items) == 1
    assert items[0]["tipo"] == "solicitacao"
    assert items[0]["status"] == "PENDENTE"


def test_agendamentos_filtro_aguardando(client, doador, instituicao):
    """Filtro ?status=AGUARDANDO deve devolver apenas as doacoes sem
    solicitacao ativa, ignorando solicitacoes."""
    doacao_sem_solicit = _criar_doacao_solicitar_coleta(client, doador, titulo="Pao")
    doacao_com_solicit = _criar_doacao_solicitar_coleta(client, doador, titulo="Arroz")
    client.post(
        "/api/solicitacoes",
        json={"doacao_id": doacao_com_solicit["id"]},
        headers=instituicao["headers"],
    )

    r = client.get(
        "/api/solicitacoes/agendamentos?status=AGUARDANDO",
        headers=doador["headers"],
    )
    items = r.get_json()
    assert len(items) == 1
    assert items[0]["doacao_id"] == doacao_sem_solicit["id"]
    assert items[0]["status"] == "AGUARDANDO"


def test_agendamentos_instituicao_ignora_filtro_aguardando(client, instituicao):
    """AGUARDANDO so faz sentido na visao do doador; instituicao recebe lista vazia."""
    r = client.get(
        "/api/solicitacoes/agendamentos?status=AGUARDANDO",
        headers=instituicao["headers"],
    )
    assert r.status_code == 200
    assert r.get_json() == []


def test_endpoints_exigem_token(client):
    assert client.get("/api/solicitacoes/recebidas").status_code == 401
    assert client.get("/api/solicitacoes/agendamentos").status_code == 401
