"""
Script para inicializar o banco de dados
"""
from app import create_app
from extensions import db


def init_database():
    """Inicializa o banco de dados"""
    app = create_app()
    
    with app.app_context():
        # Remove todas as tabelas (cuidado em produção!)
        print("🗑️  Removendo tabelas antigas...")
        db.drop_all()
        
        # Cria todas as tabelas
        print("📊 Criando tabelas...")
        db.create_all()
        
        print("✅ Banco de dados inicializado com sucesso!")
        print("\nTabelas criadas:")
        print("  - usuarios")
        print("  - instituicoes")
        print("  - doacoes")
        print("  - solicitacoes")
        print("  - dispositivos_fcm")


if __name__ == '__main__':
    init_database()

