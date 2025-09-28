// src/main/java/br/edu/iff/ccc/webdev/controller/view/AvaliacaoController.java
package br.edu.iff.ccc.webdev.controller.view;

import br.edu.iff.ccc.webdev.controller.service.AvaliacaoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) { this.service = service; }

    @PostMapping("/receitas/{id}/avaliar")
    public String avaliar(@PathVariable Long id,
                          @RequestParam("valor") int valor,
                          Authentication auth,
                          RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage","Faça login para avaliar.");
            return "redirect:/login";
        }
        try {
            service.avaliar(auth.getName(), id, valor);
            ra.addFlashAttribute("successMessage","Obrigado pela sua avaliação!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/receitas/" + id + "#avaliacao";
    }

    @PostMapping("/receitas/{id}/avaliacao/remover")
    public String remover(@PathVariable Long id,
                          Authentication auth,
                          RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage","Faça login para remover avaliação.");
            return "redirect:/login";
        }
        service.remover(auth.getName(), id);
        ra.addFlashAttribute("successMessage","Sua avaliação foi removida.");
        return "redirect:/receitas/" + id + "#avaliacao";
    }
}
