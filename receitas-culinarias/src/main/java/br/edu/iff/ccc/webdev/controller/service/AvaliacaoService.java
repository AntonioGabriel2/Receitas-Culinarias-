package br.edu.iff.ccc.webdev.controller.service;

import br.edu.iff.ccc.webdev.entities.Avaliacao;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.exception.NotaInvalidaException;
import br.edu.iff.ccc.webdev.exception.ReceitaNaoEncontrada;
import br.edu.iff.ccc.webdev.exception.UsuarioNaoEncontrado;
import br.edu.iff.ccc.webdev.repository.AvaliacaoRepository;
import br.edu.iff.ccc.webdev.repository.ReceitaRepository;
import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final ReceitaRepository receitaRepo;

    public AvaliacaoService(AvaliacaoRepository repo, UsuarioRepository usuarioRepo, ReceitaRepository receitaRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
        this.receitaRepo = receitaRepo;
    }

    /** Cria/atualiza a nota do usuário para a receita (UPSERT) e atualiza agregados. */
    @Transactional
    public Avaliacao avaliar(String emailUsuario, Long receitaId, int valor) {
        if (valor < 1 || valor > 5) {
            throw new NotaInvalidaException(valor);
        }

        Usuario u = usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontrado(emailUsuario));

        Receita r = receitaRepo.findById(receitaId)
                .orElseThrow(() -> new ReceitaNaoEncontrada(receitaId));

        var opt = repo.findByUsuarioIdAndReceitaId(u.getId(), r.getId());
        if (opt.isPresent()) {
            // UPDATE
            Avaliacao a = opt.get();
            int anterior = a.getValor();
            a.setValor(valor);

            // ajusta agregados na Receita (teu modelo já tem esses métodos)
            r.incRatingSum(valor - anterior);

            repo.save(a);
            receitaRepo.save(r);
            return a;
        } else {
            // INSERT
            Avaliacao a = new Avaliacao(u, r, valor);
            repo.save(a);

            r.incRatingSum(valor);
            r.incRatingCount(1);

            receitaRepo.save(r);
            return a;
        }
    }


    /** Remove a nota do usuário e ajusta agregados. */
    @Transactional
    public void remover(String emailUsuario, Long receitaId) {
        Usuario u = usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontrado(emailUsuario));
        Receita r = receitaRepo.findById(receitaId)
                .orElseThrow(() -> new ReceitaNaoEncontrada(receitaId));

        var opt = repo.findByUsuarioIdAndReceitaId(u.getId(), r.getId());
        if (opt.isPresent()) {
            Avaliacao a = opt.get();

            r.incRatingSum(-a.getValor());
            r.incRatingCount(-1);

            repo.delete(a);
            receitaRepo.save(r);
        }
    }

    /** Nota do usuário para a receita (ou null). */
    @Transactional(readOnly = true)
    public Integer notaDoUsuario(String emailUsuario, Long receitaId) {
        return usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .flatMap(u -> repo.findByUsuarioIdAndReceitaId(u.getId(), receitaId))
                .map(Avaliacao::getValor)
                .orElse(null);
    }
}
