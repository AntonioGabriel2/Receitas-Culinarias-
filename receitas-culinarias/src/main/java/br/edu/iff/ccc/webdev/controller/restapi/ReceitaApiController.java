package br.edu.iff.ccc.webdev.controller.restapi;

import br.edu.iff.ccc.webdev.controller.service.ReceitaService;
import br.edu.iff.ccc.webdev.dto.ReceitaDTO;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.exception.ReceitaNaoEncontrada;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/receitas")
public class ReceitaApiController {

    private final ReceitaService service;

    public ReceitaApiController(ReceitaService service) {
        this.service = service;
    }

    /* ========== GET (leitura pública) ========== */

    // GET /api/v1/receitas  -> lista simples (id + nome)
    @GetMapping
    public List<ReceitaListView> listarTodas() {
        return service.findAllAsc().stream()
                .map(ReceitaListView::of)
                .toList();
    }

    // GET /api/v1/receitas/{id} -> detalhes (sem comentários)
    @GetMapping("/{id}")
    public ResponseEntity<?> detalhes(@PathVariable Long id) {
        return service.findById(id)
                .<ResponseEntity<?>>map(r -> ResponseEntity.ok(ReceitaDetailView.of(r)))
                .orElseThrow(() -> new ReceitaNaoEncontrada(id));
    }

    /* ========== POST/PUT/DELETE (exigem permissão) ========== */

    // POST /api/v1/receitas  (ADMIN ou COZINHEIRO)
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ReceitaDTO dto,
                                   org.springframework.security.core.Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        }
        boolean pode = auth.getAuthorities().stream().anyMatch(a ->
                "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_COZINHEIRO".equals(a.getAuthority())
        );
        if (!pode) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para criar receitas.");
        }
        try {
            Receita r = service.criar(dto, auth.getName()); // define dono pelo e-mail do logado
            URI location = URI.create("/api/v1/receitas/" + r.getId());
            return ResponseEntity.created(location).body(ReceitaDetailView.of(r));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/v1/receitas/{id} (ADMIN ou DONO)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @RequestBody ReceitaDTO dto,
                                       org.springframework.security.core.Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        }
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean dono = service.isOwner(id, auth.getName());

        if (!(admin || dono)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para editar esta receita.");
        }
        try {
            Receita r = service.atualizar(id, dto);
            return ResponseEntity.ok(ReceitaDetailView.of(r));
        } catch (IllegalArgumentException e) {
            // "Receita não encontrada." ou validação
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/v1/receitas/{id} (ADMIN ou DONO)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id,
                                     org.springframework.security.core.Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        }
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean dono = service.isOwner(id, auth.getName());

        if (!(admin || dono)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para excluir esta receita.");
        }
        try {
            service.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /* ====== DTOs simples da API (records) ====== */

    public record ReceitaListView(Long id, String nome) {
        static ReceitaListView of(Receita r) { return new ReceitaListView(r.getId(), r.getNome()); }
    }

    // dentro de ReceitaApiController

    public record ReceitaDetailView(
            Long id,
            String nome,
            List<ItemIngrediente> ingredientes,
            String modoPreparo
    ) {
        static ReceitaDetailView of(Receita r) {
            var itens = r.getIngredientes().stream()
                    .map(i -> new ItemIngrediente(i.getNome(), i.getQuantidade()))
                    .toList();
            return new ReceitaDetailView(r.getId(), r.getNome(), itens, r.getModoPreparo());
        }

        public record ItemIngrediente(String nome, String quantidade) {}
    }

}