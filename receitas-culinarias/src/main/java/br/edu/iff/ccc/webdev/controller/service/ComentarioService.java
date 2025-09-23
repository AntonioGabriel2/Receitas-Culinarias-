package br.edu.iff.ccc.webdev.controller.service;

import br.edu.iff.ccc.webdev.entities.Comentario;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.repository.ComentarioRepository;
import br.edu.iff.ccc.webdev.repository.ReceitaRepository;
import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComentarioService {

    private final ComentarioRepository repo;
    private final ReceitaRepository receitaRepo;
    private final UsuarioRepository usuarioRepo;

    public ComentarioService(ComentarioRepository repo,
                             ReceitaRepository receitaRepo,
                             UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.receitaRepo = receitaRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarPorReceita(Long receitaId, boolean incluirApagados) {
        return incluirApagados
                ? repo.findByReceitaIdOrderByCriadoEmAsc(receitaId)
                : repo.findByReceitaIdAndApagadoFalseOrderByCriadoEmAsc(receitaId);
    }

    // padrão (não mostra apagados)
    @Transactional(readOnly = true)
    public List<Comentario> listarPorReceita(Long receitaId) {
        return listarPorReceita(receitaId, false);
    }

    @Transactional
    public Comentario criar(Long receitaId, String emailAutor, String texto) {
        Receita receita = receitaRepo.findById(receitaId)
                .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada."));
        Usuario autor = usuarioRepo.findByEmailIgnoreCase(emailAutor)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) throw new IllegalArgumentException("Texto obrigatório.");

        Comentario c = new Comentario(receita, autor, t);
        return repo.save(c);
    }

    @Transactional
    public void excluirSoft(Long comentarioId, String requesterEmail, boolean isAdmin) {
        Comentario c = repo.findById(comentarioId)
                .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado."));

        if (c.isApagado()) return;

        boolean author = c.getAutor().getEmail().equalsIgnoreCase(requesterEmail);
        if (!isAdmin && !author) {
            throw new IllegalArgumentException("Sem permissão para excluir este comentário.");
        }

        Usuario apagador = usuarioRepo.findByEmailIgnoreCase(requesterEmail).orElse(null);
        c.setApagado(true);
        c.setApagadoPor(apagador);
        c.setApagadoEm(LocalDateTime.now());
        repo.save(c);
    }

    @Transactional(readOnly = true)
    public Optional<Long> receitaIdDoComentario(Long comentarioId) {
        return repo.findById(comentarioId).map(c -> c.getReceita().getId());
    }
}
