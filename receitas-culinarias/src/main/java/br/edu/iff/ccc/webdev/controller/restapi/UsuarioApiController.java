package br.edu.iff.ccc.webdev.controller.restapi;

import br.edu.iff.ccc.webdev.controller.service.UsuarioService;
import br.edu.iff.ccc.webdev.dto.UsuarioDTO;
import br.edu.iff.ccc.webdev.entities.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.security.core.Authentication;

import java.net.URI;
import java.util.List;

@Tag(name = "Usuários", description = "Cadastro e gerenciamento de usuários")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioApiController {

    private final UsuarioService service;

    public UsuarioApiController(UsuarioService service) {
        this.service = service;
    }

    /* ======================= GETs ======================= */

    // GET /api/v1/usuarios  (lista todos) — ADMIN
    @GetMapping
    public ResponseEntity<?> listarTodos(Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas ADMIN.");
        List<Usuario> lista = service.findAll();
        return ResponseEntity.ok(lista.stream().map(UsuarioListView::of).toList());
    }

    // GET /api/v1/usuarios/{id} — ADMIN ou o próprio
    @Operation(summary = "Obtém usuário por id")
    @ApiResponses({
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/problem+json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> detalhes(@PathVariable Long id, Authentication auth) {
        if (!isAdmin(auth) && !isSelf(id, auth))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão.");

        return service.findById(id)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(UsuarioView.of(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado."));
    }

    // GET /api/v1/usuarios/me — autenticado
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        return service.findByEmail(auth.getName())
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(UsuarioView.of(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado."));
    }

    /* ======================= POSTs ======================= */

    // POST /api/v1/usuarios  (cadastro público)
    @Operation(summary = "Cadastra novo usuário")
    @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Criado",
        headers = @Header(name = "Location", description = "URL do usuário criado")),
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/problem+json")),
    @ApiResponse(responseCode = "409", description = "E-mail/CPF já cadastrado", content = @Content(mediaType = "application/problem+json"))
    })
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody NovoUsuario req) {
        try {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNome(req.nome());
            dto.setCpf(req.cpf());
            dto.setEmail(req.email());
            dto.setSenha(req.senha());

            Usuario u = service.cadastrar(dto);
            URI location = URI.create("/api/v1/usuarios/" + u.getId());
            return ResponseEntity.created(location).body(UsuarioView.of(u));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/v1/usuarios/me/solicitar-cozinheiro — próprio usuário
    @PostMapping("/me/solicitar-cozinheiro")
    public ResponseEntity<?> solicitarCozinheiro(Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        var opt = service.findByEmail(auth.getName());
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        try {
            service.solicitarCozinheiro(opt.get().getId());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/v1/usuarios/{id}/aprovar-cozinheiro — ADMIN
    @PostMapping("/{id}/aprovar-cozinheiro")
    public ResponseEntity<?> aprovar(@PathVariable Long id, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas ADMIN.");
        try {
            service.aprovarCozinheiro(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/v1/usuarios/{id}/rejeitar-cozinheiro — ADMIN
    @PostMapping("/{id}/rejeitar-cozinheiro")
    public ResponseEntity<?> rejeitar(@PathVariable Long id, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas ADMIN.");
        try {
            service.rejeitarCozinheiro(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* ======================= PUT/DELETE ======================= */

    // PUT /api/v1/usuarios/{id} — ADMIN ou o próprio (CPF não muda)
    @Operation(summary = "Atualiza usuário por id")
    @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Atualizado", content = @Content(mediaType = "application/json")),
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/problem+json")),
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/problem+json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @RequestBody AtualizaUsuario req,
                                       Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login.");
        if (!isAdmin(auth) && !isSelf(id, auth))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão.");

        try {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNome(req.nome());
            dto.setEmail(req.email());
            dto.setSenha(req.senha()); // se vier null/vazio, o service mantém a senha atual
            Usuario u = service.atualizar(id, dto);
            return ResponseEntity.ok(UsuarioView.of(u));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/v1/usuarios/{id} — ADMIN
    @Operation(summary = "Remove usuário por id")
    @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Excluído"),
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/problem+json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id, Authentication auth) {
        if (!isAdmin(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas ADMIN.");
        try {
            service.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /* =================== helpers de permissão =================== */

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private boolean isSelf(Long id, Authentication auth) {
        if (auth == null) return false;
        return service.findById(id)
                .map(u -> u.getEmail().equalsIgnoreCase(auth.getName()))
                .orElse(false);
    }

    /* ======================= DTOs da API ======================= */

    public record UsuarioListView(Long id, String nome, String email, String perfil) {
        static UsuarioListView of(Usuario u) {
            return new UsuarioListView(u.getId(), u.getNome(), u.getEmail(), u.getPerfil().name());
        }
    }

    public record UsuarioView(Long id, String nome, String cpf, String email,
                              String perfil, boolean pedidoCozinheiroPendente) {
        static UsuarioView of(Usuario u) {
            return new UsuarioView(
                    u.getId(), u.getNome(), u.getCpf(), u.getEmail(),
                    u.getPerfil().name(), u.isPedidoCozinheiroPendente()
            );
        }
    }

    // payloads de entrada
    public record NovoUsuario(String nome, String cpf, String email, String senha) { }
    public record AtualizaUsuario(String nome, String email, String senha) { }
}
