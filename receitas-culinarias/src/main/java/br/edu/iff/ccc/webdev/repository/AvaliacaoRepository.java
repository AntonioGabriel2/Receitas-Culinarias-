// src/main/java/br/edu/iff/ccc/webdev/repository/AvaliacaoRepository.java
package br.edu.iff.ccc.webdev.repository;

import br.edu.iff.ccc.webdev.entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByUsuarioIdAndReceitaId(Long usuarioId, Long receitaId);
    boolean existsByUsuarioIdAndReceitaId(Long usuarioId, Long receitaId);
    long countByReceitaId(Long receitaId);
    void deleteByReceitaId(Long receitaId);
}
