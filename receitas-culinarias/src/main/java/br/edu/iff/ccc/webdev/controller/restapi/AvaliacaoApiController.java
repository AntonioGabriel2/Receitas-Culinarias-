// src/main/java/br/edu/iff/ccc/webdev/controller/restapi/AvaliacaoApiController.java
package br.edu.iff.ccc.webdev.controller.restapi;

import br.edu.iff.ccc.webdev.controller.service.AvaliacaoService;
import br.edu.iff.ccc.webdev.controller.service.ReceitaService;
import br.edu.iff.ccc.webdev.entities.Receita;
import org.springframework.http.HttpStatus;
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
        Receita r = receitaService.findById(id).orElse(null);
        if (r == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receita não encontrada.");
        }
        Integer minha = (auth != null) ? service.notaDoUsuario(auth.getName(), id) : null;
        return ResponseEntity.ok(new RatingView(r.getRatingMedia(), r.getRatingCount(), minha));
    }

    /** PUT sem sufixo: cria/atualiza a nota do usuário (1..5). */
    @PutMapping
    public ResponseEntity<?> put(@PathVariable Long id,
                                 @RequestBody RatingRequest req,
                                 Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        try {
            service.avaliar(auth.getName(), id, req.valor());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** DELETE sem sufixo: remove a nota do usuário. */
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
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
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        Integer v = service.notaDoUsuario(auth.getName(), id);
        return (v == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(new Minha(v));
    }

    /* ===== DTOs ===== */

    public record RatingRequest(int valor) {}
    public record RatingView(double media, int votos, Integer minha) {}
    public record Minha(int valor) {}
}