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

Sistema para gerenciamento de **clientes**, **produtos** e **pedidos**, desenvolvido como desafio técnico para a vaga de **Analista de Sistemas de Computação** na [SergipeTec](https://sergipetec.org.br).

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
- Lombok

### Frontend:
- React + Vite
- Javascript
- Bootstrap
- Nginx (container)

### Banco de Dados:
| **Componente** | **Versão** |
|:--------------:|:----------:|
|   PostgreSQL   |     18     |

- Executado em container via Docker Compose

### Infraestrutura:
- Docker
- Docker Compose 
  - PostgreSQL:18 (full-image)
  - Container backend: build multi-estágio (Maven + Eclipse Temurin JDK 21)

---

## 🚀 Como Executar

### Pré-requisitos
  
  - Git
  - Docker em execução
  - Portas livres:
    - `5432` (PostgreSQL)
    - `8080` (Backend API)

### Executar aplicação:

```bash
git clone https://github.com/manoel-alves/order-management-system.git
cd order-management-system
docker compose up --build -d
```
- API disponível em: `http://localhost:8080/`

### Parar aplicação:

```bash
docker compose stop
```
  
#### Remover containers:
```bash
docker compose down 
```
   
#### Resetar banco (remove volumes):
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
- `Controller` → exposição REST e mapeamento DTO
- `Service` → Regras de negócio
- `Repository` → Acesso ao banco via Native Queries (JdbcTemplate)
- `Mapper` → conversão entre domínio e DTO
- `DTO` → Padronização de entrada e saída
- `Exception` → Tratamento global de erros

### Frontend
O frontend segue uma arquitetura modular orientada a features (Feature-Based Architecture), com separação clara de responsabilidades entre UI, estado e camada de acesso à API.

### Infraestrutura
- Containerização via Docker
- Orquestração dos serviços com Docker Compose
- Serviços configurados no `docker-compose.yml`:
  - `postgres` -> postgres:18
  - `backend-tests` -> Container para execução isolada de testes unitários.
  - `backend` -> Container principal da API do backend
  - `frontend` -> Container responsável por servir o frontend via _Nginx_.

---

## 📁 Estrutura do Projeto

### Monorepo:
```bash
order-management-system/ # Raiz do projeto (monorepo)
├─ backend/        # Aplicação Spring Boot (API REST)
├─ frontend/       # Interface do usuário (React + Vite)
├─ docker-compose.yml # Orquestra o backend e o banco via containers
├─ .gitignore      # Ignores gerais
└─ README.md       # Documentação do Projeto
```

### Backend:
```bash
backend/ 
├─ src/main/java/br/com/manoel/ordermanagement/
│  ├─ controller/     # Endpoints REST
│  ├─ dto/            # Padronização de Requests e Responses
│  │  ├── request/    # DTOs de entrada
│  │  └── response/   # DTOs de saída
│  ├─ exception/      # Tratamento de exceções
│  │  ├── api/        # Exceções de API
│  │  ├── domain/     # Exceções de domínio
│  │  └── GlobalExceptionHandler.java # Tratamento global de exceções
│  ├─ mapper/         # Conversão domínio → DTO
│  ├─ model/          # Entidades e regras de domínio
│  ├─ repository/     # Acesso a dados (Native Queries)
│  ├─ service/        # Regras de negócio
│  └─ OrderManagementSystemApplication.java # Ponto de entrada do Spring Boot
├─ src/main/resources/
│  ├─ db.migration/   # migrations do flyway
│  └─ application.yml # Configurações do Spring Boot 
├─ src/test/java/     # Testes unitários e de integração
├─ .gitignore         # Ignores específicos do backend
├─ Dockerfile         # instruções do container backend
├─ Dockerfile.test    # instruções do container de testes
└─ pom.xml            # Gerenciamento de dependências e build
```

### frontend
```bash
frontend/
├─ src/
│  ├─ api/           # Camada de acesso HTTP (fetch) e funções por recurso
│  ├─ components/    # Componentes compartilhados
│  ├─ layout/        # Layout base
│  ├─ app.css        # Estilos globais
│  ├─ App.jsx        # Componente raiz
│  └─ main.jsx       # Entrypoint
├─ .dockerignore     # ignores do contexto de build do Docker
├─ .gitignore        # ignores específicos do frontend
├─ Dockerfile        # Instruções do container frontend
├─ eslint.config.js  # Configuração do ESLint
├─ index.html        # Template base do Vite
├─ package.json      # Dependências e scripts
├─ package-lock.json # Lockfile npm para builds 
└─ vite.config.js    # Configuração do Vite
```

---

## 📡 API Endpoints

### Customer:

| **Método** | **Endpoint**             | **Descrição**           | **Retorno**                                   |
|:----------:|--------------------------|-------------------------|-----------------------------------------------|
|   _POST_   | `/customers`             | Cria um novo cliente    | 201 + dados do cliente ou 400 em caso de erro |
|   _GET_    | `/customers/{id}`        | Retorna cliente por ID  | 200 ou 404                                    |
|   _GET_    | `/customers?name=<nome>` | Busca clientes por nome | Lista de clientes                             |
|   _GET_    | `/customers`             | Lista todos os clientes | Lista de clientes                             |

### Product:

| **Método** | **Endpoint**                   | **Descrição**                | **Retorno**                                   |
|:----------:|--------------------------------|------------------------------|-----------------------------------------------|
|   *POST*   | `/products`                    | Cria um novo produto         | 201 + dados do produto ou 400 em caso de erro |
|   *GET*    | `/products/{id}`               | Retorna produto por ID       | 200 ou 404                                    |
|   *GET*    | `/products?description=<desc>` | Busca produtos por descrição | Lista de produtos                             |
|   *GET*    | `/products`                    | Lista todos os produtos      | Lista de produtos                             |

### Order:

| **Método** | **Endpoint**                              | **Descrição**                        | **Retorno**                                  |
|:----------:|-------------------------------------------|--------------------------------------|----------------------------------------------|
|   *POST*   | `/orders`                                 | Cria um novo pedido                  | 201 + dados do pedido ou 400 em caso de erro |
|   *GET*    | `/orders/{id}`                            | Retorna pedido por ID                | 200 ou 404                                   |
|   *GET*    | `/orders`                                 | Lista todos os pedidos               | Lista de pedidos                             |
|   *GET*    | `/orders?customerId=<id>`                 | Busca pedidos por cliente            | Lista de pedidos                             |
|   *GET*    | `/orders?productId=<id>`                  | Busca pedidos por produto            | Lista de pedidos                             |
|   *GET*    | `/orders/by-period?start=<iso>&end=<iso>` | Busca pedidos por intervalo de datas | Lista de pedidos                             |
|   *GET*    | `/orders/total?customerId=<id>`           | Retorna total de pedidos por cliente | Valor total (BigDecimal)                     |

### Observações:
- Todas as requisições **POST** utilizam validação via _Bean Validation_
- Respostas de erro seguem padrão definido pelo **GlobalExceptionHandler**
- _Header Location_ é retornado em operações de criação (201 Created)
- _Datas_ devem ser enviadas no formato **ISO-8601** (YYYY-MM-DDTHH:mm:ssZ)
- Endpoints de leitura (**GET**) não exigem payload

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

O backend possui **testes unitários** implementados cobrindo:

- `Models`: validações e regras de negócio
- `Services`: fluxos de criação, validações, exceções e integrações entre camadas
- `Controllers`: contratos HTTP, status codes, headers e tratamento global de erros

Os testes são isolados e não dependem de banco de dados real.

### Tecnologias utilizadas
- JUnit 5
- Mockito
- MockMvc

### Execução manual:
```bash
cd backend
mvn clean test
```

---

## 💡 Decisões Técnicas

### Gerais:
- **Monorepo**: centraliza backend e frontend, simplificando builds e versionamento, se adequando ao escopo simples do projeto.


- **Containerização via Docker Compose**: garante que a aplicação possa ser reproduzida facilmente em qualquer ambiente, de maneira consistente, independente do ambiente de execução.


- **Conventional Commits**: para garantir organização, clareza e uniformidade entre os commits. Seguindo o padrão `tipo(escopo): descrição` com body descritivo.


- **Padronização de Idioma**:
   - **Código**: escrito em _inglês_ para garantir clareza semântica e melhor legibilidade.
   - **Mensagens de Commit**: escritas em _inglês_ para garantir alinhamento técnico com o código e conformidade com o padrão Conventional Commits.
   - **Documentação**: escrita em _português_, considerando o público avaliador.
   - **Mensagens de exceção** (validação e erro): escritas em _português_ a fim de se adequar ao público-alvo que serão usuários finais.
   - _Internacionalização (i18n)_ **não será implementada** devido ao escopo e prazo do projeto.


### Backend:

- **Spring Boot**: adotado por ser um ecossistema Java maduro e amplamente reconhecido, reduzindo o overhead de compreensão da stack, permitindo maior foco nas regras de negócio. Além disso, a auto-configuração contribui para a consistência e previsibilidade do ambiente.


- **Arquitetura em camadas (Controller → Service → Repository)**: Garante separação clara de responsabilidades:
  - Controller: camada de exposição HTTP
  - Service: regras de negócio e orquestração
  - Repository: acesso a dados


- **Mappers dedicados**: Convertem entidades de domínio para DTOs de resposta, evitando expor o modelo interno diretamente e mantém o Controller limpo.


- **DTOs**: para evitar acoplamento direto ao modelo de persistência e permitir maior controle sobre dados expostos.


- **JdbcTemplate**: adotado para atender ao requisito do desafio de uso de native queries, garantindo controle explícito sobre as consultas SQL e maior previsibilidade do fluxo de persistência.
  - ORM (JPA/Hibernate) poderia ser uma alternativa viável para esse sistema em outro contexto, devido à simplicidade das operações requisitadas, já que simplifica o mapeamento de relacionamentos e reduz boilerplate, permitindo maior foco nas regras de negócio e na organização do domínio.


- **Validações em múltiplas camadas**: visa garantir que as regras de negócio sejam resguardadas independentemente da camada de entrada. 
  - _Bean Validation_ para validação estrutural de requisições
  - _Validação de domínio_ nas entidades e serviços
  - Tratamento centralizado de exceções via _GlobalExceptionHandler_


- **Lombok**: utilizado exclusivamente para getters/toString, sem geração automática de CRUD ou código estrutural.


### Banco de dados

- **PostgreSQL**: banco de dados relacional conforme requisito do desafio, usado devido à maior familiaridade e domínio diante das demais opções. 


- **Flyway**: para versionamento de banco de dados automatizado, garantindo reprodutibilidade e histórico auditável.


- **BIGSERIAL (autoincrement)**: adotado por ser simples e ter baixo custo de indexação, sendo mais eficiente diante do cenário single-database do desafio. Além de facilitar auditoria e debug.


- **Nomes de tabelas**: para evitar conflito com a palavra reservada `ORDER` no SQL, optei por nomeá-las no **plural** (ex.: `orders`), garantindo uniformidade e evitando problemas de sintaxe. 


- **TIMESTAMP WITH TIME ZONE (UTC)**: adotado para armazenar datas no padrão UTC, garantindo maior precisão e evitando ambiguidades de fuso horário.
  - DEFAULT CURRENT_TIMESTAMP foi removido a fim de delegar controle exclusivo da definição de timestamps à aplicação.

### Frontend

- **Stack**: 
  - **React**: utilizado para construir a interface em componentes reutilizáveis, facilitando manutenção e evolução incremental por entidade (Clientes, Produtos, Pedidos).

  - **Vite**: adotado por oferecer experiência de desenvolvimento dinâmica no ciclo _alterar -> testar_.

  - **Javascript**: escolhido considerando o escopo controlado da aplicação e a baixa complexidade estrutural do frontend, diminuindo configuração e verbosidade adicional.

  - **Bootstrap**: adotado por oferecer UI consistente, responsiva e padronizada com baixo custo de manutenção de estilo.

  - **Nginx (container)**: utilizado para garantir um contexto de build leve e estável para servir o frontend dentro do container. 


- **Arquitetura (Feature-based)**: 
  - Organizada por feature para manter cada entidade isolada e fácil de evoluir de acordo com suas particularidades.
  - distinção clara entre camada de UI (componentes), camada de estado (hooks) e camada de integração (API layer), garantindo maior previsibilidade e facilidade de manutenção 


- **apiFetch**: implementei apiFetch ao invés de usar fetch() diretamente para centralizar configuração de headers, tratamento de erros, parsing de resposta e desacoplar funções de domínio da implementação http.


- **UI e Layout**: Layout com header fixo e com foco em telas por entidade.

---

## 👤 Autor

Desenvolvido por **Manoel Alves** como desafio técnico para o processo
seletivo da [SergipeTec](https://sergipetec.org.br).