"""
Seed do ambiente de desenvolvimento.

Cria:
- Doador "Maria Silva" (email maria@paonosso.dev / senha 123456)
- Tres instituicoes aprovadas (mock)
- Uma doacao RESERVADA "Cesta de Frutas" com solicitacao ACEITA na primeira instituicao
- Uma doacao DISPONIVEL "5kg de Arroz" com solicitacao PENDENTE na segunda instituicao

Uso (a partir da pasta backend/):
    python scripts/seed_dev.py
    # ou, equivalente:
    python -m scripts.seed_dev
"""

import os
import sys
from datetime import time

# Permite executar tanto `python scripts/seed_dev.py` (a partir de backend/)
# quanto `python -m scripts.seed_dev`. No primeiro caso, sys.path[0] e' a
# pasta scripts/ e o pacote `app` nao e' encontrado sem o ajuste abaixo.
_BACKEND_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _BACKEND_DIR not in sys.path:
    sys.path.insert(0, _BACKEND_DIR)

import bcrypt

from app import create_app
from extensions import db
from models import Doacao, Instituicao, Solicitacao, Usuario
from models.doacao import (
    CategoriaDoacao,
    JanelaEntrega,
    MetodoEntrega,
    StatusDoacao,
)
from models.instituicao import TipoInstituicao
from models.solicitacao import StatusSolicitacao
from models.usuario import TipoUsuario


def _hash(senha: str) -> str:
    return bcrypt.hashpw(senha.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


def _ensure_user(nome, email, tipo, telefone="11999999999", senha="123456"):
    user = Usuario.query.filter_by(email=email).first()
    if user:
        return user
    user = Usuario(
        nome=nome,
        email=email,
        senha_hash=_hash(senha),
        telefone=telefone,
        tipo=tipo,
    )
    db.session.add(user)
    db.session.flush()
    return user


def _ensure_instituicao(usuario, nome, tipo, endereco, bairro, telefone, descricao=""):
    inst = Instituicao.query.filter_by(usuario_id=usuario.id).first()
    if inst:
        return inst
    inst = Instituicao(
        usuario_id=usuario.id,
        nome_instituicao=nome,
        tipo=tipo,
        descricao=descricao,
        endereco_completo=endereco,
        bairro=bairro,
        telefone_contato=telefone,
        aprovado=True,
    )
    db.session.add(inst)
    db.session.flush()
    return inst


def seed():
    app = create_app("development")
    with app.app_context():
        # Doador
        maria = _ensure_user("Maria Silva", "maria@paonosso.dev", TipoUsuario.DOADOR)

        # Tres instituicoes
        u1 = _ensure_user("Sopa Solidaria Centro", "sopa@paonosso.dev", TipoUsuario.INSTITUICAO)
        u2 = _ensure_user("Maos que Alimentam", "maos@paonosso.dev", TipoUsuario.INSTITUICAO)
        u3 = _ensure_user(
            "Banco de Alimentos Municipal",
            "banco@paonosso.dev",
            TipoUsuario.INSTITUICAO,
        )

        i1 = _ensure_instituicao(
            u1,
            "Sopa Solidaria Centro",
            TipoInstituicao.ONG,
            "Rua Augusta, 1000",
            "Centro",
            "1133334444",
            "Distribui sopas no centro tres vezes por semana",
        )
        i2 = _ensure_instituicao(
            u2,
            "Maos que Alimentam",
            TipoInstituicao.IGREJA,
            "Av. Paulista, 1500",
            "Bela Vista",
            "1133335555",
            "Refeicoes para moradores em situacao de rua",
        )
        i3 = _ensure_instituicao(
            u3,
            "Banco de Alimentos Municipal",
            TipoInstituicao.GOVERNO,
            "Praca da Se, s/n",
            "Se",
            "1133336666",
            "Banco municipal que redistribui doacoes",
        )

        # Limpa doacoes/solicitacoes anteriores da Maria para idempotencia
        Solicitacao.query.filter(
            Solicitacao.doacao_id.in_(db.session.query(Doacao.id).filter_by(doador_id=maria.id))
        ).delete(synchronize_session=False)
        Doacao.query.filter_by(doador_id=maria.id).delete()
        db.session.flush()

        # Doacao 1: Cesta de Frutas (Eu entrego, Sopa Solidaria, ACEITA)
        d1 = Doacao(
            doador_id=maria.id,
            titulo="Cesta de Frutas",
            descricao="Mix de laranjas, bananas e macas",
            quantidade="3kg",
            categoria=CategoriaDoacao.HORTIFRUTI,
            janela=JanelaEntrega.HOJE,
            horario=time(14, 30),
            metodo_entrega=MetodoEntrega.EU_ENTREGO,
            instituicao_id=i1.id,
            endereco_retirada="Rua das Flores, 123",
            bairro="Centro",
            status=StatusDoacao.RESERVADA,
        )
        db.session.add(d1)
        db.session.flush()
        s1 = Solicitacao(
            doacao_id=d1.id,
            instituicao_id=i1.id,
            data_coleta_proposta=None,
            hora_coleta_proposta=time(14, 30),
            status=StatusSolicitacao.ACEITA,
        )
        db.session.add(s1)

        # Doacao 2: 5kg de Arroz (Solicitar Coleta, PENDENTE com Maos que Alimentam)
        d2 = Doacao(
            doador_id=maria.id,
            titulo="5kg de Arroz",
            descricao="Arroz branco tipo 1, fechado",
            quantidade="5kg",
            categoria=CategoriaDoacao.NAO_PERECIVEL,
            janela=JanelaEntrega.AMANHA,
            horario=time(10, 0),
            metodo_entrega=MetodoEntrega.SOLICITAR_COLETA,
            endereco_retirada="Ponto de Coleta B",
            bairro="Centro",
            status=StatusDoacao.DISPONIVEL,
        )
        db.session.add(d2)
        db.session.flush()
        s2 = Solicitacao(
            doacao_id=d2.id,
            instituicao_id=i2.id,
            hora_coleta_proposta=time(10, 0),
            status=StatusSolicitacao.PENDENTE,
        )
        db.session.add(s2)

        db.session.commit()

        print("Seed completo:")
        print(f"  Doador  -> {maria.email} (senha: 123456)")
        print(f"  ONGs    -> {i1.nome_instituicao}, {i2.nome_instituicao}, {i3.nome_instituicao}")
        print(f"  Doacoes -> {d1.titulo} (RESERVADA), {d2.titulo} (DISPONIVEL)")


if __name__ == "__main__":
    sys.exit(seed())
