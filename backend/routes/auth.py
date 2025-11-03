"""
Authentication routes
"""
from flask import Blueprint, request, jsonify
from flask_jwt_extended import create_access_token, jwt_required, get_jwt_identity
from extensions import db
from models import Usuario
from models.usuario import TipoUsuario
from utils.validators import validate_email, validate_senha
import bcrypt

auth_bp = Blueprint('auth', __name__, url_prefix='/api/auth')


@auth_bp.route('/register', methods=['POST'])
def register():
    """Registra um novo usuário"""
    try:
        data = request.get_json()
        
        # Validações básicas
        required_fields = ['nome', 'email', 'senha', 'telefone', 'tipo']
        for field in required_fields:
            if field not in data:
                return jsonify({'error': f'Campo obrigatório: {field}'}), 400
        
        # Valida email
        if not validate_email(data['email']):
            return jsonify({'error': 'Email inválido'}), 400
        
        # Valida senha
        senha_valida, msg_erro = validate_senha(data['senha'])
        if not senha_valida:
            return jsonify({'error': msg_erro}), 400
        
        # Verifica se email já existe
        if Usuario.query.filter_by(email=data['email']).first():
            return jsonify({'error': 'Email já cadastrado'}), 409
        
        # Valida tipo de usuário
        try:
            tipo_usuario = TipoUsuario[data['tipo']]
        except KeyError:
            return jsonify({'error': 'Tipo de usuário inválido'}), 400
        
        # Hash da senha
        senha_hash = bcrypt.hashpw(data['senha'].encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        
        # Cria novo usuário
        novo_usuario = Usuario(
            nome=data['nome'],
            email=data['email'].lower(),
            senha_hash=senha_hash,
            telefone=data['telefone'],
            tipo=tipo_usuario
        )
        
        db.session.add(novo_usuario)
        db.session.commit()
        
        # Gera token JWT
        access_token = create_access_token(identity=str(novo_usuario.id))
        
        return jsonify({
            'message': 'Usuário criado com sucesso',
            'user_id': str(novo_usuario.id),
            'tipo': tipo_usuario.value,
            'access_token': access_token
        }), 201
        
    except Exception as e:
        db.session.rollback()
        return jsonify({'error': f'Erro ao criar usuário: {str(e)}'}), 500


@auth_bp.route('/login', methods=['POST'])
def login():
    """Realiza login do usuário"""
    try:
        data = request.get_json()
        
        # Validações básicas
        if not data.get('email') or not data.get('senha'):
            return jsonify({'error': 'Email e senha são obrigatórios'}), 400
        
        # Busca usuário
        usuario = Usuario.query.filter_by(email=data['email'].lower()).first()
        
        if not usuario:
            return jsonify({'error': 'Email ou senha incorretos'}), 401
        
        # Verifica senha
        if not bcrypt.checkpw(data['senha'].encode('utf-8'), usuario.senha_hash.encode('utf-8')):
            return jsonify({'error': 'Email ou senha incorretos'}), 401
        
        # Gera token JWT
        access_token = create_access_token(identity=str(usuario.id))
        
        return jsonify({
            'message': 'Login realizado com sucesso',
            'user_id': str(usuario.id),
            'nome': usuario.nome,
            'email': usuario.email,
            'tipo': usuario.tipo.value,
            'access_token': access_token
        }), 200
        
    except Exception as e:
        return jsonify({'error': f'Erro ao fazer login: {str(e)}'}), 500


@auth_bp.route('/me', methods=['GET'])
@jwt_required()
def get_current_user():
    """Retorna informações do usuário autenticado"""
    try:
        current_user_id = get_jwt_identity()
        usuario = Usuario.query.get(current_user_id)
        
        if not usuario:
            return jsonify({'error': 'Usuário não encontrado'}), 404
        
        return jsonify(usuario.to_dict()), 200
        
    except Exception as e:
        return jsonify({'error': f'Erro ao buscar usuário: {str(e)}'}), 500


@auth_bp.route('/logout', methods=['POST'])
@jwt_required()
def logout():
    """Realiza logout (apenas retorna mensagem de sucesso no MVP)"""
    return jsonify({'message': 'Logout realizado com sucesso'}), 200

