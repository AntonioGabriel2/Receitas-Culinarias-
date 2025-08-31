package br.edu.iff.ccc.webdev;

import br.edu.iff.ccc.webdev.entities.Perfil;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ReceitasCulinariasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReceitasCulinariasApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(UsuarioRepository repo, PasswordEncoder enc) {
        return args -> {
            repo.findByEmailIgnoreCase("admin@ex.com").orElseGet(() -> {
                Usuario a = new Usuario("Admin", "00000000000",
                                        "admin@ex.com", enc.encode("123456"));
                a.setPerfil(Perfil.ADMIN);
                return repo.save(a);
            });
        };
    }
}
