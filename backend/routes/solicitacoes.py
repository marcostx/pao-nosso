"""
Routes for Solicitacao (collection requests).
"""

from datetime import date, time

from flask import Blueprint, jsonify, request
from flask_jwt_extended import get_jwt_identity, jwt_required

from extensions import db
from models import Doacao, Solicitacao, Usuario
from models.doacao import StatusDoacao
from models.solicitacao import StatusSolicitacao
from models.usuario import TipoUsuario
from services.donation_service import (
    aceitar_solicitacao,
    cancelar_solicitacao,
    concluir_solicitacao,
    get_instituicao_for_user,
    recusar_solicitacao,
)

solicitacoes_bp = Blueprint("solicitacoes", __name__, url_prefix="/api/solicitacoes")


def _parse_date(value):
    if not value:
        return None
    try:
        return date.fromisoformat(value)
    except (TypeError, ValueError):
        return None


def _parse_time(value):
    if not value:
        return None
    try:
        parts = [int(p) for p in value.split(":")]
        if len(parts) == 2:
            return time(parts[0], parts[1])
        if len(parts) == 3:
            return time(parts[0], parts[1], parts[2])
    except (ValueError, TypeError):
        return None
    return None


def _agendamento_dict(solicitacao: Solicitacao) -> dict:
    """Resposta achatada usada pela tela "Agenda" do app (mock)."""
    doacao = solicitacao.doacao
    instituicao = solicitacao.instituicao
    return {
        "id": str(solicitacao.id),
        "tipo": "solicitacao",
        "doacao_id": str(doacao.id) if doacao else None,
        "item": doacao.titulo if doacao else None,
        "categoria": doacao.categoria.value if doacao and doacao.categoria else None,
        "metodo": doacao.metodo_entrega.value if doacao and doacao.metodo_entrega else None,
        "janela": doacao.janela.value if doacao and doacao.janela else None,
        "horario": doacao.horario.isoformat() if doacao and doacao.horario else None,
        "endereco": doacao.endereco_retirada if doacao else None,
        "instituicao_id": str(instituicao.id) if instituicao else None,
        "instituicao_nome": instituicao.nome_instituicao if instituicao else None,
        "status": solicitacao.status.value,
        "created_at": solicitacao.created_at.isoformat(),
        "updated_at": solicitacao.updated_at.isoformat(),
    }


def _agendamento_doacao_dict(doacao: Doacao) -> dict:
    """Visualiza uma Doacao "solta" (sem solicitacao ativa) na agenda do doador.

    Usado quando o doador escolheu ``SOLICITAR_COLETA`` e ainda nenhuma
    instituicao manifestou interesse. ``id`` e' o proprio doacao_id (nao
    existe solicitacao a referenciar). O cliente diferencia pelo campo
    ``tipo: "doacao"`` e cancela via ``DELETE /api/doacoes/{id}``.
    """
    return {
        "id": str(doacao.id),
        "tipo": "doacao",
        "doacao_id": str(doacao.id),
        "item": doacao.titulo,
        "categoria": doacao.categoria.value if doacao.categoria else None,
        "metodo": doacao.metodo_entrega.value if doacao.metodo_entrega else None,
        "janela": doacao.janela.value if doacao.janela else None,
        "horario": doacao.horario.isoformat() if doacao.horario else None,
        "endereco": doacao.endereco_retirada,
        "instituicao_id": None,
        "instituicao_nome": None,
        "status": "AGUARDANDO",
        "created_at": doacao.created_at.isoformat(),
        "updated_at": doacao.updated_at.isoformat(),
    }


@solicitacoes_bp.route("", methods=["POST"])
@jwt_required()
def criar_solicitacao():
    """Instituicao envia uma solicitacao de coleta para uma doacao DISPONIVEL."""
    user_id = get_jwt_identity()
    usuario = Usuario.query.get(user_id)
    if not usuario or usuario.tipo != TipoUsuario.INSTITUICAO:
        return jsonify({"error": "Apenas instituicoes podem solicitar"}), 403

    instituicao = get_instituicao_for_user(user_id)
    if not instituicao:
        return jsonify({"error": "Instituicao nao cadastrada"}), 404

    data = request.get_json() or {}
    doacao_id = data.get("doacao_id")
    if not doacao_id:
        return jsonify({"error": "doacao_id obrigatorio"}), 400

    doacao = Doacao.query.get(doacao_id)
    if not doacao:
        return jsonify({"error": "Doacao nao encontrada"}), 404
    if doacao.status != StatusDoacao.DISPONIVEL:
        return jsonify({"error": "Doacao nao esta disponivel"}), 409

    existente = Solicitacao.query.filter_by(
        doacao_id=doacao_id,
        instituicao_id=instituicao.id,
        status=StatusSolicitacao.PENDENTE,
    ).first()
    if existente:
        return jsonify(existente.to_dict()), 200

    solicitacao = Solicitacao(
        doacao_id=doacao_id,
        instituicao_id=instituicao.id,
        data_coleta_proposta=_parse_date(data.get("data_coleta_proposta"))
        or doacao.data_disponivel,
        hora_coleta_proposta=_parse_time(data.get("hora_coleta_proposta")) or doacao.horario,
        observacoes=data.get("observacoes"),
        status=StatusSolicitacao.PENDENTE,
    )
    db.session.add(solicitacao)
    db.session.commit()
    return jsonify(solicitacao.to_dict()), 201


@solicitacoes_bp.route("/recebidas", methods=["GET"])
@jwt_required()
def listar_recebidas():
    """Doador (dono da doacao) ou instituicao alvo veem solicitacoes recebidas.

    Para o doador: todas as solicitacoes feitas em suas doacoes.
    Para a instituicao: solicitacoes em doacoes onde ela e o ponto de coleta.
    """
    user_id = get_jwt_identity()
    usuario = Usuario.query.get(user_id)
    if not usuario:
        return jsonify({"error": "Usuario nao encontrado"}), 404

    if usuario.tipo == TipoUsuario.DOADOR:
        solicitacoes = (
            Solicitacao.query.join(Doacao)
            .filter(Doacao.doador_id == user_id)
            .order_by(Solicitacao.created_at.desc())
            .all()
        )
    else:
        instituicao = get_instituicao_for_user(user_id)
        if not instituicao:
            return jsonify([]), 200
        solicitacoes = (
            Solicitacao.query.filter_by(instituicao_id=instituicao.id)
            .order_by(Solicitacao.created_at.desc())
            .all()
        )

    return jsonify([s.to_dict() for s in solicitacoes]), 200


@solicitacoes_bp.route("/enviadas", methods=["GET"])
@jwt_required()
def listar_enviadas():
    """Apenas instituicoes: solicitacoes que ela mesma enviou."""
    user_id = get_jwt_identity()
    instituicao = get_instituicao_for_user(user_id)
    if not instituicao:
        return jsonify([]), 200

    solicitacoes = (
        Solicitacao.query.filter_by(instituicao_id=instituicao.id)
        .order_by(Solicitacao.created_at.desc())
        .all()
    )
    return jsonify([s.to_dict() for s in solicitacoes]), 200


@solicitacoes_bp.route("/agendamentos", methods=["GET"])
@jwt_required()
def listar_agendamentos():
    """Lista achatada usada pela aba "Agenda" do app (mock).

    Para o doador: solicitacoes vinculadas as suas doacoes + doacoes
    DISPONIVEL sem nenhuma solicitacao ativa (status sintetico
    ``AGUARDANDO`` — tipico de SOLICITAR_COLETA recem-criado).
    Para a instituicao: solicitacoes feitas por ela.

    Filtro opcional: ``?status=PENDENTE|ACEITA|RECUSADA|CANCELADA|CONCLUIDA|AGUARDANDO``.
    Para a instituicao o filtro ``AGUARDANDO`` retorna lista vazia (so faz
    sentido na visao do doador).
    """
    user_id = get_jwt_identity()
    usuario = Usuario.query.get(user_id)
    if not usuario:
        return jsonify({"error": "Usuario nao encontrado"}), 404

    status_str = request.args.get("status")
    is_aguardando_filter = status_str == "AGUARDANDO"

    if usuario.tipo == TipoUsuario.INSTITUICAO:
        instituicao = get_instituicao_for_user(user_id)
        if not instituicao or is_aguardando_filter:
            return jsonify([]), 200
        query = Solicitacao.query.filter_by(instituicao_id=instituicao.id)
        if status_str:
            try:
                status_enum = StatusSolicitacao[status_str]
            except KeyError:
                return jsonify({"error": "status invalido"}), 400
            query = query.filter(Solicitacao.status == status_enum)
        solicitacoes = query.order_by(Solicitacao.created_at.desc()).all()
        return jsonify([_agendamento_dict(s) for s in solicitacoes]), 200

    # Doador: combina solicitacoes vinculadas as suas doacoes + doacoes
    # DISPONIVEL sem nenhuma solicitacao ativa.
    items: list[dict] = []

    if not is_aguardando_filter:
        sol_query = Solicitacao.query.join(Doacao).filter(Doacao.doador_id == user_id)
        if status_str:
            try:
                status_enum = StatusSolicitacao[status_str]
            except KeyError:
                return jsonify({"error": "status invalido"}), 400
            sol_query = sol_query.filter(Solicitacao.status == status_enum)
        solicitacoes = sol_query.order_by(Solicitacao.created_at.desc()).all()
        items.extend(_agendamento_dict(s) for s in solicitacoes)

    if status_str is None or is_aguardando_filter:
        active_doacao_ids_subq = (
            db.session.query(Solicitacao.doacao_id)
            .filter(Solicitacao.status.in_([StatusSolicitacao.PENDENTE, StatusSolicitacao.ACEITA]))
            .subquery()
        )
        doacoes_aguardando = (
            Doacao.query.filter(Doacao.doador_id == user_id)
            .filter(Doacao.status == StatusDoacao.DISPONIVEL)
            .filter(~Doacao.id.in_(db.session.query(active_doacao_ids_subq.c.doacao_id)))
            .order_by(Doacao.created_at.desc())
            .all()
        )
        items.extend(_agendamento_doacao_dict(d) for d in doacoes_aguardando)

    items.sort(key=lambda x: x["created_at"], reverse=True)
    return jsonify(items), 200


def _load_solicitacao_para_doador(user_id, solicitacao_id):
    """Garante que a solicitacao pertence a uma doacao do doador autenticado."""
    return (
        Solicitacao.query.join(Doacao)
        .filter(Solicitacao.id == solicitacao_id, Doacao.doador_id == user_id)
        .first()
    )


def _load_solicitacao_para_instituicao(user_id, solicitacao_id):
    instituicao = get_instituicao_for_user(user_id)
    if not instituicao:
        return None
    return Solicitacao.query.filter_by(id=solicitacao_id, instituicao_id=instituicao.id).first()


@solicitacoes_bp.route("/<solicitacao_id>/aceitar", methods=["PUT"])
@jwt_required()
def aceitar(solicitacao_id):
    """Doador aceita uma solicitacao."""
    user_id = get_jwt_identity()
    solicitacao = _load_solicitacao_para_doador(user_id, solicitacao_id)
    if not solicitacao:
        return jsonify({"error": "Solicitacao nao encontrada"}), 404
    try:
        aceitar_solicitacao(solicitacao)
        db.session.commit()
        return jsonify(solicitacao.to_dict()), 200
    except ValueError as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 409


@solicitacoes_bp.route("/<solicitacao_id>/recusar", methods=["PUT"])
@jwt_required()
def recusar(solicitacao_id):
    """Doador recusa uma solicitacao."""
    user_id = get_jwt_identity()
    solicitacao = _load_solicitacao_para_doador(user_id, solicitacao_id)
    if not solicitacao:
        return jsonify({"error": "Solicitacao nao encontrada"}), 404
    try:
        recusar_solicitacao(solicitacao)
        db.session.commit()
        return jsonify(solicitacao.to_dict()), 200
    except ValueError as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 409


@solicitacoes_bp.route("/<solicitacao_id>/cancelar", methods=["PUT"])
@jwt_required()
def cancelar(solicitacao_id):
    """Doador (ou instituicao que enviou) cancela uma solicitacao."""
    user_id = get_jwt_identity()
    solicitacao = _load_solicitacao_para_doador(
        user_id, solicitacao_id
    ) or _load_solicitacao_para_instituicao(user_id, solicitacao_id)
    if not solicitacao:
        return jsonify({"error": "Solicitacao nao encontrada"}), 404
    try:
        cancelar_solicitacao(solicitacao)
        db.session.commit()
        return jsonify(solicitacao.to_dict()), 200
    except ValueError as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 409


@solicitacoes_bp.route("/<solicitacao_id>/concluir", methods=["PUT"])
@jwt_required()
def concluir(solicitacao_id):
    """Doador ou instituicao envolvida marca a coleta como concluida."""
    user_id = get_jwt_identity()
    solicitacao = _load_solicitacao_para_doador(
        user_id, solicitacao_id
    ) or _load_solicitacao_para_instituicao(user_id, solicitacao_id)
    if not solicitacao:
        return jsonify({"error": "Solicitacao nao encontrada"}), 404
    try:
        concluir_solicitacao(solicitacao)
        db.session.commit()
        return jsonify(solicitacao.to_dict()), 200
    except ValueError as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 409
