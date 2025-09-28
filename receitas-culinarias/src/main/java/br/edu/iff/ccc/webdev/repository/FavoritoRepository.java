package br.edu.iff.ccc.webdev.repository;

import br.edu.iff.ccc.webdev.entities.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    boolean existsByUsuarioIdAndReceitaId(Long usuarioId, Long receitaId);

    Optional<Favorito> findByUsuarioIdAndReceitaId(Long usuarioId, Long receitaId);

    // ✅ usado pelo service (ordem CRESCENTE por ID do favorito)
    List<Favorito> findAllByUsuarioIdOrderByIdAsc(Long usuarioId);

    // Se sua entidade Favorito tiver "createdAt", pode manter este também (opcional):
    // List<Favorito> findAllByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    long countByReceitaId(Long receitaId);

    void deleteByUsuarioIdAndReceitaId(Long usuarioId, Long receitaId);

    // Para pegar só os IDs das receitas favoritas do usuário (por e-mail)
    @Query("select f.receita.id from Favorito f where lower(f.usuario.email) = lower(:email)")
    Set<Long> findIdsReceitasFavoritasPorEmail(@Param("email") String email);

    // Para listar os favoritos pela string do e-mail (ordem CRESCENTE por ID do favorito)
    List<Favorito> findAllByUsuarioEmailOrderByIdAsc(String email);

    void deleteByReceitaId(Long receitaId);   // <== novo
}
