"""
Pão Nosso - Backend API
Aplicação Flask principal
"""

import os

from flask import Flask, jsonify
from sqlalchemy import inspect

from config import config
from extensions import db, init_extensions
from routes import (
    auth_bp,
    doacoes_bp,
    health_bp,
    instituicoes_bp,
    solicitacoes_bp,
    stats_bp,
)

# Tabelas obrigatorias verificadas no startup. Devem refletir os modelos em
# `models/` — se voce adicionar uma nova tabela, lembre de atualizar aqui.
_REQUIRED_TABLES = ("usuarios", "instituicoes", "doacoes", "solicitacoes")


def _check_schema_health(app: Flask) -> None:
    """Garante (em DEBUG) ou avisa (em prod) que as tabelas existem.

    Evita o sintoma confuso de "500 + log so com SELECT + ROLLBACK" quando
    o desenvolvedor esquece de rodar ``python init_db.py``. Em modo
    desenvolvimento, cria silenciosamente as tabelas faltando — em
    producao, apenas loga um warning sem mexer no schema.

    Pulado em modo TESTING (cada fixture cria/destroi seu proprio schema
    in-memory).
    """
    if app.config.get("TESTING"):
        return
    try:
        with app.app_context():
            existing = set(inspect(db.engine).get_table_names())
            missing = [t for t in _REQUIRED_TABLES if t not in existing]
            if missing and app.config.get("DEBUG"):
                app.logger.info(
                    "Schema incompleto em %s: criando tabelas faltando %s "
                    "(modo DEBUG). Lembre de rodar `python scripts/seed_dev.py` "
                    "se precisar de dados de exemplo.",
                    app.config.get("SQLALCHEMY_DATABASE_URI"),
                    missing,
                )
                db.create_all()
                existing = set(inspect(db.engine).get_table_names())
                missing = [t for t in _REQUIRED_TABLES if t not in existing]
            if missing:
                app.logger.warning(
                    "Schema incompleto no banco (%s): faltam tabelas %s. "
                    "Rode `python init_db.py` antes de usar a API.",
                    app.config.get("SQLALCHEMY_DATABASE_URI"),
                    missing,
                )
    except Exception:  # pragma: no cover - defensive (DB inacessivel etc.)
        app.logger.exception("Falha ao inspecionar schema do banco")


def create_app(config_name=None):
    """Factory function para criar a aplicação Flask"""

    if config_name is None:
        config_name = os.getenv("FLASK_ENV", "development")

    app = Flask(__name__)
    app.config.from_object(config[config_name])

    # Inicializa extensões
    init_extensions(app)

    # Registra blueprints
    app.register_blueprint(health_bp)
    app.register_blueprint(auth_bp)
    app.register_blueprint(doacoes_bp)
    app.register_blueprint(solicitacoes_bp)
    app.register_blueprint(instituicoes_bp)
    app.register_blueprint(stats_bp)

    _check_schema_health(app)

    # Handler de erro global
    @app.errorhandler(404)
    def not_found(error):
        return jsonify({"error": "Endpoint não encontrado"}), 404

    @app.errorhandler(500)
    def internal_error(error):
        return jsonify({"error": "Erro interno do servidor"}), 500

    # Rota raiz
    @app.route("/")
    def index():
        return jsonify(
            {
                "message": "Bem-vindo à API Pão Nosso!",
                "version": "1.0.0",
                "endpoints": {
                    "health": "/health",
                    "auth": "/api/auth",
                    "doacoes": "/api/doacoes",
                    "solicitacoes": "/api/solicitacoes",
                    "instituicoes": "/api/instituicoes",
                    "stats": "/api/stats",
                },
            }
        )

    return app


if __name__ == "__main__":
    # _check_schema_health (chamado dentro de create_app) ja cuida de criar
    # as tabelas faltando em modo DEBUG e avisar caso contrario.
    app = create_app()

    print(f"🚀 Servidor rodando em http://{app.config['HOST']}:{app.config['PORT']}")
    app.run(host=app.config["HOST"], port=app.config["PORT"], debug=app.config["DEBUG"])
