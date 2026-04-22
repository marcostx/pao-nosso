"""
Domain service for Doacao + Solicitacao state transitions.

Centralizes the rules that must hold across endpoints:
- "Eu entrego" auto-creates a Solicitacao tied to the chosen institution.
- Aceitar uma solicitacao move a Doacao para RESERVADA e auto-recusa as irmas.
- Concluir uma solicitacao marca a Doacao como COLETADA.
- Cancelar/recusar solicitacao volta a Doacao para DISPONIVEL quando aplicavel.
"""

from datetime import datetime
from typing import Optional

from extensions import db
from models import Doacao, Instituicao, Solicitacao
from models.doacao import MetodoEntrega, StatusDoacao
from models.solicitacao import StatusSolicitacao


def attach_initial_solicitacao(doacao: Doacao) -> Optional[Solicitacao]:
    """Quando o doador escolhe "Eu entrego" + ponto de coleta, ja registramos
    uma Solicitacao PENDENTE para a instituicao escolhida."""
    if doacao.metodo_entrega != MetodoEntrega.EU_ENTREGO:
        return None
    if not doacao.instituicao_id:
        return None

    solicitacao = Solicitacao(
        doacao_id=doacao.id,
        instituicao_id=doacao.instituicao_id,
        data_coleta_proposta=doacao.data_disponivel,
        hora_coleta_proposta=doacao.horario,
        status=StatusSolicitacao.PENDENTE,
    )
    db.session.add(solicitacao)
    return solicitacao


def aceitar_solicitacao(solicitacao: Solicitacao) -> Solicitacao:
    """Aceita uma solicitacao, reserva a doacao e recusa as irmas."""
    if solicitacao.status != StatusSolicitacao.PENDENTE:
        raise ValueError("Apenas solicitacoes PENDENTES podem ser aceitas")

    solicitacao.status = StatusSolicitacao.ACEITA
    solicitacao.updated_at = datetime.utcnow()

    doacao = solicitacao.doacao
    doacao.status = StatusDoacao.RESERVADA
    doacao.instituicao_id = solicitacao.instituicao_id
    doacao.updated_at = datetime.utcnow()

    irmas = (
        db.session.query(Solicitacao)
        .filter(
            Solicitacao.doacao_id == doacao.id,
            Solicitacao.id != solicitacao.id,
            Solicitacao.status == StatusSolicitacao.PENDENTE,
        )
        .all()
    )
    for irma in irmas:
        irma.status = StatusSolicitacao.RECUSADA
        irma.updated_at = datetime.utcnow()

    return solicitacao


def recusar_solicitacao(solicitacao: Solicitacao) -> Solicitacao:
    """Recusa uma solicitacao."""
    if solicitacao.status not in (StatusSolicitacao.PENDENTE, StatusSolicitacao.ACEITA):
        raise ValueError("Solicitacao nao pode ser recusada nesse estado")

    foi_aceita = solicitacao.status == StatusSolicitacao.ACEITA
    solicitacao.status = StatusSolicitacao.RECUSADA
    solicitacao.updated_at = datetime.utcnow()

    if foi_aceita:
        doacao = solicitacao.doacao
        doacao.status = StatusDoacao.DISPONIVEL
        doacao.instituicao_id = None
        doacao.updated_at = datetime.utcnow()

    return solicitacao


def cancelar_solicitacao(solicitacao: Solicitacao) -> Solicitacao:
    """Cancelamento feito pelo doador."""
    if solicitacao.status in (StatusSolicitacao.CONCLUIDA, StatusSolicitacao.CANCELADA):
        raise ValueError("Solicitacao ja encerrada")

    estava_aceita = solicitacao.status == StatusSolicitacao.ACEITA
    solicitacao.status = StatusSolicitacao.CANCELADA
    solicitacao.updated_at = datetime.utcnow()

    if estava_aceita:
        doacao = solicitacao.doacao
        doacao.status = StatusDoacao.CANCELADA
        doacao.updated_at = datetime.utcnow()

    return solicitacao


def concluir_solicitacao(solicitacao: Solicitacao) -> Solicitacao:
    """Marca a coleta como concluida."""
    if solicitacao.status != StatusSolicitacao.ACEITA:
        raise ValueError("Apenas solicitacoes ACEITAS podem ser concluidas")

    solicitacao.status = StatusSolicitacao.CONCLUIDA
    solicitacao.updated_at = datetime.utcnow()

    doacao = solicitacao.doacao
    doacao.status = StatusDoacao.COLETADA
    doacao.updated_at = datetime.utcnow()

    return solicitacao


def get_instituicao_for_user(user_id: str) -> Optional[Instituicao]:
    """Helper: retorna a instituicao associada ao usuario logado, se houver."""
    return Instituicao.query.filter_by(usuario_id=user_id).first()


def get_doacao_for_owner(doacao_id: str, user_id: str) -> Optional[Doacao]:
    """Helper: retorna a doacao se pertence ao usuario."""
    return Doacao.query.filter_by(id=doacao_id, doador_id=user_id).first()
