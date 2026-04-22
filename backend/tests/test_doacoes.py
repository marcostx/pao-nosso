"""
Tests for /api/doacoes.
"""


def test_criar_doacao_solicitar_coleta(client, doador):
    payload = {
        "titulo": "5kg de arroz",
        "categoria": "NAO_PERECIVEL",
        "metodo_entrega": "SOLICITAR_COLETA",
        "endereco_retirada": "Rua das Flores, 123",
        "janela": "HOJE",
        "horario": "17:00",
        "quantidade": "5kg",
    }
    r = client.post("/api/doacoes", json=payload, headers=doador["headers"])
    assert r.status_code == 201
    body = r.get_json()
    assert body["status"] == "DISPONIVEL"
    assert body["metodo_entrega"] == "SOLICITAR_COLETA"
    assert body["instituicao_id"] is None


def test_criar_doacao_eu_entrego_cria_solicitacao(client, doador, instituicao):
    payload = {
        "titulo": "Cesta de frutas",
        "categoria": "HORTIFRUTI",
        "metodo_entrega": "EU_ENTREGO",
        "instituicao_id": instituicao["instituicao"]["id"],
        "janela": "HOJE",
        "horario": "14:30",
        "quantidade": "3kg",
    }
    r = client.post("/api/doacoes", json=payload, headers=doador["headers"])
    assert r.status_code == 201
    doacao = r.get_json()
    assert doacao["instituicao_id"] == instituicao["instituicao"]["id"]

    # A solicitacao inicial deve aparecer para a instituicao
    r2 = client.get("/api/solicitacoes/recebidas", headers=instituicao["headers"])
    assert r2.status_code == 200
    sols = r2.get_json()
    assert len(sols) == 1
    assert sols[0]["doacao_id"] == doacao["id"]


def test_criar_doacao_sem_token(client):
    r = client.post("/api/doacoes", json={"titulo": "x"})
    assert r.status_code == 401


def test_criar_doacao_bloqueia_instituicao(client, instituicao):
    payload = {
        "titulo": "Nao deveria criar",
        "categoria": "NAO_PERECIVEL",
        "metodo_entrega": "SOLICITAR_COLETA",
        "endereco_retirada": "Rua Y",
    }
    r = client.post("/api/doacoes", json=payload, headers=instituicao["headers"])
    assert r.status_code == 403


def test_listar_disponiveis_filtra_por_categoria(client, doador):
    client.post(
        "/api/doacoes",
        json={
            "titulo": "Arroz",
            "categoria": "NAO_PERECIVEL",
            "metodo_entrega": "SOLICITAR_COLETA",
            "endereco_retirada": "Rua X",
        },
        headers=doador["headers"],
    )
    client.post(
        "/api/doacoes",
        json={
            "titulo": "Pera",
            "categoria": "HORTIFRUTI",
            "metodo_entrega": "SOLICITAR_COLETA",
            "endereco_retirada": "Rua X",
        },
        headers=doador["headers"],
    )
    r = client.get("/api/doacoes/disponiveis?categoria=HORTIFRUTI", headers=doador["headers"])
    assert r.status_code == 200
    items = r.get_json()
    assert len(items) == 1
    assert items[0]["categoria"] == "HORTIFRUTI"


def test_minhas_doacoes(client, doador):
    client.post(
        "/api/doacoes",
        json={
            "titulo": "x",
            "categoria": "NAO_PERECIVEL",
            "metodo_entrega": "SOLICITAR_COLETA",
            "endereco_retirada": "Rua Y",
        },
        headers=doador["headers"],
    )
    r = client.get("/api/doacoes/minhas", headers=doador["headers"])
    assert r.status_code == 200
    assert len(r.get_json()) == 1


def test_excluir_doacao_apenas_dono(client, doador, instituicao):
    r = client.post(
        "/api/doacoes",
        json={
            "titulo": "x",
            "categoria": "NAO_PERECIVEL",
            "metodo_entrega": "SOLICITAR_COLETA",
            "endereco_retirada": "Rua Y",
        },
        headers=doador["headers"],
    )
    doacao_id = r.get_json()["id"]
    # Outro usuario nao consegue
    r2 = client.delete(f"/api/doacoes/{doacao_id}", headers=instituicao["headers"])
    assert r2.status_code == 404
    # Dono consegue
    r3 = client.delete(f"/api/doacoes/{doacao_id}", headers=doador["headers"])
    assert r3.status_code == 200


def test_detalhe_404(client, doador):
    r = client.get("/api/doacoes/inexistente", headers=doador["headers"])
    assert r.status_code == 404
