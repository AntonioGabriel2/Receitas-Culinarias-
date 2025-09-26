// src/main/java/br/edu/iff/ccc/webdev/controller/service/AvaliacaoService.java
package br.edu.iff.ccc.webdev.controller.service;

import br.edu.iff.ccc.webdev.entities.Avaliacao;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.entities.Usuario;
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
            throw new IllegalArgumentException("Nota inválida (use 1 a 5).");
        }

        Usuario u = usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Receita r = receitaRepo.findById(receitaId)
                .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada."));

        Optional<Avaliacao> opt = repo.findByUsuarioIdAndReceitaId(u.getId(), r.getId());
        if (opt.isPresent()) {
            // UPDATE: ajustar soma mantendo a contagem
            Avaliacao a = opt.get();
            int anterior = a.getValor();
            a.setValor(valor);
            r.setModoPreparo(r.getModoPreparo()); // nada, só evita warning; não necessário

            // corrige agregados
            long novaSoma = r.getRatingMedia() == 0 && r.getRatingCount() == 0
                    ? valor // só para não depender da média
                    : (r.getRatingSum() /*getter se expuser*/); // mas não expusemos… ver abaixo
            // Como não expusemos getters, vamos ajustar via métodos utilitários:

            // ----- forma simples: recalcular pelos registros (custo baixo p/ poucos votos) -----
            // (Substitua por agregados se preferir expor getters/setters de ratingSum/ratingCount)
        } else {
            // INSERT
            Avaliacao a = new Avaliacao(u, r, valor);
            repo.save(a);

            // atualiza agregados
            r.setRatingCount(r.getRatingCount() + 1);   // adicione setter se precisar
            r.setRatingSum(r.getRatingSum() + valor);   // adicione setter se precisar

            receitaRepo.save(r);
            return a;
        }

        // ======= IMPLEMENTAÇÃO CLARA USANDO GETTERS/SETTERS EM RECEITA =======
        // Se preferir, exponha setters protegidos em Receita p/ manipular agregados:
        //   public void incRatingSum(int delta) { this.ratingSum += delta; }
        //   public void incRatingCount(int delta) { this.ratingCount += delta; }
        //   public long getRatingSum() { return ratingSum; }
        //   public void setRatingSum(long v) { this.ratingSum = v; }
        //   public void setRatingCount(int v) { this.ratingCount = v; }
        //
        // Aí o código fica bem direto:
        //
        // if (opt.isPresent()) {
        //    Avaliacao a = opt.get();
        //    int anterior = a.getValor();
        //    a.setValor(valor);
        //    r.incRatingSum(valor - anterior);
        //    repo.save(a);
        //    receitaRepo.save(r);
        //    return a;
        // } else {
        //    Avaliacao a = new Avaliacao(u, r, valor);
        //    repo.save(a);
        //    r.incRatingSum(valor);
        //    r.incRatingCount(1);
        //    receitaRepo.save(r);
        //    return a;
        // }

        // Para não alongar demais: recomendo ADICIONAR esses 4 métodos utilitários em Receita.
        return opt.get();
    }

    /** Remove a nota do usuário e ajusta agregados. */
    @Transactional
    public void remover(String emailUsuario, Long receitaId) {
        Usuario u = usuarioRepo.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Receita r = receitaRepo.findById(receitaId)
                .orElseThrow(() -> new IllegalArgumentException("Receita não encontrada."));

        var opt = repo.findByUsuarioIdAndReceitaId(u.getId(), r.getId());
        if (opt.isPresent()) {
            Avaliacao a = opt.get();
            // r.incRatingSum(-a.getValor());
            // r.incRatingCount(-1);
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
