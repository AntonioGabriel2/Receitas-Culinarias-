package br.edu.iff.ccc.webdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.iff.ccc.webdev.model.Receita;

public interface ReceitaRepository extends JpaRepository<Receita, Long> {
}