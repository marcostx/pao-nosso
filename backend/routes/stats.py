"""
Stats routes (used by Home header and Profile cards).
"""

from flask import Blueprint, jsonify
from flask_jwt_extended import get_jwt_identity, jwt_required

from models import Usuario
from services.stats_service import stats_for_user

stats_bp = Blueprint("stats", __name__, url_prefix="/api/stats")


@stats_bp.route("/me", methods=["GET"])
@jwt_required()
def me():
    user_id = get_jwt_identity()
    usuario = Usuario.query.get(user_id)
    if not usuario:
        return jsonify({"error": "Usuario nao encontrado"}), 404
    return jsonify(stats_for_user(usuario)), 200
