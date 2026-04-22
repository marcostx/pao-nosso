"""
Tests for /api/instituicoes.
"""

from tests.conftest import _auth, _register_and_login


def test_doador_nao_pode_criar(client, doador):
    r = client.post(
        "/api/instituicoes",
        json={
            "nome_instituicao": "x",
            "tipo": "ONG",
            "endereco_completo": "Rua A",
            "telefone_contato": "1",
        },
        headers=doador["headers"],
    )
    assert r.status_code == 403


def test_criar_e_listar(client, instituicao):
    r = client.get("/api/instituicoes", headers=instituicao["headers"])
    assert r.status_code == 200
    items = r.get_json()
    assert len(items) == 1
    assert items[0]["nome_instituicao"] == "Sopa Solidaria Centro"


def test_filtro_por_bairro(client, instituicao):
    r = client.get("/api/instituicoes?bairro=Centro", headers=instituicao["headers"])
    assert r.status_code == 200
    assert len(r.get_json()) == 1
    r = client.get(
        "/api/instituicoes?bairro=Inexistente", headers=instituicao["headers"]
    )
    assert r.get_json() == []


def test_unica_por_usuario(client, instituicao):
    r = client.post(
        "/api/instituicoes",
        json={
            "nome_instituicao": "outra",
            "tipo": "ONG",
            "endereco_completo": "Rua B",
            "telefone_contato": "2",
        },
        headers=instituicao["headers"],
    )
    assert r.status_code == 409


def test_atualizar_apenas_dona(client, instituicao):
    user_id, token = _register_and_login(
        client, nome="Outro", email="outro@example.com", tipo="INSTITUICAO"
    )
    inst_id = instituicao["instituicao"]["id"]
    r = client.put(
        f"/api/instituicoes/{inst_id}",
        json={"descricao": "oops"},
        headers=_auth(token),
    )
    assert r.status_code == 403


def test_me_retorna_404_para_doador(client, doador):
    r = client.get("/api/instituicoes/me", headers=doador["headers"])
    assert r.status_code == 404


def test_endpoints_exigem_token(client):
    assert client.get("/api/instituicoes").status_code == 401
