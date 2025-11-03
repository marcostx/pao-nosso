"""
Model: DispositivoFCM
"""
import uuid
from datetime import datetime

from sqlalchemy import Column, DateTime, ForeignKey, String
from sqlalchemy.orm import relationship

from extensions import db


class DispositivoFCM(db.Model):
    """Modelo de dispositivo FCM para notificações push"""
    
    __tablename__ = 'dispositivos_fcm'
    
    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    usuario_id = Column(String(36), ForeignKey('usuarios.id'), nullable=False, index=True)
    token_fcm = Column(String(255), nullable=False, unique=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    
    # Relacionamentos
    usuario = relationship("Usuario", back_populates="dispositivos_fcm")
    
    def __repr__(self):
        return f'<DispositivoFCM {self.id}>'
    
    def to_dict(self):
        """Converte o modelo para dicionário"""
        return {
            'id': str(self.id),
            'usuario_id': str(self.usuario_id),
            'token_fcm': self.token_fcm,
            'created_at': self.created_at.isoformat()
        }

