# Ecommerce Foursales

Sistema de gerenciamento de pedidos e produtos desenvolvido como desafio técnico para a vaga de Desenvolvedor Back-End Pleno. A aplicação expõe uma API REST segura com autenticação JWT, regras de negócio para pedidos e consultas otimizadas em MySQL.

## Sumário
- [Arquitetura da solução](#arquitetura-da-solução)
- [Tecnologias principais](#tecnologias-principais)
- [Requisitos de ambiente](#requisitos-de-ambiente)
- [Como executar](#como-executar)
    - [Variáveis de ambiente](#variáveis-de-ambiente)
    - [Banco de dados e migrações](#banco-de-dados-e-migrações)
- [Documentação da API](#documentação-da-api)
- [Usuários de demonstração](#usuários-de-demonstração)
- [Autenticação e perfis de acesso](#autenticação-e-perfis-de-acesso)
- [Guia de endpoints](#guia-de-endpoints)
    - [Autenticação](#autenticação)
    - [Produtos](#produtos)
    - [Pedidos](#pedidos)
    - [Relatórios SQL otimizados](#relatórios-sql-otimizados)
- [Regras de negócio dos pedidos](#regras-de-negócio-dos-pedidos)
- [Testes automatizados](#testes-automatizados)
- [Próximos passos e melhorias sugeridas](#próximos-passos-e-melhorias-sugeridas)

## Arquitetura da solução
A estrutura segue uma arquitetura em camadas para facilitar manutenção e testes:

- **API (`com.foursales.ecommerce.api`)** – controladores REST, DTOs de requisição/resposta e tratadores globais de exceção.
- **Application (`com.foursales.ecommerce.application`)** – serviços com regras de negócio, mapeadores e fachadas de autenticação.
- **Domain (`com.foursales.ecommerce.domain`)** – entidades JPA, enums e repositórios Spring Data responsáveis pelo acesso ao banco.
- **Infra (`com.foursales.ecommerce.infra`)** – configurações de segurança, filtro JWT, propriedades e bean de `PasswordEncoder`.

As migrações estão centralizadas em `src/main/resources/db/migration` e são executadas automaticamente pelo Flyway. O projeto é um módulo Maven único.

## Tecnologias principais
- Java 17
- Spring Boot 3 (Web, Data JPA, Security, Validation)
- MySQL 8
- Flyway para versionamento da base
- Docker Compose para infraestrutura local
- Maven Wrapper (`mvnw`) para build/testes
- JWT (JSON Web Token) para autenticação stateless

## Requisitos de ambiente
Antes de executar a aplicação, garanta que o ambiente possui:

| Recurso | Versão recomendada |
|---------|--------------------|
| Java | 17+ |
| Maven | 3.8+ (opcional, `./mvnw` já incluído) |
| Docker & Docker Compose | 20+ / 2.15+ |

## Como executar
1. **Clonar o repositório**
   ```bash
   git clone https://github.com/g-andradd/ecommerce-foursales.git
   cd ecommerce-foursales
   ```

2. **Subir o MySQL com Docker Compose**
   ```bash
   docker compose up -d mysql
   ```
   O serviço expõe o MySQL na porta `3306` com usuário `app` e senha `app` (configuráveis).

3. **Iniciar a aplicação Spring Boot**
   ```bash
   ./mvnw spring-boot:run
   ```
   A API ficará disponível em `http://localhost:8080`.

4. **(Opcional) Executar todos os testes**
   ```bash
   ./mvnw test
   ```

## Documentação da API
- A aplicação publica automaticamente a especificação OpenAPI em `http://localhost:8080/v3/api-docs`.
- A interface interativa do Swagger UI pode ser acessada em `http://localhost:8080/swagger-ui/index.html`.

Esses endpoints são públicos e permitem testar os fluxos autenticados enviando o token JWT obtido no login.

## Usuários de demonstração
As migrações do Flyway criam usuários e produtos de exemplo para facilitar testes locais. As credenciais padrão são:

| Perfil | Email | Senha |
|--------|-------|-------|
| ADMIN  | `admin@demo.com` | `123` |
| USER   | `joao@demo.com`  | `123` |
| USER   | `maria@demo.com` | `123` |

### Variáveis de ambiente
A aplicação lê as propriedades JWT de `application.yml`, permitindo sobrescrever por variáveis de ambiente:

| Variável | Descrição | Valor padrão |
|----------|-----------|---------------|
| `JWT_SECRET` | Segredo usado para assinar tokens JWT | `Zmp3bDNKa1QxTnZBczRkUmtmVjNyQnpYajR2aVBsN2MyZ1h1R2pUQnA=` |
| `SPRING_DATASOURCE_URL` | URL JDBC do MySQL | `jdbc:mysql://localhost:3306/ecommerce` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `app` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `app` |

### Banco de dados e migrações
- O Flyway executa automaticamente os scripts `V1__init.sql`, `V2__seed.sql` e `V3__ajustes_ecommerce.sql`, criando o schema, perfis de usuário e dados de exemplo.
- Caso prefira restaurar uma base completa, utilize as migrações acima ou gere um dump via `mysqldump` após a primeira execução.

## Autenticação e perfis de acesso
- **Registro**: qualquer pessoa pode criar um usuário enviando `nome`, `email`, `senha` e `perfil` (`ADMIN` ou `USER`).
- **Login**: retorna um token JWT com duração configurável (`expiration`).
- **Headers**: para acessar endpoints protegidos inclua `Authorization: Bearer <token>`.
- **Perfis**:
    - `ADMIN` – gerenciamento completo de produtos e acesso a relatórios.
    - `USER` – criação/listagem de pedidos e consulta de produtos.

### Exemplos de payloads
```http
POST /api/v1/auth/registrar
Content-Type: application/json

{
  "nome": "Maria Admin",
  "email": "maria@example.com",
  "senha": "senha123",
  "perfil": "ADMIN"
}
```

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "maria@example.com",
  "senha": "senha123"
}
```

## Guia de endpoints
### Autenticação
| Método | Caminho | Permissão | Descrição |
|--------|---------|-----------|-----------|
| `POST` | `/api/v1/auth/registrar` | Público | Cria um usuário com perfil `ADMIN` ou `USER`.
| `POST` | `/api/v1/auth/login` | Público | Autentica usuário e retorna token JWT.

### Produtos
| Método | Caminho | Permissão | Descrição |
|--------|---------|-----------|-----------|
| `POST` | `/api/v1/produtos` | `ADMIN` | Cria produto com nome, descrição, preço, categoria e estoque.
| `PUT` | `/api/v1/produtos/{id}` | `ADMIN` | Atualiza dados do produto.
| `DELETE` | `/api/v1/produtos/{id}` | `ADMIN` | Remove produto.
| `GET` | `/api/v1/produtos/{id}` | `ADMIN` ou `USER` | Recupera um produto pelo ID.
| `GET` | `/api/v1/produtos` | `ADMIN` ou `USER` | Lista produtos paginados (`page`, `size`, `sort`).

Exemplo de criação:
```http
POST /api/v1/produtos
Authorization: Bearer <token-admin>
Content-Type: application/json

{
  "nome": "Teclado Mecânico",
  "descricao": "Switches azuis, layout ABNT2",
  "preco": 499.90,
  "categoria": "Periféricos",
  "quantidadeEstoque": 25
}
```

### Pedidos
| Método | Caminho | Permissão | Descrição |
|--------|---------|-----------|-----------|
| `POST` | `/api/v1/pedidos` | `USER` | Cria pedido pendente a partir de uma lista de itens.
| `POST` | `/api/v1/pedidos/{id}/pagar` | `USER` | Processa pagamento, valida estoque e confirma/cancela pedido.
| `GET` | `/api/v1/pedidos` | `USER` | Lista pedidos do usuário autenticado com paginação.

Exemplo de criação e pagamento:
```http
POST /api/v1/pedidos
Authorization: Bearer <token-user>
Content-Type: application/json

{
  "itens": [
    { "produtoId": "f47ac10b-58cc-4372-a567-0e02b2c3d479", "quantidade": 2 },
    { "produtoId": "68b5a4ef-e2d2-4db8-83a6-92f312f0b1f8", "quantidade": 1 }
  ]
}
```
```http
POST /api/v1/pedidos/{id}/pagar
Authorization: Bearer <token-user>
```

### Relatórios SQL otimizados
Todos os endpoints de relatório exigem perfil `ADMIN` e utilizam consultas otimizadas diretamente no repositório de pedidos.

| Método | Caminho | Parâmetros | Descrição |
|--------|---------|------------|-----------|
| `GET` | `/api/v1/relatorios/top-usuarios` | — | Retorna top 5 usuários por valor total comprado.
| `GET` | `/api/v1/relatorios/ticket-medio` | — | Calcula ticket médio (valor médio por pedido) de cada usuário.
| `GET` | `/api/v1/relatorios/faturado` | `ano`, `mes` | Total faturado no mês informado.

## Regras de negócio dos pedidos
1. Todo pedido inicia com status `PENDENTE` e total calculado dinamicamente.
2. O pagamento:
    - Verifica se o pedido pertence ao usuário autenticado e se ainda está pendente.
    - Faz lock pessimista (`SELECT ... FOR UPDATE`) nos produtos envolvidos.
    - Cancela o pedido se algum item estiver sem estoque ou se o produto tiver sido removido.
    - Decrementa estoque de forma concorrente e marca o pedido como `PAGO`.
3. Se qualquer verificação falhar, o status é alterado para `CANCELADO` e o motivo é retornado na resposta de erro.
4. Após o pagamento, o pedido registra `pagoEm` e mantém histórico dos itens com preço unitário e subtotal praticados no momento da compra.

## Testes automatizados
Execute todos os testes com o Maven Wrapper:
```bash
./mvnw test
```
Os principais cenários cobertos incluem:

- **`EcommerceApplicationTests`** – smoke test que garante o carregamento completo do contexto Spring Boot.
- **`AutenticacaoFacadeTest`** e **`AutenticacaoServiceTest`** – validam o fluxo de registro e autenticação, incluindo hashing de senha, geração de token e tratamento de credenciais inválidas.
- **`TokenServiceTest`** – cobre a criação e validação de tokens JWT, além da extração de informações do usuário autenticado.
- **`ProdutoServiceTest`** – verifica criação, atualização, deleção e paginação de produtos com as devidas regras de negócio e validações.
- **`PedidoServiceTest`** – exercita a orquestração de pedidos, cálculos de totais, controle de estoque e transições de status.
- **`RelatorioServiceTest`** – garante o cálculo dos relatórios administrativos (top compradores, ticket médio e faturamento mensal).

> As classes de teste utilizam mocks do Mockito e o suporte do Spring Boot Test para isolar serviços de infraestrutura, permitindo execução rápida dos testes unitários.
