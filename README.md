# ECOUP - Sprint 4 (versao simplificada conforme aulas)

API RESTful em Java 21 com Spring Boot 4.1.1 e Oracle JDBC. Estrutura conforme MegaFarma e Pokedex: `to`, `dao`, `bo` e `resource`.

## Escopo implementado

- Cadastro, listagem, consulta por codigo, alteracao e exclusao de usuarios.
- Cadastro, listagem, consulta por codigo, alteracao e exclusao de acoes sustentaveis.
- Cada acao registra categoria, descricao e link de evidencia em video.
- Ao cadastrar, o Java define a data atual e o status `REGISTRADA`, pois nao existe etapa de aprovacao ou validacao de evidencia neste prototipo. Os pontos de cada categoria sao somente uma simulacao visual do React e nao sao gravados no Oracle.
- Validacao basica com `@Valid`, `@NotBlank`, `@Email` e metodos simples na BO.
- CORS para posterior integracao com o front-end.

## Funcionalidades ainda nao implementadas

Nao ha validacao de video por IA, calculo de impacto, pontuacao, ranking, premiacao mensal ou autenticacao. **O front-end React da ECOUP ja consome os endpoints localmente**, conforme testes realizados. O deploy Java no Render e os testes remotos estao concluidos. A publicacao do React na Vercel e uma etapa separada. Nao e uma entrega final 100% enquanto esses passos nao forem verificados.

## Estrutura

```
src/main/java/br/com/fiap/
  bo/AcaoBO.java, UsuarioBO.java
  dao/AcaoDAO.java, ConnectionFactory.java, UsuarioDAO.java
  resource/AcaoResource.java, CorsFilter.java, UsuarioResource.java
  to/AcaoTO.java, UsuarioTO.java
  EcoupApplication.java
```

## Antes de iniciar

1. JDK 21, IntelliJ IDEA, Oracle SQL Developer e um banco Oracle acessivel.
2. Defina as variaveis `DB_URL`, `DB_USER` e `DB_PASSWORD` no ambiente do processo Java (nao gravar usuario/senha no codigo). Pode ser no terminal do IntelliJ: no Windows PowerShell use `$env:DB_URL="jdbc:oracle:thin:@//SERVIDOR:1521/SERVICO"`, `$env:DB_USER="SEU_USUARIO"`, `$env:DB_PASSWORD="SUA_SENHA"`. Troque os valores pelos da conexao Oracle. Se usa CMD, use `set "DB_URL=..."` etc. **Nao e necessario usar a tela de variaveis do IntelliJ**.
3. No Oracle, deverao existir as tabelas `DDD_USUARIO` e `DDD_ACAO_SUSTENTAVEL` com a estrutura indicada abaixo. **Nao rode scripts de criacao sem antes confirmar que essas tabelas nao existem e que nao ha dados a preservar.** O SQL sera fornecido separadamente para execucao no SQL Developer, nao em uma pasta dentro do projeto Java.
4. No mesmo terminal, execute os comandos praticados nas aulas: `./mvnw clean -DskipTests package` e `java -jar target/ecoup-0.0.1-SNAPSHOT.jar` (Windows PowerShell: `.\mvnw.cmd clean -DskipTests package`, seguido pelo comando Java). O Java e a API sobem na porta 8080; nao abrem o front automaticamente.

### Estrutura esperada das tabelas Oracle

`DDD_USUARIO`: `CODIGO` NUMBER identity/PK, `NOME` VARCHAR2(120), `EMAIL` VARCHAR2(150) UNIQUE.

`DDD_ACAO_SUSTENTAVEL`: `CODIGO` NUMBER identity/PK, `USUARIO_CODIGO` NUMBER FK para `DDD_USUARIO.CODIGO`, `TITULO` VARCHAR2(200), `CATEGORIA` VARCHAR2(30), `LINK_EVIDENCIA` VARCHAR2(1000), `DATA_REGISTRO` DATE, `STATUS` VARCHAR2(20).

Estas tabelas devem ser criadas pelo usuario no Oracle SQL Developer, depois de uma verificacao do esquema atual. O projeto nao cria nem altera tabelas automaticamente.

## Endpoints

| Metodo | Caminho | Funcao |
|---|---|---|
| GET | `/api/usuarios` | Listar usuarios |
| GET | `/api/usuarios/{codigo}` | Buscar usuario |
| POST | `/api/usuarios` | Cadastrar usuario |
| PUT | `/api/usuarios/{codigo}` | Atualizar usuario |
| DELETE | `/api/usuarios/{codigo}` | Excluir usuario |
| GET | `/api/acoes` | Listar acoes |
| GET | `/api/acoes/{codigo}` | Buscar acao |
| POST | `/api/acoes` | Cadastrar acao com status REGISTRADA |
| PUT | `/api/acoes/{codigo}` | Atualizar dados de uma acao |
| DELETE | `/api/acoes/{codigo}` | Excluir acao |

### Exemplo JSON: novo usuario

```json
{"nome":"Ana Silva","email":"ana@example.com"}
```

### Exemplo JSON: nova acao

```json
{"usuarioCodigo":1,"titulo":"Fui de bicicleta para a faculdade","categoria":"Mobilidade","linkEvidencia":"https://exemplo.com/video"}
```

Use Insomnia para testar os metodos HTTP, exatamente como nas aulas. Os campos `codigo`, `dataRegistro` e `status` sao definidos pelo servidor e o DAO nao usa os valores de entrada desses campos ao cadastrar uma acao. O sistema apenas armazena o link da evidencia: nao faz upload nem analisa videos. Nao ha aprovacao posterior; o registro e confirmado imediatamente. Se houver acoes antigas com `PENDENTE` no Oracle, elas permanecem assim ate serem modificadas no banco.

## Tratamento simples de erros (como no MegaFarma)

A camada Resource usa `try/catch`, `ResponseEntity` e `HttpStatus`. As classes DAO continuam utilizando `PreparedStatement`, `SQLException` e fechamento automatico dos recursos.

| Caso | Resposta HTTP | Observacao |
|---|---|---|
| Listagem e consulta bem-sucedidas | 200 OK | Retorna JSON |
| Cadastro bem-sucedido | 201 Created | Retorna JSON com o codigo criado |
| Alteracao bem-sucedida | 200 OK | Retorna JSON |
| Exclusao bem-sucedida | 204 No Content | Sem corpo |
| Registro nao encontrado | 404 Not Found | Mensagem curta |
| Dados invalidos (`@Valid`) ou categoria/usuario invalidos em uma acao | 400 Bad Request | Validacao do Spring ou BO |
| Email duplicado no Oracle (ORA-00001) | 409 Conflict | Email ja cadastrado |
| Tentativa de excluir usuario com acoes vinculadas (ORA-02292) | 409 Conflict | Usuario possui acoes cadastradas |
| Erro inesperado ao acessar o banco | 500 Internal Server Error | Mensagem generica; nao devolve detalhes internos do Oracle |

Os codigos Oracle especificos sao verificados com `SQLException.getErrorCode()`, sem nova dependencia. Nao ha novas regras de negocio, tabelas ou endpoints. As validacoes originais permanecem. O status de uma nova acao continua sendo `REGISTRADA`.

Os testes de 409 dependem das restricoes `UNIQUE` do email e `FOREIGN KEY` das acoes realmente existirem no Oracle. Utilize o Insomnia e registros de teste; nao exclua dados reais.

## Deploy

O `Dockerfile` utiliza os mesmos comandos basicos da aula para criar o JAR da ECOUP (`./mvnw clean -DskipTests package`). O deploy da API Java no Render esta concluido. URL publica: https://ecoup-sprint4-java.onrender.com . Os endpoints GET, POST, PUT e DELETE foram testados remotamente com acesso ao Oracle. As credenciais devem permanecer apenas nas variaveis de ambiente do Render.

## Aviso sobre segurança

O projeto e uma **API didatica** e nao inclui sistema de login/permissoes porque isso nao foi desenvolvido nos exemplos usados. Nao publique dados reais nem exponha a API para operacoes de escrita por usuarios desconhecidos. Para uso publico real, sera necessario estudar autenticacao e autorizacao antes do deploy.

## Integração com o front-end atualizado

O front-end da Sprint 4 utiliza `POST /api/usuarios` para cadastrar um participante ficticio, `GET /api/usuarios/{codigo}` para identifica-lo na aba, `POST /api/acoes` para registrar a acao, `GET /api/acoes` para listar e `GET /api/acoes/{codigo}` para detalhar. Os demais metodos Java (PUT/DELETE de usuarios e acoes) continuam acessiveis no Insomnia, mas nao sao exibidos como botoes de administracao publica no front. 

O painel da SoulUp no React e institucional e seus dados nao sao os registros Oracle da ECOUP. Use somente dados ficticios para demonstracao; os endpoints Java ainda nao tem autorizacao de acesso.

## Documentacao final (PDF)

A documentacao da Sprint 4 Java inclui MER exportado do Oracle SQL Developer, diagrama de classes UML exportado do Astah, tabela de endpoints, testes no Render, arquitetura, procedimentos de execucao e conclusao.

- [Abrir documentacao em PDF](ECOUP_Sprint4_Java_Documentacao.pdf)
