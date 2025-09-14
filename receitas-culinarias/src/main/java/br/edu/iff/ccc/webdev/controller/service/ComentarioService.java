package br.edu.iff.ccc.webdev.controller.service;

import br.edu.iff.ccc.webdev.entities.Comentario;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.repository.ComentarioRepository;
import br.edu.iff.ccc.webdev.repository.ReceitaRepository;
import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepo;
    private final ReceitaRepository receitaRepo;
    private final UsuarioRepository usuarioRepo;

    public ComentarioService(ComentarioRepository comentarioRepo,
                             ReceitaRepository receitaRepo,
                             UsuarioRepository usuarioRepo) {
        this.comentarioRepo = comentarioRepo;
        this.receitaRepo = receitaRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarPorReceita(Long receitaId) {
        return comentarioRepo.findByReceitaIdOrderByCreatedAtAsc(receitaId);
    }

    @Transactional
    public Comentario adicionar(Long receitaId, String emailAutor, String texto) {
        String t = (texto == null) ? null : texto.trim();
        if (t == null || t.isEmpty()) {
            throw new IllegalArgumentException("Comentário não pode ser vazio.");
        }

        Receita r = receitaRepo.findById(receitaId)
                .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada."));

        Usuario autor = usuarioRepo.findByEmailIgnoreCase(emailAutor.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Usuário autor não encontrado."));

        Comentario c = new Comentario(t, autor, r);
        return comentarioRepo.save(c);
    }

    @Transactional
    public void excluir(Long comentarioId, boolean podeExcluir) {
        Comentario c = comentarioRepo.findById(comentarioId)
                .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado."));
        if (!podeExcluir) {
            throw new IllegalArgumentException("Sem permissão para excluir este comentário.");
        }
        comentarioRepo.delete(c);
    }
}
