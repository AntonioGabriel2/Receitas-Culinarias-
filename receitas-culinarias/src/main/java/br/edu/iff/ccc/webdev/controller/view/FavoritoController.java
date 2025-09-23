package br.edu.iff.ccc.webdev.controller.view;

import br.edu.iff.ccc.webdev.controller.service.FavoritoService;
import br.edu.iff.ccc.webdev.entities.Receita;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    // POST /receitas/{id}/favoritar
    @PostMapping("/receitas/{id}/favoritar")
    public String favoritar(@PathVariable Long id,
                            Authentication auth,
                            RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage", "Faça login para favoritar.");
            return "redirect:/login";
        }
        try {
            favoritoService.favoritar(auth.getName(), id);
            ra.addFlashAttribute("successMessage", "Adicionada aos favoritos!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/receitas/";
    }

    // POST /receitas/{id}/desfavoritar
    @PostMapping("/receitas/{id}/desfavoritar")
    public String desfavoritar(@PathVariable Long id,
                               Authentication auth,
                               RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage", "Faça login para desfavoritar.");
            return "redirect:/login";
        }
        favoritoService.desfavoritar(auth.getName(), id);
        ra.addFlashAttribute("successMessage", "Removida dos favoritos.");
        return "redirect:/receitas/";
    }

    // GET /favoritos  (lista só do logado)
    @GetMapping("/favoritos")
    public String meusFavoritos(Authentication auth, Model model, RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage", "Faça login para ver seus favoritos.");
            return "redirect:/login";
        }
        List<Receita> receitas = favoritoService.listarReceitasFavoritas(auth.getName());
        model.addAttribute("receitas", receitas);
        return "favoritos"; // templates/favoritos.html
    }
}
