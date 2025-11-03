"""
Model: Instituicao
"""
import enum
import uuid
from datetime import datetime

from sqlalchemy import Boolean, Column, DateTime, Enum, ForeignKey, Numeric, String, Text
from sqlalchemy.orm import relationship

from extensions import db


class TipoInstituicao(enum.Enum):
    """Enum para tipo de instituição"""
    COZINHA_SOLIDARIA = "COZINHA_SOLIDARIA"
    ABRIGO = "ABRIGO"
    ONG = "ONG"
    OUTRO = "OUTRO"


class Instituicao(db.Model):
    """Modelo de instituição"""
    
    __tablename__ = 'instituicoes'
    
    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    usuario_id = Column(String(36), ForeignKey('usuarios.id'), nullable=False, unique=True)
    nome_instituicao = Column(String(200), nullable=False)
    cnpj = Column(String(18), unique=True, nullable=True)
    tipo = Column(Enum(TipoInstituicao), nullable=False)
    descricao = Column(Text, nullable=True)
    endereco_completo = Column(String(500), nullable=False)
    latitude = Column(Numeric(10, 7), nullable=False, index=True)
    longitude = Column(Numeric(10, 7), nullable=False, index=True)
    horario_funcionamento = Column(String(200), nullable=True)
    telefone_contato = Column(String(20), nullable=False)
    aprovado = Column(Boolean, default=False, nullable=False, index=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    
    # Relacionamentos
    usuario = relationship("Usuario", back_populates="instituicao")
    solicitacoes = relationship("Solicitacao", back_populates="instituicao", cascade="all, delete-orphan")
    
    def __repr__(self):
        return f'<Instituicao {self.nome_instituicao}>'
    
    def to_dict(self):
        """Converte o modelo para dicionário"""
        return {
            'id': str(self.id),
            'usuario_id': str(self.usuario_id),
            'nome_instituicao': self.nome_instituicao,
            'cnpj': self.cnpj,
            'tipo': self.tipo.value,
            'descricao': self.descricao,
            'endereco_completo': self.endereco_completo,
            'latitude': float(self.latitude) if self.latitude else None,
            'longitude': float(self.longitude) if self.longitude else None,
            'horario_funcionamento': self.horario_funcionamento,
            'telefone_contato': self.telefone_contato,
            'aprovado': self.aprovado,
            'created_at': self.created_at.isoformat()
        }

