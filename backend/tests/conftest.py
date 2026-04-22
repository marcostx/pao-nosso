"""
Pytest fixtures shared by the v2 blueprint tests.
"""

import pytest

from app import create_app
from extensions import db as _db


@pytest.fixture
def app():
    app = create_app("development")
    app.config["TESTING"] = True
    app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///:memory:"
    app.config["JWT_SECRET_KEY"] = "test-secret-key"
    return app


@pytest.fixture
def client(app):
    with app.test_client() as client:
        with app.app_context():
            _db.create_all()
        yield client
        with app.app_context():
            _db.drop_all()


def _register_and_login(client, *, nome="Joao", email="joao@example.com", tipo="DOADOR"):
    payload = {
        "nome": nome,
        "email": email,
        "senha": "senha123",
        "telefone": "11999999999",
        "tipo": tipo,
    }
    r = client.post("/api/auth/register", json=payload)
    assert r.status_code == 201, r.get_json()
    body = r.get_json()
    return body["user_id"], body["access_token"]


def _auth(token):
    return {"Authorization": f"Bearer {token}"}


@pytest.fixture
def doador(client):
    user_id, token = _register_and_login(
        client, nome="Maria", email="maria@example.com", tipo="DOADOR"
    )
    return {"id": user_id, "token": token, "headers": _auth(token)}


@pytest.fixture
def instituicao(client):
    user_id, token = _register_and_login(
        client, nome="ONG", email="ong@example.com", tipo="INSTITUICAO"
    )
    payload = {
        "nome_instituicao": "Sopa Solidaria Centro",
        "tipo": "ONG",
        "endereco_completo": "Rua A, 1",
        "telefone_contato": "1133334444",
        "bairro": "Centro",
    }
    r = client.post("/api/instituicoes", json=payload, headers=_auth(token))
    assert r.status_code == 201, r.get_json()
    inst = r.get_json()
    return {
        "id": user_id,
        "token": token,
        "headers": _auth(token),
        "instituicao": inst,
    }
