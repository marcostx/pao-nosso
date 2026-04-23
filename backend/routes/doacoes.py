"""
Donation routes (Doacao).

Pao Nosso v2: list-based UX, no map / no lat-lng filters.
"""

from datetime import time

from flask import Blueprint, current_app, jsonify, request
from flask_jwt_extended import get_jwt_identity, jwt_required

from extensions import db
from models import Doacao, Instituicao, Usuario
from models.doacao import (
    CategoriaDoacao,
    JanelaEntrega,
    MetodoEntrega,
    StatusDoacao,
)
from models.usuario import TipoUsuario
from services.donation_service import (
    attach_initial_solicitacao,
    get_doacao_for_owner,
)

doacoes_bp = Blueprint("doacoes", __name__, url_prefix="/api/doacoes")


def _parse_horario(value):
    """Aceita "HH:MM" ou "HH:MM:SS" e retorna time."""
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


def _parse_enum(enum_cls, value, *, optional=False):
    if value is None:
        return None if optional else None
    try:
        return enum_cls[value]
    except KeyError:
        return None


@doacoes_bp.route("", methods=["POST"])
@jwt_required()
def criar_doacao():
    """Cria uma nova doacao a partir do fluxo "Nova Doacao" (3 passos)."""
    try:
        user_id = get_jwt_identity()
        usuario = Usuario.query.get(user_id)
        if not usuario:
            return jsonify({"error": "Usuario nao encontrado"}), 404
        if usuario.tipo != TipoUsuario.DOADOR:
            return jsonify({"error": "Apenas doadores podem criar doacoes"}), 403

        data = request.get_json() or {}

        titulo = (data.get("titulo") or "").strip()
        if not titulo:
            return jsonify({"error": "Campo obrigatorio: titulo"}), 400

        categoria = _parse_enum(CategoriaDoacao, data.get("categoria"))
        if categoria is None:
            return jsonify({"error": "Categoria invalida"}), 400

        metodo = _parse_enum(MetodoEntrega, data.get("metodo_entrega"))
        if metodo is None:
            return jsonify({"error": "metodo_entrega invalido"}), 400

        janela = _parse_enum(JanelaEntrega, data.get("janela"), optional=True)
        horario = _parse_horario(data.get("horario"))

        instituicao_id = data.get("instituicao_id")
        endereco_retirada = data.get("endereco_retirada")

        if metodo == MetodoEntrega.EU_ENTREGO:
            if not instituicao_id:
                return jsonify({"error": "instituicao_id obrigatorio para EU_ENTREGO"}), 400
            instituicao = Instituicao.query.get(instituicao_id)
            if not instituicao:
                return jsonify({"error": "Instituicao nao encontrada"}), 404
        else:
            instituicao_id = None
            if not endereco_retirada:
                return (
                    jsonify({"error": "endereco_retirada obrigatorio para SOLICITAR_COLETA"}),
                    400,
                )

        doacao = Doacao(
            doador_id=user_id,
            titulo=titulo,
            descricao=data.get("descricao"),
            quantidade=data.get("quantidade"),
            categoria=categoria,
            janela=janela,
            horario=horario,
            metodo_entrega=metodo,
            endereco_retirada=endereco_retirada,
            bairro=data.get("bairro"),
            instituicao_id=instituicao_id,
            status=StatusDoacao.DISPONIVEL,
        )
        db.session.add(doacao)
        db.session.flush()  # garante doacao.id antes de attach_initial_solicitacao

        attach_initial_solicitacao(doacao)

        db.session.commit()
        return jsonify(doacao.to_dict()), 201

    except Exception as e:  # pragma: no cover - defensive
        db.session.rollback()
        current_app.logger.exception("Erro inesperado em POST /api/doacoes")
        return jsonify({"error": f"Erro ao criar doacao: {str(e)}"}), 500


@doacoes_bp.route("/disponiveis", methods=["GET"])
@jwt_required()
def listar_disponiveis():
    """Lista doacoes DISPONIVEIS (instituicoes consultam aqui).

    Filtros opcionais (todos string):
      ?bairro=Centro
      ?categoria=PERECIVEL
      ?janela=HOJE
    """
    try:
        query = Doacao.query.filter(Doacao.status == StatusDoacao.DISPONIVEL)

        bairro = request.args.get("bairro")
        if bairro:
            query = query.filter(Doacao.bairro.ilike(f"%{bairro}%"))

        categoria_str = request.args.get("categoria")
        if categoria_str:
            categoria = _parse_enum(CategoriaDoacao, categoria_str)
            if categoria is None:
                return jsonify({"error": "categoria invalida"}), 400
            query = query.filter(Doacao.categoria == categoria)

        janela_str = request.args.get("janela")
        if janela_str:
            janela = _parse_enum(JanelaEntrega, janela_str)
            if janela is None:
                return jsonify({"error": "janela invalida"}), 400
            query = query.filter(Doacao.janela == janela)

        doacoes = query.order_by(Doacao.created_at.desc()).all()
        return jsonify([d.to_dict() for d in doacoes]), 200

    except Exception as e:  # pragma: no cover - defensive
        current_app.logger.exception("Erro inesperado em GET /api/doacoes/disponiveis")
        return jsonify({"error": f"Erro ao listar: {str(e)}"}), 500


@doacoes_bp.route("/minhas", methods=["GET"])
@jwt_required()
def minhas_doacoes():
    """Doacoes criadas pelo doador autenticado."""
    user_id = get_jwt_identity()
    doacoes = Doacao.query.filter_by(doador_id=user_id).order_by(Doacao.created_at.desc()).all()
    return jsonify([d.to_dict() for d in doacoes]), 200


@doacoes_bp.route("/<doacao_id>", methods=["GET"])
@jwt_required()
def detalhe_doacao(doacao_id):
    doacao = Doacao.query.get(doacao_id)
    if not doacao:
        return jsonify({"error": "Doacao nao encontrada"}), 404
    return jsonify(doacao.to_dict()), 200


@doacoes_bp.route("/<doacao_id>", methods=["PUT"])
@jwt_required()
def atualizar_doacao(doacao_id):
    user_id = get_jwt_identity()
    doacao = get_doacao_for_owner(doacao_id, user_id)
    if not doacao:
        return jsonify({"error": "Doacao nao encontrada"}), 404
    if doacao.status not in (StatusDoacao.DISPONIVEL, StatusDoacao.CANCELADA):
        return jsonify({"error": "Doacao nao pode ser editada nesse status"}), 409

    data = request.get_json() or {}
    if "titulo" in data:
        doacao.titulo = data["titulo"]
    if "descricao" in data:
        doacao.descricao = data["descricao"]
    if "quantidade" in data:
        doacao.quantidade = data["quantidade"]
    if "categoria" in data:
        cat = _parse_enum(CategoriaDoacao, data["categoria"])
        if cat is None:
            return jsonify({"error": "categoria invalida"}), 400
        doacao.categoria = cat
    if "janela" in data:
        doacao.janela = _parse_enum(JanelaEntrega, data["janela"], optional=True)
    if "horario" in data:
        doacao.horario = _parse_horario(data["horario"])
    if "endereco_retirada" in data:
        doacao.endereco_retirada = data["endereco_retirada"]
    if "bairro" in data:
        doacao.bairro = data["bairro"]

    db.session.commit()
    return jsonify(doacao.to_dict()), 200


@doacoes_bp.route("/<doacao_id>", methods=["DELETE"])
@jwt_required()
def excluir_doacao(doacao_id):
    user_id = get_jwt_identity()
    doacao = get_doacao_for_owner(doacao_id, user_id)
    if not doacao:
        return jsonify({"error": "Doacao nao encontrada"}), 404
    if doacao.status == StatusDoacao.COLETADA:
        return jsonify({"error": "Doacao ja coletada nao pode ser excluida"}), 409

    db.session.delete(doacao)
    db.session.commit()
    return jsonify({"message": "Doacao excluida"}), 200
