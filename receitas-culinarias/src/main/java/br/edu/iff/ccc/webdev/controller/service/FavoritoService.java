package br.edu.iff.ccc.webdev.controller.service;

import br.edu.iff.ccc.webdev.entities.Favorito;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.exception.ReceitaNaoEncontrada;
import br.edu.iff.ccc.webdev.exception.UsuarioNaoEncontrado;
import br.edu.iff.ccc.webdev.repository.FavoritoRepository;
import br.edu.iff.ccc.webdev.repository.ReceitaRepository;
import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class FavoritoService {

    private final FavoritoRepository favRepo;
    private final UsuarioRepository usuarioRepo;
    private final ReceitaRepository receitaRepo;

    public FavoritoService(FavoritoRepository favRepo,
                           UsuarioRepository usuarioRepo,
                           ReceitaRepository receitaRepo) {
        this.favRepo = favRepo;
        this.usuarioRepo = usuarioRepo;
        this.receitaRepo = receitaRepo;
    }

    @Transactional
    public void favoritar(String emailUsuario, Long receitaId) {
        Usuario u = usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontrado(emailUsuario));
        Receita r = receitaRepo.findById(receitaId)
                .orElseThrow(() -> new ReceitaNaoEncontrada(receitaId));

        if (favRepo.existsByUsuarioIdAndReceitaId(u.getId(), receitaId)) return; // idempotente
        favRepo.save(new Favorito(u, r));
    }

    @Transactional
    public void desfavoritar(String emailUsuario, Long receitaId) {
        Usuario u = usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontrado(emailUsuario));
        favRepo.deleteByUsuarioIdAndReceitaId(u.getId(), receitaId);
    }

    @Transactional(readOnly = true)
    public boolean isFavorita(String emailUsuario, Long receitaId) {
        return usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .map(u -> favRepo.existsByUsuarioIdAndReceitaId(u.getId(), receitaId))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<Receita> listarReceitasFavoritas(String emailUsuario) {
        Usuario u = usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontrado(emailUsuario));
        return favRepo.findAllByUsuarioIdOrderByIdAsc(u.getId())
                      .stream()
                      .map(Favorito::getReceita)
                      .toList();
    }

    /**
     * IDs das receitas favoritas do usuário logado (para pintar "estrelinhas" na listagem).
     */
    @Transactional(readOnly = true)
    public Set<Long> idsReceitasFavoritasDoUsuario(String emailUsuario) {
        return favRepo.findIdsReceitasFavoritasPorEmail(emailUsuario);
    }
}
