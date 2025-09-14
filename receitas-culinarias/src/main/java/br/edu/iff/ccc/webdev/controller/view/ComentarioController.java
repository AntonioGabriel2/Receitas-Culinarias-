package br.edu.iff.ccc.webdev.controller.view;

import br.edu.iff.ccc.webdev.controller.service.ComentarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    // POST /receitas/{id}/comentarios  (criar)
    @PostMapping("/receitas/{id}/comentarios")
    public String criar(@PathVariable Long id,
                        @RequestParam("texto") String texto,
                        Authentication auth,
                        RedirectAttributes ra) {

        if (auth == null) {
            ra.addFlashAttribute("errorMessage", "Faça login para comentar.");
            return "redirect:/login";
        }
        try {
            comentarioService.criar(id, auth.getName(), texto);
            ra.addFlashAttribute("successMessage", "Comentário publicado!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        // volta para a receita e ancora na seção de comentários
        return "redirect:/receitas/" + id + "#comentarios";
    }

    // POST /comentarios/{id}/delete  (soft delete)
    @PostMapping("/comentarios/{id}/delete")
    public String excluir(@PathVariable Long id,
                          Authentication auth,
                          RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage", "Ação requer login.");
            return "redirect:/login";
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        try {
            comentarioService.excluirSoft(id, auth.getName(), isAdmin);
            ra.addFlashAttribute("successMessage", "Comentário excluído.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        Long receitaId = comentarioService.receitaIdDoComentario(id).orElse(null);
        return receitaId != null ? "redirect:/receitas/" + receitaId + "#comentarios"
                                 : "redirect:/receitas";
    }
}
