package br.edu.iff.ccc.webdev.controller.view;

import br.edu.iff.ccc.webdev.dto.UsuarioDTO;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.controller.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    private boolean isSelf(Long id, Authentication auth) {
        if (auth == null) return false;
        String email = auth.getName();
        return service.findById(id).map(u -> u.getEmail().equalsIgnoreCase(email)).orElse(false);
    }

    /* LISTAR */
    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("usuarios", service.findAll());
        return "usuarios";
    }

    /* FORM NOVO (livre) */
    @GetMapping("/new")
    public String formNovo(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("modo", "create");
        return "usuario_form";
    }

    /* CRIAR (livre) */
    @PostMapping("/new")
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

    /* FORM EDITAR (ADMIN ou o próprio) */
    @GetMapping("/{id}/edit")
    public String formEditar(@PathVariable Long id, Authentication auth,
                             Model model, RedirectAttributes ra) {
        if (!(isAdmin(auth) || isSelf(id, auth))) {
            ra.addFlashAttribute("errorMessage","Sem permissão para editar este usuário.");
            return "redirect:/usuarios";
        }
        Usuario u = service.findById(id).orElse(null);
        if (u == null) { ra.addFlashAttribute("errorMessage","Usuário não encontrado."); return "redirect:/usuarios"; }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome(u.getNome());
        dto.setCpf(u.getCpf());
        dto.setEmail(u.getEmail());

        model.addAttribute("usuario", dto);
        model.addAttribute("modo", "edit");
        model.addAttribute("id", id);
        return "usuario_form";
    }

    /* ATUALIZAR (ADMIN ou o próprio) */
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("usuario") UsuarioDTO dto,
                            BindingResult br, Authentication auth,
                            RedirectAttributes ra, Model model) {
        if (!(isAdmin(auth) || isSelf(id, auth))) {
            ra.addFlashAttribute("errorMessage","Sem permissão para editar este usuário.");
            return "redirect:/usuarios";
        }
        if (br.hasErrors()) { model.addAttribute("modo", "edit"); model.addAttribute("id", id); return "usuario_form"; }
        try {
            service.atualizar(id, dto);
            ra.addFlashAttribute("successMessage", "Usuário atualizado!");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            br.reject("erro.atualizar", e.getMessage());
            model.addAttribute("modo", "edit");
            model.addAttribute("id", id);
            return "usuario_form";
        }
    }

    /* SOLICITAR COZINHEIRO (somente o próprio) */
    @PostMapping("/{id}/solicitar-cozinheiro")
    public String solicitarCozinheiro(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        if (!isSelf(id, auth)) {
            ra.addFlashAttribute("errorMessage","Você só pode solicitar no seu próprio usuário.");
            return "redirect:/usuarios";
        }
        try {
            service.solicitarCozinheiro(id);
            ra.addFlashAttribute("successMessage","Solicitação enviada ao administrador.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    /* APROVAR/REJEITAR (ADMIN) */
    @PostMapping("/{id}/aprovar-cozinheiro")
    @PreAuthorize("hasRole('ADMIN')")
    public String aprovar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.aprovarCozinheiro(id);
            ra.addFlashAttribute("successMessage","Usuário agora é COZINHEIRO.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/rejeitar-cozinheiro")
    @PreAuthorize("hasRole('ADMIN')")
    public String rejeitar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.rejeitarCozinheiro(id);
            ra.addFlashAttribute("successMessage","Solicitação rejeitada.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    /* ===== DETALHES (GET /usuarios/{id}) ===== */
    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model, RedirectAttributes ra) {
        var opt = service.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage","Usuário não encontrado.");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", opt.get());
        return "usuario_detalhes"; // templates/usuario_detalhes.html
    }

    // Excluir: POST /usuarios/{id}/delete (apenas ADMIN)
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            service.excluir(id); // implemente no seu service se ainda não tiver
            ra.addFlashAttribute("successMessage", "Usuário excluído!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
