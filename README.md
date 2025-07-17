# AllsLife Framework

## 1. Backend

O backend é construído com Spring Boot e é o coração do sistema de geração de rotinas. Ele gerencia a lógica de negócio, a interação com o LLM e a persistência de dados.

### Pré-requisitos

Para executar o backend, você precisará ter instalado:

*   **Java Development Kit (JDK) 17 ou superior**
*   **Maven**

### Configuração do Banco de Dados

É necessário configurar as credenciais do banco de dados no arquivo `src/main/resources/application.properties`. Certifique-se de que o banco de dados esteja em execução e acessível.

Crie um banco chamado `db_allslife` antes de configurar da seguinte forma:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/db_allslife
spring.datasource.username=username
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Security settings
api.security.token.secret=secret-key-here

#Gemini
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=
gemini.api.key=your-key-here
```

### Executando o Backend

1.  **Navegue até o diretório raiz do backend:**

    ```bash
    cd ...
    ```

2.  **Compile o projeto usando Maven:**

    ```bash
    mvn clean install
    ```

3.  **Execute a aplicação Spring Boot:**

    ```bash
    mvn spring-boot:run
    ```

    O backend estará disponível em `http://localhost:8080`.

## 2. Frontend

Cada instância é um frontend desenvolvido com Next.js, que interage com o backend para fornecer funcionalidades específicas para rotinas de esportes, de estudos e de idiomas.

### Pré-requisitos

Para executar cada frontend, você precisará ter instalado:

*   **Node.js (versão 18 ou superior)**
*   **npm** ou **Yarn**

### Executando o Frontend `languageslife`

1.  **Navegue até o diretório raiz do frontend `languageslife`:**

    ```bash
    cd /allslife-frontend/languageslife
    ```

2.  **Instale as dependências:**

    ```bash
    npm install
    # ou yarn install
    ```

3.  **Inicie o servidor de desenvolvimento:**

    ```bash
    npm run dev
    # ou yarn dev
    ```

    O `languageslife` estará disponível em `http://localhost:3000`.
    
### Executando o Frontend `sportslife`

1.  **Navegue até o diretório raiz do frontend `sportslife`:**

    ```bash
    cd /allslife-frontend/sportslife
    ```

2.  **Instale as dependências:**

    ```bash
    npm install
    # ou yarn install
    ```

3.  **Inicie o servidor de desenvolvimento:**

    ```bash
    npm run dev
    # ou yarn dev
    ```

    O `sportslife` estará disponível em `http://localhost:3000`.

### Executando o Frontend `studyslife`

1.  **Navegue até o diretório raiz do frontend `studyslife`:**

    ```bash
    cd /allslife-frontend/sportslife
    ```

2.  **Instale as dependências:**

    ```bash
    npm install
    # ou yarn install
    ```

3.  **Inicie o servidor de desenvolvimento:**

    ```bash
    npm run dev
    # ou yarn dev
    ```

    O frontend `studyslife` estará disponível em `http://localhost:3000`.


