"""
Routes package
"""

from .auth import auth_bp
from .doacoes import doacoes_bp
from .health import health_bp
from .instituicoes import instituicoes_bp
from .solicitacoes import solicitacoes_bp
from .stats import stats_bp

__all__ = [
    "auth_bp",
    "doacoes_bp",
    "health_bp",
    "instituicoes_bp",
    "solicitacoes_bp",
    "stats_bp",
]
