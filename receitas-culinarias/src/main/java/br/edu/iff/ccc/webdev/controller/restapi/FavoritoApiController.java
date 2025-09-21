package br.edu.iff.ccc.webdev.controller.restapi;

import br.edu.iff.ccc.webdev.controller.service.FavoritoService;
import br.edu.iff.ccc.webdev.entities.Receita;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1")
public class FavoritoApiController {

    private final FavoritoService favoritoService;

    public FavoritoApiController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    // GET /api/v1/favoritos  -> lista (id + nome) das receitas favoritas do logado
    @GetMapping("/favoritos")
    public List<ReceitaMinDTO> meusFavoritos(Authentication auth) {
        if (auth == null) throw new UnauthorizedRuntime();
        List<Receita> rs = favoritoService.listarReceitasFavoritas(auth.getName());
        return rs.stream().map(ReceitaMinDTO::of).toList();
    }

    // GET /api/v1/favoritos/ids  -> só os IDs das receitas favoritas (para pintar estrelas)
    @GetMapping("/favoritos/ids")
    public Set<Long> idsFavoritos(Authentication auth) {
        if (auth == null) throw new UnauthorizedRuntime();
        return favoritoService.idsReceitasFavoritasDoUsuario(auth.getName());
    }

    // GET /api/v1/receitas/{id}/favoritos/status -> { "favorita": true/false }
    @GetMapping("/receitas/{id}/favoritos/status")
    public StatusDTO status(@PathVariable Long id, Authentication auth) {
        boolean fav = auth != null && favoritoService.isFavorita(auth.getName(), id);
        return new StatusDTO(fav);
    }

    // POST /api/v1/receitas/{id}/favoritos  -> 201
    @PostMapping("/receitas/{id}/favoritos")
    public ResponseEntity<?> favoritar(@PathVariable Long id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Precisa estar logado.");
        favoritoService.favoritar(auth.getName(), id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // DELETE /api/v1/receitas/{id}/favoritos -> 204
    @DeleteMapping("/receitas/{id}/favoritos")
    public ResponseEntity<?> desfavoritar(@PathVariable Long id, Authentication auth) {
        if (auth == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Precisa estar logado.");
        favoritoService.desfavoritar(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /* ====== DTOs simples da API ====== */

    public record ReceitaMinDTO(Long id, String nome) {
        static ReceitaMinDTO of(Receita r) { return new ReceitaMinDTO(r.getId(), r.getNome()); }
    }

    public record StatusDTO(boolean favorita) {}

    /** Exceção interna só para 401 rápida em métodos GET */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedRuntime extends RuntimeException {}
}
