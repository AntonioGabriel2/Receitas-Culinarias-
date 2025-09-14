package br.edu.iff.ccc.webdev.repository;

import br.edu.iff.ccc.webdev.entities.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    boolean existsByUsuarioIdAndReceitaId(Long usuarioId, Long receitaId);
    Optional<Favorito> findByUsuarioIdAndReceitaId(Long usuarioId, Long receitaId);
    List<Favorito> findAllByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    long countByReceitaId(Long receitaId);
    void deleteByUsuarioIdAndReceitaId(Long usuarioId, Long receitaId);
}
