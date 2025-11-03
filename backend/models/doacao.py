"""
Model: Doacao
"""
import enum
import uuid
from datetime import datetime

from sqlalchemy import Column, Date, DateTime, Enum, ForeignKey, Numeric, String, Text, Time
from sqlalchemy.orm import relationship

from extensions import db


class CategoriaDoacao(enum.Enum):
    """Enum para categoria de doação"""
    FRUTAS = "FRUTAS"
    LEGUMES = "LEGUMES"
    GRAOS = "GRAOS"
    LATICINIOS = "LATICINIOS"
    OUTROS = "OUTROS"


class StatusDoacao(enum.Enum):
    """Enum para status de doação"""
    DISPONIVEL = "DISPONIVEL"
    RESERVADA = "RESERVADA"
    COLETADA = "COLETADA"
    CANCELADA = "CANCELADA"


class Doacao(db.Model):
    """Modelo de doação"""
    
    __tablename__ = 'doacoes'
    
    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    doador_id = Column(String(36), ForeignKey('usuarios.id'), nullable=False, index=True)
    titulo = Column(String(100), nullable=False)
    descricao = Column(Text, nullable=True)
    quantidade = Column(String(50), nullable=False)
    categoria = Column(Enum(CategoriaDoacao), nullable=False, index=True)
    data_disponivel = Column(Date, nullable=False, index=True)
    hora_inicio = Column(Time, nullable=False)
    hora_fim = Column(Time, nullable=False)
    endereco_retirada = Column(String(500), nullable=False)
    latitude = Column(Numeric(10, 7), nullable=False, index=True)
    longitude = Column(Numeric(10, 7), nullable=False, index=True)
    status = Column(Enum(StatusDoacao), default=StatusDoacao.DISPONIVEL, nullable=False, index=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False, index=True)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)
    
    # Relacionamentos
    doador = relationship("Usuario", back_populates="doacoes")
    solicitacoes = relationship("Solicitacao", back_populates="doacao", cascade="all, delete-orphan")
    
    def __repr__(self):
        return f'<Doacao {self.titulo} - {self.status.value}>'
    
    def to_dict(self):
        """Converte o modelo para dicionário"""
        return {
            'id': str(self.id),
            'doador_id': str(self.doador_id),
            'doador_nome': self.doador.nome if self.doador else None,
            'titulo': self.titulo,
            'descricao': self.descricao,
            'quantidade': self.quantidade,
            'categoria': self.categoria.value,
            'data_disponivel': self.data_disponivel.isoformat() if self.data_disponivel else None,
            'hora_inicio': self.hora_inicio.isoformat() if self.hora_inicio else None,
            'hora_fim': self.hora_fim.isoformat() if self.hora_fim else None,
            'endereco_retirada': self.endereco_retirada,
            'latitude': float(self.latitude) if self.latitude else None,
            'longitude': float(self.longitude) if self.longitude else None,
            'status': self.status.value,
            'created_at': self.created_at.isoformat(),
            'updated_at': self.updated_at.isoformat()
        }

