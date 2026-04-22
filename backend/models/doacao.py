"""
Model: Doacao
"""

import enum
import uuid
from datetime import datetime

from sqlalchemy import Column, Date, DateTime, Enum, ForeignKey, String, Text, Time
from sqlalchemy.orm import relationship

from extensions import db


class CategoriaDoacao(enum.Enum):
    """Enum para categoria de doação"""

    # Categorias v2 (alinhadas ao mock)
    PERECIVEL = "PERECIVEL"
    NAO_PERECIVEL = "NAO_PERECIVEL"
    REFEICAO_PRONTA = "REFEICAO_PRONTA"
    HORTIFRUTI = "HORTIFRUTI"
    # Mantidas para compatibilidade retroativa
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


class JanelaEntrega(enum.Enum):
    """Janela curta de tempo escolhida pelo doador no fluxo do mock"""

    HOJE = "HOJE"
    AMANHA = "AMANHA"


class MetodoEntrega(enum.Enum):
    """Como a doação chega na instituição"""

    EU_ENTREGO = "EU_ENTREGO"  # doador leva até o ponto de coleta
    SOLICITAR_COLETA = "SOLICITAR_COLETA"  # instituição vai buscar


class Doacao(db.Model):
    """Modelo de doação"""

    __tablename__ = "doacoes"

    id = Column(String(36), primary_key=True, default=lambda: str(uuid.uuid4()))
    doador_id = Column(String(36), ForeignKey("usuarios.id"), nullable=False, index=True)
    titulo = Column(String(100), nullable=False)
    descricao = Column(Text, nullable=True)
    quantidade = Column(String(50), nullable=True)
    categoria = Column(Enum(CategoriaDoacao), nullable=False, index=True)

    # Modelo v2: janela + horário simples (mock usa HOJE/AMANHA + slot fixo)
    janela = Column(Enum(JanelaEntrega), nullable=True, index=True)
    horario = Column(Time, nullable=True)

    # Campos legados (mantidos nullable para compatibilidade)
    data_disponivel = Column(Date, nullable=True, index=True)
    hora_inicio = Column(Time, nullable=True)
    hora_fim = Column(Time, nullable=True)

    metodo_entrega = Column(Enum(MetodoEntrega), nullable=False, index=True)
    endereco_retirada = Column(String(500), nullable=True)
    bairro = Column(String(100), nullable=True, index=True)

    # Quando metodo_entrega = EU_ENTREGO, instituicao_id aponta para o ponto de coleta escolhido.
    # Quando metodo_entrega = SOLICITAR_COLETA, fica nullo até alguma instituição aceitar.
    instituicao_id = Column(
        String(36), ForeignKey("instituicoes.id"), nullable=True, index=True
    )

    status = Column(Enum(StatusDoacao), default=StatusDoacao.DISPONIVEL, nullable=False, index=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False, index=True)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)

    # Relacionamentos
    doador = relationship("Usuario", back_populates="doacoes")
    instituicao = relationship("Instituicao", back_populates="doacoes")
    solicitacoes = relationship(
        "Solicitacao", back_populates="doacao", cascade="all, delete-orphan"
    )

    def __repr__(self):
        return f"<Doacao {self.titulo} - {self.status.value}>"

    def to_dict(self):
        """Converte o modelo para dicionário"""
        return {
            "id": str(self.id),
            "doador_id": str(self.doador_id),
            "doador_nome": self.doador.nome if self.doador else None,
            "titulo": self.titulo,
            "descricao": self.descricao,
            "quantidade": self.quantidade,
            "categoria": self.categoria.value,
            "janela": self.janela.value if self.janela else None,
            "horario": self.horario.isoformat() if self.horario else None,
            "data_disponivel": (
                self.data_disponivel.isoformat() if self.data_disponivel else None
            ),
            "hora_inicio": self.hora_inicio.isoformat() if self.hora_inicio else None,
            "hora_fim": self.hora_fim.isoformat() if self.hora_fim else None,
            "metodo_entrega": self.metodo_entrega.value,
            "endereco_retirada": self.endereco_retirada,
            "bairro": self.bairro,
            "instituicao_id": str(self.instituicao_id) if self.instituicao_id else None,
            "instituicao_nome": (
                self.instituicao.nome_instituicao if self.instituicao else None
            ),
            "status": self.status.value,
            "created_at": self.created_at.isoformat(),
            "updated_at": self.updated_at.isoformat(),
        }
