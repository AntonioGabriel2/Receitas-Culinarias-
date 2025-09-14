package br.edu.iff.ccc.webdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.iff.ccc.webdev.entities.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByReceitaIdOrderByCriadoEmAsc(Long receitaId);
    List<Comentario> findByReceitaIdAndApagadoFalseOrderByCriadoEmAsc(Long receitaId);
    Optional<Comentario> findById(Long id);
}
