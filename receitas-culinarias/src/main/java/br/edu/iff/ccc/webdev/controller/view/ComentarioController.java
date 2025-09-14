package br.edu.iff.ccc.webdev.controller.view;

import br.edu.iff.ccc.webdev.controller.service.ComentarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    // Adicionar comentário
    @PostMapping("/receitas/{id}/comentarios")
    public String adicionar(@PathVariable("id") Long receitaId,
                            @RequestParam("texto") String texto,
                            Authentication auth,
                            RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage", "Faça login para comentar.");
            return "redirect:/login";
        }
        try {
            comentarioService.adicionar(receitaId, auth.getName(), texto);
            ra.addFlashAttribute("successMessage", "Comentário publicado!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/receitas/" + receitaId;
    }

    // Soft delete
    @PostMapping("/comentarios/{id}/delete")
    public String excluir(@PathVariable("id") Long comentarioId,
                          Authentication auth,
                          RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage", "Faça login.");
            return "redirect:/login";
        }
        try {
            boolean admin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            comentarioService.softDelete(comentarioId, auth.getName(), admin);
            ra.addFlashAttribute("successMessage", "Comentário excluído.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        // redireciona para a receita (descobrir ID pela própria camada de serviço é possível,
        // mas aqui vamos pelo “depois” com âncora genérica)
        return "redirect:/receitas/" + comentarioId; // se preferir, capture a receita antes e redirecione certo
    }
}
