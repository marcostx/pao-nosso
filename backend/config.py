"""
Configuração da aplicação Flask
"""

import os
from datetime import timedelta

from dotenv import load_dotenv

# Carrega variáveis de ambiente
load_dotenv()


class Config:
    """Configuração base da aplicação"""

    # Flask
    SECRET_KEY = os.getenv("JWT_SECRET_KEY", "dev-secret-key-change-in-production")
    DEBUG = os.getenv("FLASK_DEBUG", "True") == "True"

    # Database
    SQLALCHEMY_DATABASE_URI = os.getenv("DATABASE_URL", "sqlite:///paonosso.db")
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ECHO = DEBUG

    # JWT
    JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY", "dev-secret-key-change-in-production")
    JWT_ACCESS_TOKEN_EXPIRES = timedelta(days=int(os.getenv("JWT_ACCESS_TOKEN_EXPIRES", "7")))
    JWT_TOKEN_LOCATION = ["headers"]
    JWT_HEADER_NAME = "Authorization"
    JWT_HEADER_TYPE = "Bearer"

    # CORS
    CORS_ORIGINS = os.getenv("CORS_ORIGINS", "*")

    # Server
    HOST = os.getenv("HOST", "0.0.0.0")  # nosec B104
    PORT = int(os.getenv("PORT", "5000"))

    # Timezone
    TIMEZONE = os.getenv("TIMEZONE", "America/Sao_Paulo")


class DevelopmentConfig(Config):
    """Configuração de desenvolvimento"""

    DEBUG = True


class ProductionConfig(Config):
    """Configuração de produção"""

    DEBUG = False
    SQLALCHEMY_ECHO = False


# Dicionário de configurações
config = {
    "development": DevelopmentConfig,
    "production": ProductionConfig,
    "default": DevelopmentConfig,
}
