# Controle Veicular (Spring Boot + Supabase + Vercel)

API RESTful (Spring Boot + Swagger/OpenAPI) com front-end integrado para controlar o seu carro:
cadastro de veículos, histórico de manutenções e documentação (CRLV, IPVA, seguro etc.).

- **Back-end:** Java 17+ (Docker usa Java 21), Spring Boot 3.5, Spring Data JPA, Validation, springdoc (Swagger)
- **Banco de dados:** PostgreSQL do **Supabase**
- **Hospedagem:** **Vercel** (Container runtime, a partir do `Dockerfile`)
- **Front-end:** HTML, CSS e JavaScript puro em `src/main/resources/static` (servido pelo próprio Spring Boot,
  na mesma URL da API, então não precisa de CORS)

---

## 1. Configurar o Supabase

1. Crie uma conta em https://supabase.com e um novo projeto. **Anote a senha do banco** que você definir.
2. No menu lateral, abra **SQL Editor**, cole o conteúdo do arquivo `supabase/schema.sql` e clique em **Run**.
   Isso cria as tabelas `veiculo`, `manutencao` e `documento` e ativa o RLS (protege a API pública do Supabase).
3. Clique no botão **Connect** (topo da página do projeto), escolha **Session pooler** e a aba **JDBC**.
   Dessa tela você vai usar três informações:
   - o **host** (algo como `aws-0-sa-east-1.pooler.supabase.com`), porta `5432`;
   - o **usuário** (algo como `postgres.abcdefghijklmnop`);
   - a **senha** do passo 1.
4. Monte as três variáveis de ambiente:

| Variável | Valor |
|---|---|
| `DB_URL` | `jdbc:postgresql://SEU-HOST:5432/postgres?sslmode=require` |
| `DB_USER` | `postgres.SEU-REF` |
| `DB_PASSWORD` | a senha do banco (escreva normal, sem codificar) |

Use o **Session pooler** (porta 5432). A conexão direta do Supabase usa IPv6 e pode não funcionar em redes IPv4.

## 2. Testar no seu computador (IntelliJ)

1. Abra a pasta do projeto no IntelliJ (`File > Open`, pasta que contém o `pom.xml`).
2. Clique em **Run > Edit Configurations**, selecione `ControleVeicularApplication` e, em **Environment variables**,
   cole: `DB_URL=...;DB_USER=...;DB_PASSWORD=...` (separadas por `;`).
3. Execute a aplicação e abra:
   - Front-end: http://localhost:8080
   - Swagger: http://localhost:8080/swagger-ui/index.html

## 3. Publicar na Vercel

1. Coloque o projeto em um repositório no **GitHub** (a raiz do repositório deve conter `Dockerfile`, `vercel.json` e `pom.xml`).
   Não envie senhas: elas ficam apenas nas variáveis de ambiente da Vercel.
2. Em https://vercel.com, clique em **Add New > Project** e importe o repositório.
3. Se o preset não for detectado sozinho, escolha **Container** (o `vercel.json` já pede isso).
4. Em **Environment Variables**, cadastre `DB_URL`, `DB_USER` e `DB_PASSWORD`.
5. Clique em **Deploy**. Quando terminar, abra a URL do projeto:
   - Front-end: `https://SEU-PROJETO.vercel.app`
   - Swagger: `https://SEU-PROJETO.vercel.app/swagger-ui/index.html`

### Observações
- O Container runtime da Vercel está em **beta**. Containers ociosos são desligados após alguns minutos,
  então a **primeira requisição depois de um tempo parado é lenta** (o Java precisa iniciar de novo).
- A Vercel envia o tráfego para a porta definida em `PORT` (padrão 80). O `Dockerfile` já define `PORT=80`
  e o Spring Boot lê essa variável. Se o deploy reclamar de porta, cadastre `PORT=8080` nas variáveis de ambiente.
- Arquivos locais do container não são permanentes. Por isso todos os dados ficam no Supabase.

## Endpoints

| Recurso | POST (inserir) | GET (todos) | GET (um) | PUT (atualizar) | DELETE (remover) |
|---|---|---|---|---|---|
| Veículos | `/veiculos` | `/veiculos` | `/veiculos/{id}` | `/veiculos/{id}` | `/veiculos/{id}` |
| Manutenções | `/manutencoes` | `/manutencoes` (filtro opcional `?veiculoId=`) | `/manutencoes/{id}` | `/manutencoes/{id}` | `/manutencoes/{id}` |
| Documentos | `/documentos` | `/documentos` (filtro opcional `?veiculoId=`) | `/documentos/{id}` | `/documentos/{id}` | `/documentos/{id}` |

Códigos de resposta: 200 (OK), 201 (criado), 204 (removido), 400 (dados inválidos),
404 (não encontrado) e 409 (placa já cadastrada).

## Estrutura
```
.
├── Dockerfile            (como a Vercel compila e executa o projeto)
├── vercel.json           (pede o Container runtime)
├── pom.xml
├── supabase/schema.sql   (cria as tabelas no Supabase)
└── src/main
    ├── java/com/fatec/controleveicular
    │   ├── ControleVeicularApplication.java   (inicialização + metadados do Swagger)
    │   ├── model/        (entities: Veiculo, Manutencao, Documento)
    │   ├── repository/   (acesso ao banco com Spring Data JPA)
    │   ├── service/      (regras de negócio)
    │   ├── controller/   (endpoints REST documentados com Swagger)
    │   └── exception/    (tratamento padronizado de erros)
    └── resources
        ├── application.properties   (lê DB_URL, DB_USER, DB_PASSWORD e PORT)
        └── static/                  (front-end: index.html, css/style.css, js/app.js)
```
