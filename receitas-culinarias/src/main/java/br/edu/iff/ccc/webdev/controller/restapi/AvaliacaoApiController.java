// src/main/java/br/edu/iff/ccc/webdev/controller/restapi/AvaliacaoApiController.java
package br.edu.iff.ccc.webdev.controller.restapi;

import br.edu.iff.ccc.webdev.controller.service.AvaliacaoService;
import br.edu.iff.ccc.webdev.controller.service.ReceitaService;
import br.edu.iff.ccc.webdev.entities.Receita;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receitas/{id}/rating")
public class AvaliacaoApiController {

    private final AvaliacaoService service;
    private final ReceitaService receitaService;

    public AvaliacaoApiController(AvaliacaoService service, ReceitaService receitaService) {
        this.service = service;
        this.receitaService = receitaService;
    }

    // GET: média, votos e (se logado) minha nota
    @GetMapping
    public ResponseEntity<?> get(@PathVariable Long id,
                                 org.springframework.security.core.Authentication auth) {
        Receita r = receitaService.findById(id)
                .orElse(null);
        if (r == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receita não encontrada.");

        Integer minha = (auth != null) ? service.notaDoUsuario(auth.getName(), id) : null;
        return ResponseEntity.ok(new RatingView(r.getRatingMedia(), r.getRatingCount(), minha));
    }

    // PUT: definir/atualizar minha nota
    @PutMapping
    public ResponseEntity<?> put(@PathVariable Long id,
                                 @RequestBody RatingRequest req,
                                 org.springframework.security.core.Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        try {
            service.avaliar(auth.getName(), id, req.valor());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE: remover minha nota
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    org.springframework.security.core.Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        service.remover(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    // DTOs da API
    public record RatingRequest(int valor) {}
    public record RatingView(double media, int votos, Integer minha) {}
}
