"""
Routes for Instituicao.
"""

from flask import Blueprint, jsonify, request
from flask_jwt_extended import get_jwt_identity, jwt_required

from extensions import db
from models import Instituicao, Usuario
from models.instituicao import TipoInstituicao
from models.usuario import TipoUsuario

instituicoes_bp = Blueprint("instituicoes", __name__, url_prefix="/api/instituicoes")


def _parse_tipo(value):
    try:
        return TipoInstituicao[value]
    except (KeyError, TypeError):
        return None


@instituicoes_bp.route("", methods=["POST"])
@jwt_required()
def criar_instituicao():
    """Cadastra a instituicao do usuario logado (1 por usuario)."""
    user_id = get_jwt_identity()
    usuario = Usuario.query.get(user_id)
    if not usuario:
        return jsonify({"error": "Usuario nao encontrado"}), 404
    if usuario.tipo != TipoUsuario.INSTITUICAO:
        return jsonify({"error": "Apenas usuarios INSTITUICAO podem cadastrar"}), 403
    if Instituicao.query.filter_by(usuario_id=user_id).first():
        return jsonify({"error": "Instituicao ja cadastrada para este usuario"}), 409

    data = request.get_json() or {}
    required = ["nome_instituicao", "tipo", "endereco_completo", "telefone_contato"]
    for f in required:
        if not data.get(f):
            return jsonify({"error": f"Campo obrigatorio: {f}"}), 400

    tipo = _parse_tipo(data.get("tipo"))
    if tipo is None:
        return jsonify({"error": "tipo invalido"}), 400

    instituicao = Instituicao(
        usuario_id=user_id,
        nome_instituicao=data["nome_instituicao"],
        cnpj=data.get("cnpj"),
        tipo=tipo,
        descricao=data.get("descricao"),
        endereco_completo=data["endereco_completo"],
        bairro=data.get("bairro"),
        horario_funcionamento=data.get("horario_funcionamento"),
        telefone_contato=data["telefone_contato"],
    )
    db.session.add(instituicao)
    db.session.commit()
    return jsonify(instituicao.to_dict()), 201


@instituicoes_bp.route("", methods=["GET"])
@jwt_required()
def listar_instituicoes():
    """Lista instituicoes APROVADAS, ordenadas por nome.

    Filtro opcional: ?bairro=Centro
    """
    query = Instituicao.query.filter_by(aprovado=True)
    bairro = request.args.get("bairro")
    if bairro:
        query = query.filter(Instituicao.bairro.ilike(f"%{bairro}%"))
    instituicoes = query.order_by(Instituicao.nome_instituicao.asc()).all()
    return jsonify([i.to_dict() for i in instituicoes]), 200


@instituicoes_bp.route("/me", methods=["GET"])
@jwt_required()
def minha_instituicao():
    """Retorna a instituicao do usuario logado (se for tipo INSTITUICAO)."""
    user_id = get_jwt_identity()
    instituicao = Instituicao.query.filter_by(usuario_id=user_id).first()
    if not instituicao:
        return jsonify({"error": "Instituicao nao encontrada"}), 404
    return jsonify(instituicao.to_dict()), 200


@instituicoes_bp.route("/<instituicao_id>", methods=["GET"])
@jwt_required()
def detalhe_instituicao(instituicao_id):
    instituicao = Instituicao.query.get(instituicao_id)
    if not instituicao:
        return jsonify({"error": "Instituicao nao encontrada"}), 404
    return jsonify(instituicao.to_dict()), 200


@instituicoes_bp.route("/<instituicao_id>", methods=["PUT"])
@jwt_required()
def atualizar_instituicao(instituicao_id):
    user_id = get_jwt_identity()
    instituicao = Instituicao.query.get(instituicao_id)
    if not instituicao:
        return jsonify({"error": "Instituicao nao encontrada"}), 404
    if str(instituicao.usuario_id) != str(user_id):
        return jsonify({"error": "Sem permissao"}), 403

    data = request.get_json() or {}
    for field in (
        "nome_instituicao",
        "cnpj",
        "descricao",
        "endereco_completo",
        "bairro",
        "horario_funcionamento",
        "telefone_contato",
    ):
        if field in data:
            setattr(instituicao, field, data[field])
    if "tipo" in data:
        tipo = _parse_tipo(data["tipo"])
        if tipo is None:
            return jsonify({"error": "tipo invalido"}), 400
        instituicao.tipo = tipo

    db.session.commit()
    return jsonify(instituicao.to_dict()), 200
