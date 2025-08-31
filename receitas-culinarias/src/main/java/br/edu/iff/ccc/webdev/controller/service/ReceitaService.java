package br.edu.iff.ccc.webdev.controller.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.iff.ccc.webdev.dto.ReceitaDTO;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.repository.ReceitaRepository;

@Service
public class ReceitaService {

    private final ReceitaRepository repo;

    public ReceitaService(ReceitaRepository repo) {
        this.repo = repo;
    }

    /* CREATE */
    @Transactional
    public Receita criar(ReceitaDTO dto) {
        String nome = trimToNull(dto.getNome());
        if (nome == null) throw new IllegalArgumentException("Nome obrigatório.");

        Receita r = new Receita(
            nome,
            trimOrNull(dto.getIngredientes()),
            trimOrNull(dto.getModoPreparo())
        );
        return repo.save(r);
    }

    /* UPDATE */
    @Transactional
    public Receita atualizar(Long id, ReceitaDTO dto) {
        Receita r = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada."));

        String nome = trimToNull(dto.getNome());
        if (nome == null) throw new IllegalArgumentException("Nome obrigatório.");

        r.setNome(nome);
        r.setIngredientes(trimOrNull(dto.getIngredientes()));
        r.setModoPreparo(trimOrNull(dto.getModoPreparo()));
        return repo.save(r);
    }

    /* READ - list (entidade) */
    @Transactional(readOnly = true)
    public List<Receita> findAll() {
        return repo.findAll(Sort.by("nome").ascending());
    }

    /* READ - one (entidade) */
    @Transactional(readOnly = true)
    public Optional<Receita> findById(Long id) {
        return repo.findById(id);
    }

    /* DELETE */
    @Transactional
    public void excluir(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Receita não encontrada.");
        }
        repo.deleteById(id);
    }

    /* ===== helpers ===== */


    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String trimToNull(String s) {
        return trimOrNull(s); // igual ao de cima, só semântica
    }
}
