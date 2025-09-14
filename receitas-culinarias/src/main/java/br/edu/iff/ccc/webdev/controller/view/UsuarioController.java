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

    // ajustar: detalhes só ADMIN ou o próprio
    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id,
                        Model model,
                        RedirectAttributes ra,
                        org.springframework.security.core.Authentication auth) {
        // permitir admin ou dono
        boolean admin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean self = auth != null && service.findById(id)
                .map(u -> u.getEmail().equalsIgnoreCase(auth.getName()))
                .orElse(false);

        if (!(admin || self)) {
            ra.addFlashAttribute("errorMessage","Sem permissão para ver este usuário.");
            return "redirect:/";
        }

        var opt = service.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage","Usuário não encontrado.");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuario", opt.get());
        return "usuario_detalhes";
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

    /* ====== ENDPOINTS /usuarios/me ====== */

    // Descobrir ID do logado pelo email do Authentication
    private Long myId(Authentication auth) {
        if (auth == null) return null;
        return service.findByEmail(auth.getName())
                    .map(Usuario::getId)
                    .orElse(null);
    }

    // novo: /usuarios/me -> redireciona para /usuarios/{id do logado}
    @GetMapping("/me")
    public String meuPerfil(Authentication auth, RedirectAttributes ra) {
        if (auth == null) {
            ra.addFlashAttribute("errorMessage", "Faça login para ver seu perfil.");
            return "redirect:/login";
        }
        // precisa de um método no service para buscar por e-mail
        var opt = service.findByEmail(auth.getName());
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/";
        }
        return "redirect:/usuarios/" + opt.get().getId();
    }

    // Form de edição do meu perfil (reaproveita /{id}/edit)
    @GetMapping("/me/editar")
    public String editarMeuPerfil(Authentication auth, RedirectAttributes ra) {
        Long id = myId(auth);
        if (id == null) { ra.addFlashAttribute("errorMessage","Usuário não encontrado."); return "redirect:/login"; }
        return "redirect:/usuarios/" + id + "/edit";
    }

    // Solicitar virar cozinheiro (reaproveita /{id}/solicitar-cozinheiro)
    // próprio usuário
    @PostMapping("/me/solicitar-cozinheiro")
    public String solicitarCozinheiroMeu(Authentication auth, RedirectAttributes ra) {
        Long id = myId(auth);
        if (id == null) {
            ra.addFlashAttribute("errorMessage","Usuário não encontrado.");
            return "redirect:/login";
        }
        try {
            service.solicitarCozinheiro(id);
            ra.addFlashAttribute("successMessage","Solicitação enviada ao administrador.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/usuarios/" + id;
    }


}
