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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity // habilita @PreAuthorize se quiser usar
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // casa com seu UsuarioService
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioRepository repo) {
        return username -> repo.findByEmailIgnoreCase(username.toLowerCase())
            .map(u -> User.withUsername(u.getEmail())
                    .password(u.getSenhaHash())
                    .roles(u.getPerfil().name()) // gera ROLE_COZINHEIRO, etc.
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
            // H2 console
            .requestMatchers("/h2-console/**").permitAll()

            // 🔒 TELAS DE FORMULÁRIO (mesmo sendo GET) — exigem login com papel
            .requestMatchers(HttpMethod.GET, "/receitas/new", "/receitas/*/edit")
                .hasAnyRole("COZINHEIRO","ADMIN")

            // 🔒 Escrita em receitas (seus forms usam POST)
            .requestMatchers(HttpMethod.POST, "/receitas/**")
                .hasAnyRole("COZINHEIRO","ADMIN")

            // 🌐 Leitura pública
            .requestMatchers(HttpMethod.GET, "/receitas", "/receitas/").permitAll()
            .requestMatchers(HttpMethod.GET, "/receitas/*").permitAll()

            // resto
            .anyRequest().permitAll()
        )
        .formLogin(Customizer.withDefaults())
        .logout(Customizer.withDefaults());

        return http.build();
    }
}
