"""
Aggregate stats per user, used by the home header and profile cards.
"""

import re

from models import Doacao, Solicitacao
from models.doacao import StatusDoacao
from models.solicitacao import StatusSolicitacao
from models.usuario import TipoUsuario

# Cada doacao concluida equivale a esse numero de refeicoes salvas (heuristica MVP).
REFEICOES_POR_DOACAO = 4


def _parse_kg(quantidade) -> float:
    """Best-effort: extrai um numero (kg) da string de quantidade.

    Aceita "5kg", "5 kg", "5", "1.5kg", etc. Retorna 0.0 se nao conseguir.
    """
    if not quantidade:
        return 0.0
    m = re.search(r"(\d+(?:[\.,]\d+)?)", quantidade)
    if not m:
        return 0.0
    try:
        return float(m.group(1).replace(",", "."))
    except ValueError:
        return 0.0


def stats_for_user(usuario) -> dict:
    """Calcula as estatisticas para o doador ou instituicao."""
    if usuario.tipo == TipoUsuario.DOADOR:
        doacoes = Doacao.query.filter_by(doador_id=usuario.id).all()
        concluidas = [d for d in doacoes if d.status == StatusDoacao.COLETADA]
        peso = sum(_parse_kg(d.quantidade) for d in concluidas)
        instituicoes_ids = {d.instituicao_id for d in concluidas if d.instituicao_id is not None}
        return {
            "doacoes_total": len(doacoes),
            "doacoes_concluidas": len(concluidas),
            "peso_total_kg": round(peso, 2),
            "refeicoes_salvas": len(concluidas) * REFEICOES_POR_DOACAO,
            "instituicoes_ajudadas": len(instituicoes_ids),
        }

    # INSTITUICAO
    instituicao = usuario.instituicao
    if not instituicao:
        return {
            "doacoes_total": 0,
            "doacoes_concluidas": 0,
            "peso_total_kg": 0.0,
            "refeicoes_salvas": 0,
            "doadores_atendidos": 0,
        }

    solicitacoes = Solicitacao.query.filter_by(instituicao_id=instituicao.id).all()
    concluidas = [s for s in solicitacoes if s.status == StatusSolicitacao.CONCLUIDA]
    peso = sum(_parse_kg(s.doacao.quantidade) for s in concluidas if s.doacao)
    doadores_ids = {s.doacao.doador_id for s in concluidas if s.doacao}
    return {
        "doacoes_total": len(solicitacoes),
        "doacoes_concluidas": len(concluidas),
        "peso_total_kg": round(peso, 2),
        "refeicoes_salvas": len(concluidas) * REFEICOES_POR_DOACAO,
        "doadores_atendidos": len(doadores_ids),
    }
