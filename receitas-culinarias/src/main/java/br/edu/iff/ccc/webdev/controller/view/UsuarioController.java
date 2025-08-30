package br.edu.iff.ccc.webdev.controller.view;

import br.edu.iff.ccc.webdev.dto.UsuarioDTO;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.controller.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("usuarios")
public class UsuarioController {

    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }

    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("usuarios", service.findAll());
        return "usuarios";
    }

    @GetMapping("/new")
    public String formNovo(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("modo", "create");
        return "usuario_form";
    }

    @PostMapping("")
    public String criar(@Valid @ModelAttribute("usuario") UsuarioDTO dto,
                        BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "usuario_form";
        try {
            service.cadastrar(dto);
            ra.addFlashAttribute("successMessage", "Usuário cadastrado!");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            br.reject("erro.cadastro", e.getMessage());
            return "usuario_form";
        }
    }

    @GetMapping("/{id}/edit")
    public String formEditar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Usuario u = service.findById(id).orElse(null);
        if (u == null) { ra.addFlashAttribute("errorMessage","Usuário não encontrado."); return "redirect:/usuarios"; }
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome(u.getNome());
        dto.setCpf(u.getCpf());     // mostrar como readonly no form
        dto.setEmail(u.getEmail());
        model.addAttribute("usuario", dto);
        model.addAttribute("modo", "edit");
        model.addAttribute("id", id);
        return "usuario_form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("usuario") UsuarioDTO dto,
                            BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "usuario_form";
        try {
            service.atualizar(id, dto);
            ra.addFlashAttribute("successMessage", "Usuário atualizado!");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            br.reject("erro.atualizar", e.getMessage());
            return "usuario_form";
        }
    }

    // DETALHES
    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Usuario u = service.findById(id).orElse(null);
        if (u == null) {
            ra.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", u); // pode ser a entity aqui
        return "usuario_detalhes";        // template abaixo
    }

// EXCLUIR
    @PostMapping("/{id}/delete")
    public String deletar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id);
            ra.addFlashAttribute("successMessage", "Usuário excluído!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/usuarios";
    }

}
