"""
Validadores de dados
"""

import re


def validate_email(email):
    """Valida formato de email"""
    pattern = r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
    return re.match(pattern, email) is not None


def validate_senha(senha):
    """
    Valida senha (mínimo 6 caracteres para MVP)
    Retorna (bool, mensagem_erro)
    """
    if len(senha) < 6:
        return False, "Senha deve ter no mínimo 6 caracteres"

    return True, None


def validate_telefone(telefone):
    """Valida formato de telefone brasileiro"""
    # Remove caracteres não numéricos
    telefone_limpo = re.sub(r"\D", "", telefone)

    # Aceita 10 ou 11 dígitos (com ou sem 9 no celular)
    if len(telefone_limpo) not in [10, 11]:
        return False

    return True


def validate_cnpj(cnpj):
    """Valida formato de CNPJ (básico para MVP)"""
    if not cnpj:
        return True  # CNPJ é opcional

    # Remove caracteres não numéricos
    cnpj_limpo = re.sub(r"\D", "", cnpj)

    # Verifica se tem 14 dígitos
    return len(cnpj_limpo) == 14


def validate_coordinates(latitude, longitude):
    """Valida coordenadas geográficas"""
    try:
        lat = float(latitude)
        lng = float(longitude)

        # Brasil aproximadamente: lat -33 a 5, lng -74 a -34
        if -90 <= lat <= 90 and -180 <= lng <= 180:
            return True

        return False
    except (ValueError, TypeError):
        return False
