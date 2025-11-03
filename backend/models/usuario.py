"""
Model: Usuario
"""
import uuid
from datetime import datetime
from sqlalchemy import Column, String, DateTime, Enum
from sqlalchemy.orm import relationship
import enum
from extensions import db


class TipoUsuario(enum.Enum):
    """Enum para tipo de usuário"""
    DOADOR = "DOADOR"
    INSTITUICAO = "INSTITUICAO"


class Usuario(db.Model):
    """Modelo de usuário (doador ou instituição)"""
    
    __tablename__ = 'usuarios'
    
    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    nome = Column(String(100), nullable=False)
    email = Column(String(255), unique=True, nullable=False, index=True)
    senha_hash = Column(String(255), nullable=False)
    telefone = Column(String(20), nullable=False)
    tipo = Column(Enum(TipoUsuario), nullable=False, index=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    
    # Relacionamentos
    instituicao = relationship("Instituicao", back_populates="usuario", uselist=False, cascade="all, delete-orphan")
    doacoes = relationship("Doacao", back_populates="doador", cascade="all, delete-orphan")
    dispositivos_fcm = relationship("DispositivoFCM", back_populates="usuario", cascade="all, delete-orphan")
    
    def __repr__(self):
        return f'<Usuario {self.nome} ({self.email})>'
    
    def to_dict(self):
        """Converte o modelo para dicionário"""
        return {
            'id': str(self.id),
            'nome': self.nome,
            'email': self.email,
            'telefone': self.telefone,
            'tipo': self.tipo.value,
            'created_at': self.created_at.isoformat()
        }

