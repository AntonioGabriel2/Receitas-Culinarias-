package br.edu.iff.ccc.webdev.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.iff.ccc.webdev.entities.Receita;

public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    // Para checar se um e-mail é dono da receita (ADMIN ou DONO)
    boolean existsByIdAndCriadoPorEmailIgnoreCase(Long id, String email);

    @Query("select r from Receita r " +
        "where (:q is null or lower(r.nome) like lower(concat('%', :q, '%'))) " +
        "order by r.nome asc")
    List<Receita> buscarPorNome(@Param("q") String q);

    
}
