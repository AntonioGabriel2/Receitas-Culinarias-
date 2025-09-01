
Receitas Culinárias

Aplicação web para cadastro e gestão de receitas, com controle de acesso por perfis (ADMIN, COZINHEIRO, USUARIO). Usa Spring Boot 3, Spring Security, Thymeleaf e banco em memória H2.

Funcionalidades

Listar receitas (público)

Detalhar, criar, editar e excluir receitas (ADMIN/COZINHEIRO)

Listar usuários (público)

Criar usuário (público) e editar dados do próprio usuário (ou de qualquer usuário, se ADMIN)

Fluxo de solicitação para virar cozinheiro: o usuário pede; o ADMIN aprova ou rejeita

Login/Logout padrão do Spring Security

Console do H2 disponível em /h2-console


Stack

Java 21 • Spring Boot 3.5.x

Spring MVC • Spring Data JPA • Spring Security

Thymeleaf + thymeleaf-extras-springsecurity6

Banco: H2 (memória)


Estrutura (principais pacotes)

br.edu.iff.ccc.webdev
├─ config/
│  └─ SecurityConfig.java
├─ controller/
│  ├─ service/
│  │  ├─ UsuarioService.java
│  │  └─ ReceitaService.java
│  └─ view/
│     ├─ UsuarioController.java
│     └─ ReceitaController.java
├─ dto/
│  ├─ UsuarioDTO.java
│  └─ ReceitaDTO.java
├─ entities/
│  ├─ Usuario.java
│  └─ Receita.java
├─ repository/
│  ├─ UsuarioRepository.java
│  └─ ReceitaRepository.java
└─ templates/
   ├─ index.html
   ├─ receitas.html, receita_form.html, receita_detalhes.html
   ├─ usuarios.html, usuario_form.html, usuario_detalhes.html
 

Perfis e Regras de Acesso

Perfis suportados (Enum Perfil do Usuario):

ADMIN

COZINHEIRO

USUARIO (padrão para novos cadastros)


Regras principais (definidas em SecurityConfig):

Público:

GET /receitas, GET /receitas/{id}

GET /usuarios, GET /usuarios/{id}

GET /usuarios/new e POST /usuarios (cadastrar usuário)

/h2-console/**


Apenas COZINHEIRO/ADMIN:

GET /receitas/new, GET /receitas/{id}/edit

POST /receitas/** (criar/atualizar/excluir)


Somente o próprio usuário OU ADMIN:

GET /usuarios/{id}/edit, POST /usuarios/{id} (editar dados)

POST /usuarios/{id}/solicitar-cozinheiro (apenas o próprio)


Somente ADMIN:

POST /usuarios/{id}/aprovar-cozinheiro

POST /usuarios/{id}/rejeitar-cozinheiro



Como rodar

Pré-requisitos

JDK 21

Maven 3.9+ (ou usar sua IDE)


Executando

# na raiz do projeto
mvn spring-boot:run
# ou empacotar e rodar:
mvn clean package
java -jar target/*.jar

A aplicação sobe em: http://localhost:8080

URLs úteis

Receitas: /receitas

Usuários: /usuarios

Login: /login (padrão do Spring Security)

Logout: /logout

H2 Console: /h2-console

JDBC URL: jdbc:h2:mem:receitas

User: SA

Password: (vazio)



> Observação: o profile dev costuma estar ativo; o banco é em memória (reseta a cada execução).



Criando o primeiro ADMIN

Como o login usa usuários do banco, e novos usuários entram como USUARIO, faça assim:

1. Acesse /usuarios/new e crie um usuário (senha é salva com BCrypt).


2. Vá para /h2-console e conecte.


3. Execute um UPDATE para promover seu usuário:



-- o nome da tabela pode ser USUARIO ou USUARIOS, conforme sua entidade
UPDATE USUARIO SET PERFIL = 'ADMIN' WHERE EMAIL = 'seu-email@exemplo.com';
-- ou, se a tabela estiver pluralizada:
UPDATE USUARIOS SET PERFIL = 'ADMIN' WHERE EMAIL = 'seu-email@exemplo.com';

> Depois disso, faça login e use as telas de aprovação/rejeição de cozinheiro e de edição geral.



Navegação principal

/receitas

Lista de receitas.

Se logado como COZINHEIRO/ADMIN, aparece botão “Nova receita”.

Links para Detalhes, Editar e Excluir (conforme permissões).


/usuarios

Lista de usuários.

Para cada usuário:

Detalhes (público)

Editar (próprio ou ADMIN)

Pedir para virar cozinheiro (apenas no próprio)

Aprovar/Rejeitar (ADMIN, quando houver solicitação)


Topo com Login/Logout.



Segurança (como está configurado)

SecurityConfig:

Define um PasswordEncoder (BCrypt).

Implementa UserDetailsService que carrega o usuário por e-mail a partir do UsuarioRepository e monta um UserDetails com roles(...) baseadas no perfil do usuário (gera ROLE_ADMIN, ROLE_COZINHEIRO, etc).

Regras de autorização por endpoint (ver seção “Perfis e Regras de Acesso”).

Ativa formulário de login/logout padrão.


Página de Login customizada (opcional)

Se quiser usar uma página própria (templates/login.html), ajuste o SecurityConfig:

http
  .formLogin(login -> login
     .loginPage("/login")     // sua rota GET
     .permitAll()
     .defaultSuccessUrl("/receitas", true)
  )
  .logout(logout -> logout
     .logoutUrl("/logout")
     .logoutSuccessUrl("/receitas")
  );

E crie um @Controller simples:

@Controller
public class AuthController {
  @GetMapping("/login")
  public String login() {
    return "login"; // templates/login.html
  }
}

login.html mínimo (Thymeleaf):

<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="pt-br">
<head><meta charset="UTF-8"><title>Login</title></head>
<body>
  <h1>Entrar</h1>
  <form th:action="@{/login}" method="post">
    <div><label>Email: <input type="text" name="username"></label></div>
    <div><label>Senha: <input type="password" name="password"></label></div>
    <button type="submit">Login</button>
  </form>
  <p><a th:href="@{/usuarios/new}">Criar conta</a></p>
</body>
</html>

Banco e Dados

H2 em memória (jdbc:h2:mem:receitas), reinicia a cada execução.

Console habilitado em /h2-console (com frameOptions e CSRF liberados apenas para isso).


Dicas & Troubleshooting

Erro ao carregar expressões de segurança no Thymeleaf: ao usar sec:authorize, não use ${} dentro de SpEL. Ex.:
sec:authorize="hasRole('ADMIN') or principal?.username == ${u.email}" ❌
Correto: sec:authorize="hasRole('ADMIN') or (isAuthenticated() and principal?.username == *{email})" quando dentro de um form/model adequado, ou prepare flags no model no controller.

IDs “pulando” após exclusão: isso é o comportamento normal de IDs auto-increment. Não são “reaproveitados”. Se precisar ordenação “do mais antigo pro mais novo”, ordene por id ASC; se quiser “recentes primeiro”, id DESC.

Botões não aparecendo: confira permissões no SecurityConfig e, no HTML, condicional de exibição (por exemplo com sec:authorize).





