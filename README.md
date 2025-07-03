# Java Prime - Desafio Técnico

## 📋 Descrição do Projeto

Este projeto é uma aplicação web para gerenciamento de clientes e contratos, desenvolvida como parte do desafio técnico.  
Permite cadastrar clientes, vincular contratos a eles e consultar informações de forma simples e eficiente, aplicando boas práticas de arquitetura, código limpo e padrões de projeto.

---

## 🔧 Tecnologias Utilizadas

- [Java 21](https://www.oracle.com/java/)
- [Spring Boot 3](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Liquibase](https://www.liquibase.org/)
- [MySQL](https://www.mysql.com/)
- [Docker & Docker Compose](https://www.docker.com/)
- [JUnit 5](https://junit.org/junit5/)
- [Lombok](https://projectlombok.org/)
- [Angular 15+ (desenvolvimento será feito futuramente)](https://angular.io/)

---

## 🛠 Como Rodar o Projeto

### Backend
### 1. Clone o repositório
  ```bash
  git clone https://github.com/Caio-Henrique3/DesafioJavaPrime
  ```
### 2. Instale e rode o Docker.
### 3. No diretório do backend, execute:
   ```bash
   docker-compose up -d db
   ```
### 4. Configure o banco (usuário, senha e porta) no application.yml.

### 5. Rode a aplicação Spring Boot pela IDE ou:
   ```bash
    mvn spring-boot:run
   ```
O Liquibase aplicará as migrations automaticamente no banco.

---

### 🏛 Arquitetura e Design
    Arquitetura em camadas (Controller, Service, Repository).

    Uso de UUID para identificação única dos clientes e contratos.

    Migrations controladas via Liquibase, garantindo versionamento do schema do banco.

    Código limpo, aplicando SOLID e design patterns básicos (Repository, DTOs).

    Containerização com Docker para facilitar execução e ambiente consistente.

### ✨ Funcionalidades Implementadas
    Cadastro e gerenciamento de clientes (nome, documento, email, telefone).

    Cadastro de contratos vinculados aos clientes.

    Consulta de contratos por cliente.

    Integração simulada para consulta de score de crédito (mock/stub).

    API RESTful documentada e testada.

### 🚧 Pontos Diferenciais
    Uso do Liquibase para migrations versionadas.

    Implementação com UUID para IDs, pensando em escalabilidade e segurança.

    Docker Compose para subir banco MySQL e backend com facilidade.

    Cobertura de testes unitários com JUnit.

    Código totalmente escrito em inglês para facilitar manutenção internacional.

    Preparação para CI/CD (GitHub Actions pipeline configurável).

### 🚀 Próximos Passos e Melhorias
    Finalizar frontend Angular.

    Implementar testes de integração e de API.

    Adicionar autenticação e controle de acesso.

    Documentar API com Swagger/OpenAPI.

    Automatizar pipeline CI/CD para build, testes e deploy.

### 📞 Contato
[![LinkedIn](https://img.shields.io/badge/LinkedIn--blue?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/caio-henrique-56b713200)
[![Email](https://img.shields.io/badge/Email--red?style=for-the-badge&logo=gmail&logoColor=white)](mailto:m.henrique.caio@gmail.com)

---

## Obrigado por avaliar meu projeto! Qualquer dúvida, fico à disposição para conversar.
