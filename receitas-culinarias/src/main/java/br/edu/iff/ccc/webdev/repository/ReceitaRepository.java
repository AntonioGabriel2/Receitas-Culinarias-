package br.edu.iff.ccc.webdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.iff.ccc.webdev.entities.Receita;

public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    // Para checar se um e-mail é dono da receita (ADMIN ou DONO)
    boolean existsByIdAndCriadoPorEmailIgnoreCase(Long id, String email);
}
