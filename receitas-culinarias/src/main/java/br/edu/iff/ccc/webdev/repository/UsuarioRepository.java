package br.edu.iff.ccc.webdev.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.iff.ccc.webdev.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByCpf(String cpf);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id); // útil no update
}
