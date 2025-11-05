"""
Extensões Flask (compartilhadas entre módulos)
"""

from flask_cors import CORS
from flask_jwt_extended import JWTManager
from flask_sqlalchemy import SQLAlchemy

# Inicializa extensões (sem app context)
db = SQLAlchemy()
jwt = JWTManager()
cors = CORS()


def init_extensions(app):
    """Inicializa as extensões com o app Flask"""
    db.init_app(app)
    jwt.init_app(app)
    cors.init_app(app, resources={r"/api/*": {"origins": app.config["CORS_ORIGINS"]}})
