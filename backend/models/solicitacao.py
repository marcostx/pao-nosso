"""
Model: Solicitacao
"""
import enum
import uuid
from datetime import datetime

from sqlalchemy import Column, Date, DateTime, Enum, ForeignKey, String, Text, Time
from sqlalchemy.orm import relationship

from extensions import db


class StatusSolicitacao(enum.Enum):
    """Enum para status de solicitação"""
    PENDENTE = "PENDENTE"
    ACEITA = "ACEITA"
    RECUSADA = "RECUSADA"
    CONCLUIDA = "CONCLUIDA"


class Solicitacao(db.Model):
    """Modelo de solicitação de coleta"""
    
    __tablename__ = 'solicitacoes'
    
    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    doacao_id = Column(String(36), ForeignKey('doacoes.id'), nullable=False, index=True)
    instituicao_id = Column(String(36), ForeignKey('instituicoes.id'), nullable=False, index=True)
    data_coleta_proposta = Column(Date, nullable=False)
    hora_coleta_proposta = Column(Time, nullable=False)
    observacoes = Column(Text, nullable=True)
    status = Column(Enum(StatusSolicitacao), default=StatusSolicitacao.PENDENTE, nullable=False, index=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False, index=True)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)
    
    # Relacionamentos
    doacao = relationship("Doacao", back_populates="solicitacoes")
    instituicao = relationship("Instituicao", back_populates="solicitacoes")
    
    def __repr__(self):
        return f'<Solicitacao {self.id} - {self.status.value}>'
    
    def to_dict(self):
        """Converte o modelo para dicionário"""
        return {
            'id': str(self.id),
            'doacao_id': str(self.doacao_id),
            'doacao': self.doacao.to_dict() if self.doacao else None,
            'instituicao_id': str(self.instituicao_id),
            'instituicao': self.instituicao.to_dict() if self.instituicao else None,
            'data_coleta_proposta': self.data_coleta_proposta.isoformat() if self.data_coleta_proposta else None,
            'hora_coleta_proposta': self.hora_coleta_proposta.isoformat() if self.hora_coleta_proposta else None,
            'observacoes': self.observacoes,
            'status': self.status.value,
            'created_at': self.created_at.isoformat(),
            'updated_at': self.updated_at.isoformat()
        }

