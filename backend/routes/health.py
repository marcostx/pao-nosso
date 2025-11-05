"""
Health check routes
"""

from datetime import datetime

from flask import Blueprint, jsonify

health_bp = Blueprint("health", __name__)


@health_bp.route("/health", methods=["GET"])
def health_check():
    """Health check endpoint"""
    return (
        jsonify(
            {
                "status": "ok",
                "message": "Pão Nosso API está funcionando!",
                "timestamp": datetime.utcnow().isoformat(),
                "version": "1.0.0",
            }
        ),
        200,
    )


@health_bp.route("/ping", methods=["GET"])
def ping():
    """Ping endpoint"""
    return jsonify({"message": "pong"}), 200
