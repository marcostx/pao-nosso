"""
Tests for /api/stats/me.
"""


def test_stats_doador_vazio(client, doador):
    r = client.get("/api/stats/me", headers=doador["headers"])
    assert r.status_code == 200
    body = r.get_json()
    assert body["doacoes_total"] == 0
    assert body["refeicoes_salvas"] == 0
    assert body["peso_total_kg"] == 0.0


def test_stats_doador_com_doacao_concluida(client, doador, instituicao):
    doacao = client.post(
        "/api/doacoes",
        json={
            "titulo": "Arroz",
            "categoria": "NAO_PERECIVEL",
            "metodo_entrega": "EU_ENTREGO",
            "instituicao_id": instituicao["instituicao"]["id"],
            "quantidade": "5kg",
        },
        headers=doador["headers"],
    ).get_json()

    sols = client.get("/api/solicitacoes/recebidas", headers=instituicao["headers"]).get_json()
    sol_id = sols[0]["id"]

    client.put(f"/api/solicitacoes/{sol_id}/aceitar", headers=doador["headers"])
    client.put(f"/api/solicitacoes/{sol_id}/concluir", headers=doador["headers"])

    r = client.get("/api/stats/me", headers=doador["headers"])
    body = r.get_json()
    assert body["doacoes_total"] == 1
    assert body["doacoes_concluidas"] == 1
    assert body["peso_total_kg"] == 5.0
    assert body["refeicoes_salvas"] == 4
    assert body["instituicoes_ajudadas"] == 1


def test_stats_sem_token(client):
    r = client.get("/api/stats/me")
    assert r.status_code == 401
