// src/main/java/br/edu/iff/ccc/webdev/controller/restapi/AvaliacaoApiController.java
package br.edu.iff.ccc.webdev.controller.restapi;

import br.edu.iff.ccc.webdev.controller.service.AvaliacaoService;
import br.edu.iff.ccc.webdev.controller.service.ReceitaService;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.exception.AvaliacaoInvalidaException;
import br.edu.iff.ccc.webdev.exception.ReceitaNaoEncontrada;
import br.edu.iff.ccc.webdev.exception.UsuarioNaoAutenticadoException;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/receitas/{id}/rating", "/api/v1/receitas/{id}/avaliacoes"})
public class AvaliacaoApiController {

    private final AvaliacaoService service;
    private final ReceitaService receitaService;

    public AvaliacaoApiController(AvaliacaoService service, ReceitaService receitaService) {
        this.service = service;
        this.receitaService = receitaService;
    }

    /** GET sem sufixo: retorna média/votos e, se logado, a nota do usuário (minha). */
    @GetMapping
    public ResponseEntity<?> get(@PathVariable Long id, Authentication auth) {
    Receita r = receitaService.findById(id)
        .orElseThrow(() -> new ReceitaNaoEncontrada(id));
        Integer minha = (auth != null) ? service.notaDoUsuario(auth.getName(), id) : null;
        return ResponseEntity.ok(new RatingView(r.getRatingMedia(), r.getRatingCount(), minha));
    }

    /** PUT sem sufixo: cria/atualiza a nota do usuário (1..5). */
    @PutMapping
    public ResponseEntity<Void> put(@PathVariable Long id,
                                    @RequestBody RatingRequest req,
                                    Authentication auth) {
        if (auth == null) throw new UsuarioNaoAutenticadoException();

        // se não tinha nota, será criação → 201 + Location
        boolean criando = (service.notaDoUsuario(auth.getName(), id) == null);

        service.avaliar(auth.getName(), id, req.valor());

        if (criando) {
            URI location = URI.create("/api/v1/receitas/%d/avaliacoes/minha".formatted(id));
            return ResponseEntity.created(location).build(); // 201 Created
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }



    /** DELETE sem sufixo: remove a nota do usuário. */
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        if (auth == null) throw new UsuarioNaoAutenticadoException();
        service.remover(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /* ===== Aliases para compatibilidade com o HTML atual ===== */

    /** POST sem sufixo (alias): delega para o PUT acima. */
    @PostMapping
    public ResponseEntity<?> post(@PathVariable Long id,
                                  @RequestBody RatingRequest req,
                                  Authentication auth) {
        return put(id, req, auth);
    }

    /** GET /minha: retorna somente a nota do usuário logado. */
    @GetMapping("/minha")
    public ResponseEntity<?> minha(@PathVariable Long id, Authentication auth) {
        if (auth == null) throw new UsuarioNaoAutenticadoException();
        Integer v = service.notaDoUsuario(auth.getName(), id);
        return (v == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(new Minha(v));
    }

    /* ===== DTOs ===== */

    public record RatingRequest(int valor) {}
    public record RatingView(double media, int votos, Integer minha) {}
    public record Minha(int valor) {}
}