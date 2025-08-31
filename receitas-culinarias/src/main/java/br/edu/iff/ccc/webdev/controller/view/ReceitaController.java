package br.edu.iff.ccc.webdev.controller.view;

import br.edu.iff.ccc.webdev.controller.service.ReceitaService;
import br.edu.iff.ccc.webdev.dto.ReceitaDTO;
import br.edu.iff.ccc.webdev.entities.Receita;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("receitas")
public class ReceitaController {

    private final ReceitaService service;

    public ReceitaController(ReceitaService service) {
        this.service = service;
    }

    /* LISTAR */
    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("receitas", service.findAll()); // List<Receita> (entity)
        return "receitas"; // templates/receitas.html
    }

    /* FORM NOVA */
    @GetMapping("/new")
    public String formNovo(Model model) {
        model.addAttribute("receita", new ReceitaDTO());
        model.addAttribute("modo", "create");
        return "receita_form"; // templates/receita_form.html
    }

    /* CRIAR */
    @PostMapping("")
    public String criar(@Valid @ModelAttribute("receita") ReceitaDTO dto,
                        BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("modo", "create");
            return "receita_form";
        }
        try {
            service.criar(dto);
            ra.addFlashAttribute("successMessage", "Receita criada!");
            return "redirect:/receitas";
        } catch (IllegalArgumentException e) {
            br.reject("erro.cadastro", e.getMessage());
            model.addAttribute("modo", "create");
            return "receita_form";
        }
    }

    /* DETALHES */
    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Receita r = service.findById(id).orElse(null);
        if (r == null) {
            ra.addFlashAttribute("errorMessage", "Receita não encontrada.");
            return "redirect:/receitas";
        }
        model.addAttribute("receita", r); // pode mandar a entity na tela de detalhes
        return "receita_detalhes";        // templates/receita_detalhes.html
    }

    /* FORM EDITAR */
    @GetMapping("/{id}/edit")
    public String formEditar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Receita r = service.findById(id).orElse(null);
        if (r == null) {
            ra.addFlashAttribute("errorMessage", "Receita não encontrada.");
            return "redirect:/receitas";
        }
        // Preenche o DTO a partir da entity
        ReceitaDTO dto = new ReceitaDTO();
        dto.setId(r.getId());
        dto.setNome(r.getNome());
        dto.setIngredientes(r.getIngredientes());
        dto.setModoPreparo(r.getModoPreparo());

        model.addAttribute("receita", dto);
        model.addAttribute("modo", "edit");
        model.addAttribute("id", id);
        return "receita_form";
    }

    /* ATUALIZAR */
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("receita") ReceitaDTO dto,
                            BindingResult br, Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("modo", "edit");
            model.addAttribute("id", id);
            return "receita_form";
        }
        try {
            service.atualizar(id, dto);
            ra.addFlashAttribute("successMessage", "Receita atualizada!");
            return "redirect:/receitas";
        } catch (IllegalArgumentException e) {
            br.reject("erro.atualizar", e.getMessage());
            model.addAttribute("modo", "edit");
            model.addAttribute("id", id);
            return "receita_form";
        }
    }

    /* EXCLUIR */
    @PostMapping("/{id}/delete")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("successMessage", "Receita excluída!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/receitas";
    }
}
