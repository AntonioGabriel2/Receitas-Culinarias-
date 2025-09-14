package br.edu.iff.ccc.webdev.config;

import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioRepository repo) {
        // Versão explícita (sem lambda/stream): mais fácil de ler e debugar
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                String email = (username == null) ? "" : username.toLowerCase();

                var opt = repo.findByEmailIgnoreCase(email);
                if (opt.isEmpty()) {
                    throw new UsernameNotFoundException("Usuário não encontrado");
                }

                var u = opt.get();
                // Monta o usuário do Spring Security com e-mail, senha hash e role
                return User.withUsername(u.getEmail())
                           .password(u.getSenhaHash())
                           .roles(u.getPerfil().name()) // vira ROLE_ADMIN / ROLE_COZINHEIRO / ROLE_USER
                           .build();
            }
        };
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1) CSRF (ignorar para /h2-console/**)
        http.csrf(new org.springframework.security.config.Customizer<
                org.springframework.security.config.annotation.web.configurers.CsrfConfigurer<HttpSecurity>>() {
            @Override
            public void customize(
                    org.springframework.security.config.annotation.web.configurers.CsrfConfigurer<HttpSecurity> csrf) {
                csrf.ignoringRequestMatchers("/h2-console/**");
            }
        });

        // 2) Headers: permitir frames da mesma origem (H2 Console)
        http.headers(new org.springframework.security.config.Customizer<
                org.springframework.security.config.annotation.web.configurers.HeadersConfigurer<HttpSecurity>>() {
            @Override
            public void customize(
                    org.springframework.security.config.annotation.web.configurers.HeadersConfigurer<HttpSecurity> headers) {

                headers.frameOptions(new org.springframework.security.config.Customizer<
                        org.springframework.security.config.annotation.web.configurers.HeadersConfigurer<HttpSecurity>.FrameOptionsConfig>() {
                    @Override
                    public void customize(
                            org.springframework.security.config.annotation.web.configurers.HeadersConfigurer<HttpSecurity>.FrameOptionsConfig frame) {
                        frame.sameOrigin();
                    }
                });
            }
        });

        // 3) Autorização por URL (ordem importa)
        http.authorizeHttpRequests(new org.springframework.security.config.Customizer<
                org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
            @Override
            public void customize(
                    org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {

                // Públicos (sem login)
                auth.requestMatchers("/h2-console/**", "/css/**", "/js/**", "/img/**").permitAll();
                auth.requestMatchers(org.springframework.http.HttpMethod.GET, "/login").permitAll();

                auth.requestMatchers(HttpMethod.POST, "/receitas/*/comentarios").authenticated();
                auth.requestMatchers(HttpMethod.POST, "/comentarios/*/delete").authenticated(); // controller valida se é admin/autor
                // leitura é pela mesma página de detalhes, já liberada

                // Favoritos: autenticado pode (des)favoritar e ver a página de favoritos
                auth.requestMatchers(HttpMethod.POST,
                        "/receitas/*/favoritar",
                        "/receitas/*/desfavoritar").authenticated();
                auth.requestMatchers(HttpMethod.GET, "/favoritos").authenticated();

                // (depois) sua regra já existente que restringe POSTS de criação/edição de receitas:
                auth.requestMatchers(HttpMethod.POST, "/receitas/**")
                    .hasAnyRole("COZINHEIRO", "ADMIN");


                // Formulários de receitas (COZINHEIRO/ADMIN)
                auth.requestMatchers(org.springframework.http.HttpMethod.GET, "/receitas/new", "/receitas/*/edit")
                        .hasAnyRole("COZINHEIRO", "ADMIN");
                auth.requestMatchers(org.springframework.http.HttpMethod.POST, "/receitas/**")
                        .hasAnyRole("COZINHEIRO", "ADMIN");

                // Leitura pública de receitas
                auth.requestMatchers(org.springframework.http.HttpMethod.GET, "/receitas", "/receitas/").permitAll();
                auth.requestMatchers(org.springframework.http.HttpMethod.GET, "/receitas/*").permitAll();

                // Cadastro público
                auth.requestMatchers(HttpMethod.GET,  "/usuarios/new").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/usuarios/new").permitAll();

                // Meu perfil (autenticado)
                auth.requestMatchers(HttpMethod.GET, "/usuarios/me").authenticated();

                // Listagem geral só ADMIN
                auth.requestMatchers(HttpMethod.GET, "/usuarios", "/usuarios/").hasRole("ADMIN");

                // >>> Pedido para virar cozinheiro (qualquer autenticado)
                auth.requestMatchers(org.springframework.http.HttpMethod.POST, "/usuarios/*/solicitar-cozinheiro").authenticated();

                // Ações administrativas explícitas
                auth.requestMatchers(HttpMethod.POST,
                        "/usuarios/*/aprovar-cozinheiro",
                        "/usuarios/*/rejeitar-cozinheiro",
                        "/usuarios/*/delete").hasRole("ADMIN");

                // Visualizar/editar o próprio perfil (controller faz a checagem fina)
                auth.requestMatchers(HttpMethod.GET,  "/usuarios/*", "/usuarios/*/edit").authenticated();
                auth.requestMatchers(HttpMethod.POST, "/usuarios/*").authenticated();


                // Qualquer outra URL: liberada
                auth.anyRequest().permitAll();
            }
        });

        // 4) Login com formulário (página própria)
        http.formLogin(new org.springframework.security.config.Customizer<
                org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer<HttpSecurity>>() {
            @Override
            public void customize(
                    org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer<HttpSecurity> login) {

                login.loginPage("/login");
                login.loginProcessingUrl("/login");
                login.usernameParameter("email");
                login.passwordParameter("password");
                login.defaultSuccessUrl("/", true);
                login.failureUrl("/login?error");
                login.permitAll();
            }
        });

        // 5) Logout (POST /logout, com CSRF)
        http.logout(new org.springframework.security.config.Customizer<
                org.springframework.security.config.annotation.web.configurers.LogoutConfigurer<HttpSecurity>>() {
            @Override
            public void customize(
                    org.springframework.security.config.annotation.web.configurers.LogoutConfigurer<HttpSecurity> logout) {

                logout.logoutUrl("/logout");
                logout.logoutSuccessUrl("/login?logout");
                logout.invalidateHttpSession(true);
                logout.deleteCookies("JSESSIONID");
                logout.permitAll();
            }
        });

        return http.build();
    }

}
