"""
Pão Nosso - Backend API
Aplicação Flask principal
"""
from flask import Flask, jsonify
from config import config
from extensions import init_extensions, db
from routes import auth_bp, health_bp
import os


def create_app(config_name=None):
    """Factory function para criar a aplicação Flask"""
    
    if config_name is None:
        config_name = os.getenv('FLASK_ENV', 'development')
    
    app = Flask(__name__)
    app.config.from_object(config[config_name])
    
    # Inicializa extensões
    init_extensions(app)
    
    # Registra blueprints
    app.register_blueprint(health_bp)
    app.register_blueprint(auth_bp)
    
    # Handler de erro global
    @app.errorhandler(404)
    def not_found(error):
        return jsonify({'error': 'Endpoint não encontrado'}), 404
    
    @app.errorhandler(500)
    def internal_error(error):
        return jsonify({'error': 'Erro interno do servidor'}), 500
    
    # Rota raiz
    @app.route('/')
    def index():
        return jsonify({
            'message': 'Bem-vindo à API Pão Nosso!',
            'version': '1.0.0',
            'endpoints': {
                'health': '/health',
                'auth': '/api/auth',
            }
        })
    
    return app


if __name__ == '__main__':
    app = create_app()
    
    # Cria tabelas do banco de dados se não existirem
    with app.app_context():
        db.create_all()
        print("✅ Banco de dados inicializado!")
    
    # Inicia servidor
    print(f"🚀 Servidor rodando em http://{app.config['HOST']}:{app.config['PORT']}")
    app.run(
        host=app.config['HOST'],
        port=app.config['PORT'],
        debug=app.config['DEBUG']
    )

