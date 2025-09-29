package br.edu.iff.ccc.webdev.controller.restapi;

import br.edu.iff.ccc.webdev.controller.service.FavoritoService;
import br.edu.iff.ccc.webdev.entities.Receita;
import br.edu.iff.ccc.webdev.exception.UsuarioNaoAutenticadoException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
        if (auth == null) throw new UsuarioNaoAutenticadoException();
        List<Receita> rs = favoritoService.listarReceitasFavoritas(auth.getName());
        return rs.stream().map(ReceitaMinDTO::of).toList();
    }

    // GET /api/v1/favoritos/ids  -> só os IDs das receitas favoritas (para pintar estrelas)
    @GetMapping("/favoritos/ids")
    public Set<Long> idsFavoritos(Authentication auth) {
        if (auth == null) throw new UsuarioNaoAutenticadoException();
        return favoritoService.idsReceitasFavoritasDoUsuario(auth.getName());
    }

    // GET /api/v1/receitas/{id}/favoritos/status -> { "favorita": true/false }
    @GetMapping("/receitas/{id}/favoritos/status")
    public StatusDTO status(@PathVariable Long id, Authentication auth) {
        boolean fav = auth != null && favoritoService.isFavorita(auth.getName(), id);
        return new StatusDTO(fav);
    }

    // POST /api/v1/receitas/{id}/favoritos  -> 201
// FavoritoApiController (ou no controller da Receita)
    @PostMapping("/receitas/{id}/favoritos")
    public ResponseEntity<Void> favoritar(@PathVariable Long id, Authentication auth) {
        if (auth == null) throw new UsuarioNaoAutenticadoException();

        favoritoService.favoritar(auth.getName(), id); // continua void

        // Location pode ser o sub-recurso "favoritos" da receita
        URI location = URI.create("/api/v1/receitas/%d/favoritos".formatted(id));
        return ResponseEntity.created(location).build(); // 201 Created + Location
    }


    // DELETE /api/v1/receitas/{id}/favoritos -> 204
    @DeleteMapping("/receitas/{id}/favoritos")
    public ResponseEntity<?> desfavoritar(@PathVariable Long id, Authentication auth) {
        if (auth == null) throw new UsuarioNaoAutenticadoException();
        favoritoService.desfavoritar(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /* ====== DTOs simples da API ====== */

    public record ReceitaMinDTO(Long id, String nome) {
        static ReceitaMinDTO of(Receita r) { return new ReceitaMinDTO(r.getId(), r.getNome()); }
    }

    public record StatusDTO(boolean favorita) {}

}
