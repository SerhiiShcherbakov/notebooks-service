# Notebooks Service

## Features
- Notebook creation and management
- Tagging system for notebooks
- Integration with RabbitMQ for messaging
- Transactional outbox for reliable notebook and tag event publishing
- MySQL database for data storage
- Scheduled operations to remove archived notebooks

## Technology Stack
- **Spring Boot**
- **Spring JDBC**
- **Flyway**
- **MySQL**
- **RabbitMQ**
- **Swagger/OpenAPI**
- **Actuator**, **Prometheus**
- **Docker**
- **Database-rider**
- **TestContainers**

## Getting Started
Start the required infrastructure using Docker Compose
``` bash
docker compose up -d
```
Build
``` bash
./scripts/docker-build.sh
```

Run
``` bash
./scripts/docker-run.sh
```