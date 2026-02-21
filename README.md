# 📦 Sistema de Gerenciamento de Pedidos

*Desafio técnico desenvolvido para o processo seletivo da **SergipeTec**.*

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen?style=for-the-badge)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue?style=for-the-badge)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge)](https://www.docker.com/)

---

## 🗃️ ÍNDICE
* [📋 Sobre](#-sobre)
* [🛠️ Tecnologias](#-tecnologias)
* [🚀 Como Executar](#-como-executar)
* [🏗️ Arquitetura](#-arquitetura)
* [📁 Estrutura do Projeto](#-estrutura-do-projeto)
* [🗄️ Modelo de Dados](#-modelo-de-dados)
* [💡 Decisões Técnicas](#-decisões-técnicas)
* [👤 Autor](#-autor)

---

## 📋 Sobre

Sistema de _gerenciamento de clientes, produtos e pedidos_, desenvolvido como desafio técnico para a vaga de **Analista de Sistemas de Computação** na [SergipeTec](https://sergipetec.org.br).

---

## 🛠️ Tecnologias

### Backend:
|    **Componente**     | **Versão** |
|:---------------------:|:----------:|
|         Java          |     21     |
|      Spring Boot      |   4.0.2    |
|         Maven         |  Wrapper   |

#### Dependências:
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Flyway Migration
- Spring Validation
- Spring Boot DevTools
- Lombok

### Frontend:
- Stack tecnológica será definida em etapa posterior.

### Banco de Dados:
| **Componente** | **Versão** |
|:--------------:|:----------:|
|   PostgreSQL   |     18     |

### Infraestrutura:
- Docker Compose 
  - PostgreSQL:18 (full-image)
  - Container backend: build multi-estágio (Maven + Eclipse Temurin JDK 21)

---

## 🚀 Como Executar

### Pré-requisitos
   - `Docker` instalado.
   - As seguintes _portas_ livres:
     - `5432` -> PostgreSQL
     - `8080` -> API

### Instruções
1. Subindo a aplicação:
    ```bash
    docker compose up --build
    ```
   - API disponível em: `http://localhost:8080`
   
   
2. Parando a aplicação:
- Parar a execução: pressione `CTRL + C`
- Para remover containers e redes criadas:
    ```bash
    docker compose down 
    ```
   
- Para remover volumes criados (deleta banco de dados):
    ```bash
     docker compose down -v
     ```

---

## 🏗️ Arquitetura
O projeto utiliza a abordagem `Monorepo`, centralizando **backend** e **frontend** em um mesmo repositório.

### Backend 
#### Arquitetura em Camadas:
```
Controller -> Service -> Repository
```

#### Responsabilidades:
- `Controller` → Exposição de endpoints REST
- `Service` → Regras de negócio
- `Repository` → Acesso ao banco via Native Queries
- `DTO` → Padronização de entrada e saída
- `Exception` → Tratamento global de erros

### Frontend
- Planejado para ser implementado como **SPA (Single Page Application)** baseada em separação de responsabilidades: `Pages → Components → Services`

### Infraestrutura
- Containerização via Docker
- Orquestração dos serviços com Docker Compose

---

## 📁 Estrutura do Projeto

### Monorepo:
```bash
order-management-system/ # Raiz do projeto (monorepo)
├─ backend/ # Aplicação Spring Boot (API REST)
├─ frontend/ # Interface do usuário (planejado)
├─ docker-compose.yml # Orquestra o backend e o banco via containers
├─ .gitignore # Ignores gerais
└─ README.md # Documentação do Projeto
```

### Backend:
```bash
backend/ 
├─ src/main/java/br/com/manoel/ordermanagement/
│  ├─ OrderManagementSystemApplication.java # Ponto de entrada do Spring Boot
│  ├─ config/ # Configurações gerais
│  ├─ controller/ # Endpoints REST
│  ├─ dto/ # Padronização de Requests e Responses
│  │  ├── request/  # DTOs de entrada
│  │  └── response/ # DTOs de saída
│  ├─ exception/ # Tratamento de exceções
│  ├─ model/ # Entidades JPA
│  ├─ repository/ # Acesso a dados (Native Queries)
│  └─ service/ # Lógica de negócio
├─ src/main/resources/
│  ├─ db.migration/ # migrations do flyway
│  └─ application.yml # Configurações do Spring Boot 
├─ src/test/java/ # Testes unitários e de integração (planejados)
├─ .gitignore # Ignores específicos do backend
├─ Dockerfile # instruções do container backend
└─ pom.xml # Gerenciamento de dependências e build
```

### frontend
- Será adicionado posteriormente.

---

## 🗄️ Modelo de Dados

### Relacionamentos
| **Entidade** | **Cardinalidade** | **Entidade** |
|:------------:|:-----------------:|:------------:|
|  customers   |        1:N        |    orders    |
|    orders    |        1:N        | order_items  |
|   products   |        1:N        | order_items  | 

### Tabelas

**customers**:
- `id` (BIGSERIAL, PK)
- `name` (VARCHAR, NOT NULL)
- `email` (VARCHAR, UNIQUE, NOT NULL)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**products**:
- `id` (BIGSERIAL, PK)
- `description` (VARCHAR, NOT NULL)
- `price` (NUMERIC(12,2), NOT NULL)
- `stock_quantity` (INTEGER, NOT NULL)
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

**orders**:
- `id` (BIGSERIAL, PK)
- `customer_id` (BIGINT, FK → customers(id))
- `order_date` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
- `total_amount` (NUMERIC(12,2), NOT NULL)

**order_items**:
- `id` (BIGSERIAL, PK)
- `order_id` (BIGINT, FK → orders(id))
- `product_id` (BIGINT, FK → products(id))
- `quantity` (INTEGER, NOT NULL)
- `unit_price` (NUMERIC(12,2), NOT NULL)
- `discount` (NUMERIC(12,2), NOT NULL)
- `total_price` (NUMERIC(12,2), NOT NULL)


**Índice**: `idx_order_customer_id` em `orders(customer_id)` para otimizar consultas por cliente.

---

## 💡 Decisões Técnicas

### Gerais:
- `Monorepo`: centraliza backend e frontend, simplificando builds e versionamento, se adequando ao escopo simples do projeto.


- Containerização via `Docker Compose`: garante que a aplicação possa ser reproduzida facilmente em qualquer ambiente, de maneira consistente, independente do ambiente de execução.


- `Padronização de Idioma`:
   - O **código** será escrito em _inglês_ para garantir clareza semântica e melhor legibilidade.
   - As **mensagens de Commit** serão escritas em _inglês_ para garantir alinhamento com o código e conformidade com o padrão Conventional Commits.
   - A **documentação** será mantida em _português_, considerando o público avaliador.
   - **Mensagens** de validação e erro serão em _português_ a fim de se adequar ao público-alvo primário.
   - **Internacionalização (i18n)** não será implementada devido ao escopo e prazo do projeto.


- `Padrão de Commits`: será usado o padrão **conventional commits** para garantir organização, clareza e uniformidade. 

### Backend:
- **arquitetura em camadas** (Controller → Service → Repository) para garantir separação de responsabilidades e facilitar manutenção.


- Uso de `DTOs`: para evitar acoplamento direto ao modelo de persistência e permitir maior controle sobre dados expostos.


- **Native queries**: adotadas conforme exigência do desafio, permitindo maior controle sobre as consultas SQL e compreensão explícita das operações no banco de dados.


- `Lombok`: Uso de lombok para redução de código boilerplate, em prol de maior legibilidade e alinhamento com a convenção da stack.

### Banco de dados
- `Flyway`: versionamento de banco de dados automatizado para garantir reprodutibilidade do banco e manter histórico auditável.


- `BIGSERIAL vs UUID`: optou-se por `BIGSERIAL` (autoincrement) por simplicidade, melhor desempenho e adequação ao escopo (sistema single-database).


- Para evitar conflito com a palavra reservada `ORDER` no SQL, optei por usar nomes de tabelas no **plural** (ex.: `orders`), garantindo uniformidade e evitando problemas de sintaxe. 

---

## 👤 Autor

Desenvolvido por **Manoel Alves** como desafio técnico para o processo
seletivo da [SergipeTec](https://sergipetec.org.br).