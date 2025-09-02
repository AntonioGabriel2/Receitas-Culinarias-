package br.edu.iff.ccc.webdev.config;

import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.*;
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
        return username -> repo.findByEmailIgnoreCase(username.toLowerCase())
            .map(u -> User.withUsername(u.getEmail())
                    .password(u.getSenhaHash())
                    .roles(u.getPerfil().name())
                    .build()
            )
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                // estáticos e login próprios
                .requestMatchers("/h2-console/**", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/login").permitAll()

                // telas de formulário de receitas (só logados COZINHEIRO/ADMIN)
                .requestMatchers(HttpMethod.GET, "/receitas/new", "/receitas/*/edit").hasAnyRole("COZINHEIRO","ADMIN")
                .requestMatchers(HttpMethod.POST, "/receitas/**").hasAnyRole("COZINHEIRO","ADMIN")

                // leitura pública de receitas
                .requestMatchers(HttpMethod.GET, "/receitas", "/receitas/").permitAll()
                .requestMatchers(HttpMethod.GET, "/receitas/*").permitAll()
                .requestMatchers("/usuarios/**").hasRole("ADMIN")

                // o resto você decide (aqui está liberado)
                .anyRequest().permitAll()
            )
            .formLogin(login -> login
                .loginPage("/login")              // SUA página
                .loginProcessingUrl("/login")     // endpoint que processa (padrão)
                .usernameParameter("email")       // nome do campo no seu form
                .passwordParameter("password")    // nome do campo no seu form
                .defaultSuccessUrl("/receitas", true) // pra onde vai ao logar
                .failureUrl("/login?error")       // mostra erro
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")             // por padrão é POST /logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
