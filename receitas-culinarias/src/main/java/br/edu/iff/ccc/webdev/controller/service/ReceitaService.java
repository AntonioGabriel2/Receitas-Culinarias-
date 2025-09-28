package br.edu.iff.ccc.webdev.controller.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import br.edu.iff.ccc.webdev.dto.ReceitaDTO;
import br.edu.iff.ccc.webdev.entities.Ingrediente;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.exception.ReceitaNaoEncontrada;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.repository.ReceitaRepository;
import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import br.edu.iff.ccc.webdev.repository.FavoritoRepository;

@Service
public class ReceitaService {

    private final ReceitaRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final FavoritoRepository favoritoRepository;

    public ReceitaService(ReceitaRepository repo, UsuarioRepository usuarioRepo, FavoritoRepository favoritoRepository) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.favoritoRepository = favoritoRepository;
    }

    /* CREATE — agora recebe o e-mail do dono */
    @Transactional
    public Receita criar(ReceitaDTO dto, String emailDono) {
        String nome = trimToNull(dto.getNome());
        if (nome == null) throw new IllegalArgumentException("Nome obrigatório.");

        if (emailDono == null || emailDono.isBlank()) {
            throw new IllegalArgumentException("Usuário logado não encontrado.");
        }

        Usuario dono = usuarioRepo.findByEmailIgnoreCase(emailDono.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Usuário logado não encontrado."));

        Receita r = new Receita(
            nome,
            trimOrNull(dto.getModoPreparo())
        );
        r.setCriadoPor(dono);

        // String -> List<Ingrediente>
        var lista = parseIngredientesTexto(dto.getIngredientes());
        r.setIngredientes(lista);

        return repo.save(r);
    }

    /* UPDATE — não altera o dono */
    @Transactional
    public Receita atualizar(Long id, ReceitaDTO dto) {
        Receita r = repo.findById(id)
                .orElseThrow(() -> new ReceitaNaoEncontrada(id));

        String nome = trimToNull(dto.getNome());
        if (nome == null) throw new IllegalArgumentException("Nome obrigatório.");

        r.setNome(nome);
        r.setModoPreparo(trimOrNull(dto.getModoPreparo()));

        // String -> List<Ingrediente>
        var lista = parseIngredientesTexto(dto.getIngredientes());
        r.setIngredientes(lista);

        return repo.save(r);

    }

    /* READ - list (entidade) */
    @Transactional(readOnly = true)
    public List<Receita> findAllAsc() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    /* READ - one (entidade) */
    @Transactional(readOnly = true)
    public Receita findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ReceitaNaoEncontrada(id));
    }

    /* DELETE */
    @Transactional
    public void excluir(Long id) {
        if (!repo.existsById(id)) {
            throw new ReceitaNaoEncontrada(id);
        }

        // apaga favoritos que apontam para essa receita
        favoritoRepository.deleteByReceitaId(id);

        // comentários e ingredientes já caem por cascade/orphanRemoval
        repo.deleteById(id);
    }

    /* Permissão: é dono? (para controller usar) */
    @Transactional(readOnly = true)
    public boolean isOwner(Long receitaId, String email) {
        if (email == null || email.isBlank()) return false;
        return repo.existsByIdAndCriadoPorEmailIgnoreCase(receitaId, email);
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

    public Set<Long> idsReceitasFavoritasDoUsuario(String email) {
        return favoritoRepository.findIdsReceitasFavoritasPorEmail(email);
    }


    private static List<Ingrediente> parseIngredientesTexto(String texto) {
        List<Ingrediente> list = new ArrayList<>();
        if (texto == null) return list;
        for (String linha : texto.split("\\R")) { // quebra por linhas
            String t = linha.trim();
            if (!t.isEmpty()) {
                // ajuste o construtor conforme sua classe Ingrediente
                list.add(new Ingrediente(t));
            }
        }
        return list;
    }
}
