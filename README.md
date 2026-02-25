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
* [📡 API Endpoints](#-api-endpoints)
* [🗄️ Modelo de Dados](#-modelo-de-dados)
* [💾 Scripts de Criação das Tabelas](#-scripts-de-criação-das-tabelas)
* [🧪 Testes](#-testes)
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
- Spring JDBC
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
   - `Docker` instalado e executando.
   - _Portas_ livres:
     - `5432` -> PostgreSQL
     - `8080` -> Backend API

### Instruções
1. **Subir** a aplicação:
    ```bash
    docker compose up --build -d
    ```
   - API disponível em: `http://localhost:8080/`

2. **Parar** a aplicação:

   - Parar containers:
     ```bash
     docker compose stop
     ```
  
   - Remover containers:
     ```bash
     docker compose down 
     ```
   
3. Resetar containers (deleta o banco de dados):
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
- Serviços configurados no `docker-compose.yml`:
  - `postgres` -> postgres:18
  - `backend-tests` -> Container para execução isolada de testes unitários.
  - `backend` -> Container principal da API

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
│  │  ├── api/  # Exceções de API
│  │  ├── domain/ # Exceções de domínio
│  │  └── GlobalExceptionHandler.java # Tratamento global de exceções
│  ├─ model/ # Entidades
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

## 📡 API Endpoints

### Customer:

| **Método** | **Endpoint**             | **Descrição**           | **Retorno**                                   |
|:----------:|--------------------------|-------------------------|-----------------------------------------------|
|   _POST_   | `/customers`             | Cria um novo cliente    | 201 + dados do cliente ou 400 em caso de erro |
|   _GET_    | `/customers/{id}`        | Retorna cliente por ID  | 200 ou 404                                    |
|   _GET_    | `/customers?name=<nome>` | Busca clientes por nome | Lista de clientes                             |
|   _GET_    | `/customers`             | Lista todos os clientes | Lista de clientes                             |

### Observações:
- Mensagens de erro padronizadas em português
- Endpoints de leitura (GET) não dependem de payload
- Para testes automatizados, MockMvc e GlobalExceptionHandler são utilizados para validação de respostas

---

## 🗄️ Modelo de Dados

### Relacionamentos
| **Entidade** | **Cardinalidade** | **Entidade** |
|:------------:|:-----------------:|:------------:|
|  customers   |        1:N        |    orders    |
|    orders    |        1:N        | order_items  |
|   products   |        1:N        | order_items  | 

### Tabelas
`customers`: id, name, email, created_at  
`products`: id, description, price, stock_quantity, created_at  
`orders`: id, customer_id, order_date, total_amount  
`order_items`: id, order_id, product_id, quantity, unit_price, discount, total_price

`Índice:` _idx_order_customer_id_ em _orders(customer_id)_

---

## 💾 Scripts de Criação das Tabelas
Os scripts abaixo foram aplicados via `Flyway` em versões:

### V1__init.sql:

```sql
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    price NUMERIC(12,2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount NUMERIC(12,2) NOT NULL
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    discount NUMERIC(12,2) NOT NULL,
    total_price NUMERIC(12,2) NOT NULL
);

CREATE INDEX idx_order_customer_id ON orders(customer_id);
```

### V2__change_timestamp_to_timestamptz.sql:
Converte timestamps para timestamptz garantindo armazenamento consistente e preciso com fuso horário.

```sql
ALTER TABLE customers
ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE
    USING created_at AT TIME ZONE 'UTC';

ALTER TABLE customers
    ALTER COLUMN created_at DROP DEFAULT;


ALTER TABLE products
ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE
    USING created_at AT TIME ZONE 'UTC';

ALTER TABLE products
    ALTER COLUMN created_at DROP DEFAULT;


ALTER TABLE orders
ALTER COLUMN order_date TYPE TIMESTAMP WITH TIME ZONE
    USING order_date AT TIME ZONE 'UTC';

ALTER TABLE orders
    ALTER COLUMN order_date DROP DEFAULT;
```

---

## 🧪 Testes
### Testes Unitários:
O backend possui **testes unitários** implementados, garantindo cobertura para as entidades do sistema, incluindo validações de domínio, regras de negócio e endpoints REST.

#### Customer:
- `Customer (Model)`:
  - Criação de clientes válidos
  - Validação de nome:
    - Não pode ser nulo ou vazio
    - Apenas letras e espaços únicos
    - Tamanho máximo permitido
  - Validação de email:
    - Não pode ser nulo ou vazio
    - Formato válido
    - Tamanho máximo permitido
  - Regras de mutabilidade do ID (não pode ser alterado após definido)


- `CustomerService`:
  - Criação de clientes com dados válidos
  - Criação com dados inválidos dispara `DomainValidationException`
  - Busca por ID:
    - Retorna cliente existente
    - Dispara `ResourceNotFoundException` para ID inexistente
  - Busca por nome:
    - Retorna lista de clientes correspondentes
    - Retorna lista vazia quando não há correspondência
  - Listagem de todos os clientes


- `CustomerController`: endpoints REST de POST e GET, incluindo:
  - `POST /customers`:
    - Criação de cliente válido retorna `201` com dados do cliente
    - Dados inválidos retornam `400` com mensagem de erro adequada
  - `GET /customers/{id}`:
    - Cliente existente retorna `200` com dados do cliente
    - Cliente inexistente retorna `404` com mensagem de erro
  - `GET /customers?name=<nome>`:
    - Retorna lista de clientes correspondentes
    - Retorna lista vazia se não houver correspondência
  - `GET /customers`:
    - Retorna lista de todos os clientes
    - Retorna lista vazia se não houver clientes cadastrados

#### Observações
- Testes de Controller utilizam `MockMvc` e `GlobalExceptionHandler`
- Testes de serviço e modelo são isolados e não dependem de banco de dados

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


- As _operações de persistência_ são feitas com SQL explícito via `JdbcTemplate`, garantindo total controle sobre as queries e cumprimento do requisito técnico.


- `Lombok`: Uso de lombok para redução de código boilerplate, em prol de maior legibilidade e alinhamento com a convenção da stack.

### Banco de dados
- `Flyway`: versionamento de banco de dados automatizado para garantir reprodutibilidade do banco e manter histórico auditável.


- `BIGSERIAL vs UUID`: optou-se por `BIGSERIAL` (autoincrement) por simplicidade, melhor desempenho e adequação ao escopo (sistema single-database).


- Para evitar conflito com a palavra reservada `ORDER` no SQL, optei por usar nomes de tabelas no **plural** (ex.: `orders`), garantindo uniformidade e evitando problemas de sintaxe. 


- Optei por `TIMESTAMP WITH TIME ZONE` para armazenar datas mais precisa (UTC), evitando ambiguidades de fuso horário. E removi o `DEFAULT CURRENT_TIMESTAMP`, definindo o valor de criação unicamente no escopo da aplicação.

---

## 👤 Autor

Desenvolvido por **Manoel Alves** como desafio técnico para o processo
seletivo da [SergipeTec](https://sergipetec.org.br).