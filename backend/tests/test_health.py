"""
Tests for health endpoints
"""

import pytest
from app import create_app


@pytest.fixture
def app():
    """Create application for testing"""
    app = create_app("development")
    app.config["TESTING"] = True
    app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///:memory:"
    app.config["JWT_SECRET_KEY"] = "test-secret-key"
    return app


@pytest.fixture
def client(app):
    """Create test client"""
    with app.test_client() as client:
        with app.app_context():
            from extensions import db

            db.create_all()
        yield client
        with app.app_context():
            db.drop_all()


def test_health_check(client):
    """Test health check endpoint"""
    response = client.get("/health")
    assert response.status_code == 200
    data = response.get_json()
    assert data["status"] == "ok"
    assert "timestamp" in data
    assert data["version"] == "1.0.0"


def test_ping(client):
    """Test ping endpoint"""
    response = client.get("/ping")
    assert response.status_code == 200
    data = response.get_json()
    assert data["message"] == "pong"


def test_root_endpoint(client):
    """Test root endpoint"""
    response = client.get("/")
    assert response.status_code == 200
    data = response.get_json()
    assert "message" in data
    assert "version" in data
    assert "endpoints" in data

