package br.edu.iff.ccc.webdev.controller.restapi;

import br.edu.iff.ccc.webdev.controller.service.ComentarioService;
import br.edu.iff.ccc.webdev.entities.Comentario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Comentários", description = "Comentários de receitas")
@RestController
@RequestMapping("/api/v1")
public class ComentarioApiController {

    private final ComentarioService service;

    public ComentarioApiController(ComentarioService service) {
        this.service = service;
    }

    // GET /api/receitas/{id}/comentarios?incluirApagados=true|false
    @Operation(summary = "Lista comentários da receita")
    @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
    @ApiResponse(responseCode = "404", description = "Receita não encontrada", content = @Content(mediaType = "application/problem+json"))
    })
    @GetMapping("/receitas/{id}/comentarios")
    public List<ComentarioView> listar(@PathVariable Long id,
                                       @RequestParam(defaultValue = "false") boolean incluirApagados,
                                       Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var lista = service.listarPorReceita(id, incluirApagados && isAdmin);
        return lista.stream().map(ComentarioView::of).toList();
    }

    // POST /api/receitas/{id}/comentarios   { "texto": "..." }
    @Operation(summary = "Cria comentário na receita")
    @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Criado",
        headers = @Header(name = "Location", description = "URL do comentário criado")),
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/problem+json")),
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(mediaType = "application/problem+json"))
    })
    @PostMapping("/receitas/{id}/comentarios")
    public ResponseEntity<?> criar(@PathVariable Long id,
                                @RequestBody NovoComentario req,
                                Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        Comentario c = service.criar(id, auth.getName(), req.texto());
        URI location = URI.create("/api/v1/receitas/%d/comentarios/%d".formatted(id, c.getId()));
        return ResponseEntity.created(location).body(ComentarioView.of(c));
    }


    // DELETE /api/comentarios/{id}  (soft delete)
    @Operation(summary = "Remove comentário por id")
    @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Excluído"),
    @ApiResponse(responseCode = "404", description = "Comentário/Receita não encontrado", content = @Content(mediaType = "application/problem+json"))
    })
    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Precisa estar logado.");
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        try {
            service.excluirSoft(id, auth.getName(), isAdmin);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ===== DTOs da API =====

    public record NovoComentario(String texto) { }

    public record ComentarioView(
            Long id,
            String texto,
            String autorNome,
            String autorEmail,
            LocalDateTime criadoEm,
            boolean apagado,
            LocalDateTime apagadoEm,
            String apagadoPor
    ) {
        static ComentarioView of(Comentario c) {
            return new ComentarioView(
                    c.getId(),
                    c.getTexto(),
                    c.getAutor() != null ? c.getAutor().getNome() : null,
                    c.getAutor() != null ? c.getAutor().getEmail() : null,
                    c.getCriadoEm(),
                    c.isApagado(),
                    c.getApagadoEm(),
                    (c.getApagadoPor() != null ? c.getApagadoPor().getNome() : null)
            );
        }
    }
}
