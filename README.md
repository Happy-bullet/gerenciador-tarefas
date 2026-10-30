# 📝 Gerenciador de Tarefas

> Aplicação Web Java (JSP + Servlet + Hibernate) para cadastro, listagem, edição e exclusão de tarefas, com MySQL/MariaDB.

Trabalho AT da disciplina Linguagem de Programação Orientada a Objetos (2026-II).

## ✨ Destaques

- ✅ **MVC** - JSP só exibe, Servlet controla, DAO acessa o banco
- ✅ **Hibernate/JPA** - mapeamento objeto-relacional por anotações
- ✅ **Validação no servidor** - campos obrigatórios, data e valores dos ENUMs conferidos antes de gravar

---

## 🚀 Início Rápido

### Pré-requisitos
- JDK 11+ (o Hibernate 6 e o Tomcat 10.1 não rodam em Java 8)
- Maven 3.6+ (o Eclipse já traz embutido)
- Apache Tomcat 10.1+
- MySQL/MariaDB (XAMPP) ou Docker

### 1. Banco de dados

**Com XAMPP:** ligue o MySQL no painel do XAMPP, abra `http://localhost/phpmyadmin/`, vá na aba SQL e execute o conteúdo de `src/main/resources/create_table.sql`.

**Com Docker:**
```bash
docker compose up -d
```

O `hibernate.cfg.xml` usa usuário `root` com senha `root` (a mesma do `docker-compose.yml`). No XAMPP, onde o root vem sem senha, deixe `hibernate.connection.password` vazio.

### 2. Executar no Eclipse
1. `File > Import > Maven > Existing Maven Projects` e selecione a pasta do projeto.
2. Em `Window > Preferences > Server > Runtime Environments`, adicione o **Apache Tomcat v10.1** apontando para a pasta onde ele foi descompactado.
3. Clique com o botão direito no projeto: `Run As > Run on Server` e escolha o Tomcat 10.1.
4. Acesse `http://localhost:8080/gerenciador-tarefas/`.

### Compilar pelo terminal
```bash
mvn clean package
```
O arquivo `target/gerenciador-tarefas.war` pode ser copiado para a pasta `webapps` do Tomcat.

---

## 📋 Funcionalidades

| Página | Funcionalidade |
|---|---|
| `cadastroTarefas.jsp` | Formulário com nome, data de vencimento, prioridade e andamento |
| `listarTarefas.jsp` | Tabela com todas as tarefas do banco, com botões **Editar** e **Excluir** em cada linha |
| `/tarefas` (GET) | Consulta as tarefas e exibe a listagem |
| `/tarefas?acao=editar&id=N` (GET) | Abre o formulário de cadastro já preenchido com a tarefa N |
| `/tarefas` (POST) | Valida e grava uma nova tarefa; com o campo `id` preenchido, atualiza a tarefa existente |
| `/tarefas` (POST, `acao=excluir`) | Exclui a tarefa, após confirmação no navegador |

---

## 🔍 Validações Implementadas

### Nome da Tarefa
```
✓ Obrigatório
✓ Trim automático de espaços
✓ Máximo de 255 caracteres (tamanho da coluna)
```

### Data de Vencimento
```
✓ Obrigatória
✓ Formato aaaa-mm-dd (o mesmo enviado pelo <input type="date"> e usado pelo MySQL)
```

### Prioridade e Andamento
```
✓ Obrigatórios
✓ Só aceita os valores do ENUM do banco (normal/alta/baixa e pendente/em progresso/pronto)
```

Se algo estiver errado, o formulário volta com a mensagem de erro e com os valores já digitados.

---

## 📁 Estrutura do Projeto

```
gerenciador-tarefas/
├── src/main/java/com/gerenciador/
│   ├── controle/
│   │   └── TarefaServlet.java         [Controller]
│   ├── dao/
│   │   └── TarefaDAO.java             [Persistência]
│   ├── erro/
│   │   ├── PersistenciaException.java
│   │   └── ValidacaoException.java
│   ├── modelo/
│   │   ├── Tarefa.java                [Entidade]
│   │   ├── Prioridade.java            [Enum]
│   │   ├── Andamento.java             [Enum]
│   │   ├── PrioridadeConverter.java   [Enum <-> ENUM do banco]
│   │   └── AndamentoConverter.java    [Enum <-> ENUM do banco]
│   └── util/
│       ├── HibernateUtil.java         [Configuração DB]
│       └── AplicacaoListener.java     [Fecha o Hibernate ao parar]
├── src/main/resources/
│   ├── hibernate.cfg.xml              [Config Hibernate]
│   └── create_table.sql               [Script do banco]
├── src/main/webapp/
│   ├── index.jsp                      [Redireciona para a lista]
│   ├── cadastroTarefas.jsp            [View de cadastro e edição]
│   ├── listarTarefas.jsp              [View de listagem]
│   └── WEB-INF/web.xml
├── pom.xml                            [Dependências Maven]
└── docker-compose.yml                 [Container MariaDB]
```

---

## 🧭 Decisões de Projeto

| Decisão | Motivo |
|---|---|
| Padrão **MVC** | As JSPs não acessam o banco: o `TarefaServlet` busca os dados no DAO e entrega a lista pronta para a view |
| Padrão **DAO** | Só o `TarefaDAO` abre `Session` do Hibernate; trocar a forma de persistir não afeta servlet nem JSP |
| **Hibernate** em vez de JDBC puro | O SQL de INSERT e SELECT é gerado a partir das anotações da entidade `Tarefa` |
| **Enums + `AttributeConverter`** | O banco guarda `'em progresso'` (com espaço), que não pode ser nome de constante Java. O conversor traduz `EM_PROGRESSO` ↔ `'em progresso'` e cada enum tem o rótulo exibido na tela |
| **Post/Redirect/Get** | Depois de cadastrar, editar ou excluir, o navegador é redirecionado para a lista; apertar F5 não repete a operação |
| **Mesmo formulário para cadastro e edição** | `cadastroTarefas.jsp` recebe a tarefa em `${tarefa}` e um campo oculto `id`; vazio = cadastro, preenchido = edição. Evita duas telas iguais |
| **Excluir por POST** | Um link GET pode ser disparado só por abrir uma URL; a exclusão usa um formulário POST com `confirm()` antes de enviar |
| **Validação no servidor** | O `required` do HTML pode ser burlado; o servlet confere tudo antes de chamar o DAO |
| **`fn:escapeXml`** nas JSPs | Evita que um texto digitado com HTML seja interpretado pela página |
| **UTF-8** em todo o caminho | `pageEncoding` nas JSPs, `setCharacterEncoding` no servlet e `characterEncoding` na URL do banco, para os acentos não quebrarem |
| **Tomcat 10.1 / Jakarta** | O Hibernate 6 usa `jakarta.persistence`; para não misturar `javax` e `jakarta`, o projeto todo usa as APIs Jakarta |

---

## 🛠️ Solução de Problemas

| Sintoma | Causa provável |
|---|---|
| Erro "Não foi possível ... Verifique se o MySQL está ligado" | MySQL do XAMPP desligado, banco `TarefasDB` não criado ou senha diferente no `hibernate.cfg.xml` |
| 404 em `/tarefas` ou erro citando `jakarta/servlet` | Projeto rodando em Tomcat 9 ou anterior (que usa `javax`); use Tomcat 10.1+ |
| `UnsupportedClassVersionError` | JDK 8; instale JDK 11 ou superior |
| Acentos estranhos na lista | Tabela criada sem UTF-8; recrie o banco com o `create_table.sql` |
