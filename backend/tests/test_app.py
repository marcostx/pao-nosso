"""
Tests for Flask app configuration
"""

import pytest
from app import create_app


def test_create_app_default():
    """Test app creation with default config"""
    app = create_app()
    assert app is not None
    assert app.config["DEBUG"] is True


def test_create_app_development():
    """Test app creation with development config"""
    app = create_app("development")
    assert app is not None
    assert app.config["DEBUG"] is True


def test_create_app_production():
    """Test app creation with production config"""
    app = create_app("production")
    assert app is not None
    assert app.config["DEBUG"] is False


def test_404_error_handler():
    """Test 404 error handler"""
    app = create_app("development")
    app.config["TESTING"] = True
    client = app.test_client()
    response = client.get("/nonexistent")
    assert response.status_code == 404
    data = response.get_json()
    assert "error" in data

