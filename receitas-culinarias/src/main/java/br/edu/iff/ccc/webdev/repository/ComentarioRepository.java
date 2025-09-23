package br.edu.iff.ccc.webdev.repository;

import br.edu.iff.ccc.webdev.entities.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByReceitaIdAndApagadoFalseOrderByCriadoEmAsc(Long receitaId);
    List<Comentario> findByReceitaIdOrderByCriadoEmAsc(Long receitaId);
}
