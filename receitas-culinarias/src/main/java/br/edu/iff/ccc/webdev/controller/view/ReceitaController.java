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
import br.edu.iff.ccc.webdev.controller.service.FavoritoService;
import br.edu.iff.ccc.webdev.controller.service.ComentarioService;
import br.edu.iff.ccc.webdev.controller.service.AvaliacaoService;
import br.edu.iff.ccc.webdev.entities.Comentario;
import br.edu.iff.ccc.webdev.entities.Ingrediente;

@Controller
@RequestMapping("receitas")
public class ReceitaController {

    private final ReceitaService service;
    private final FavoritoService favoritoService;
    private final ComentarioService comentarioService;
    private final AvaliacaoService avaliacaoService;

    // construtor
    public ReceitaController(ReceitaService service, FavoritoService favoritoService, ComentarioService comentarioService, AvaliacaoService avaliacaoService) {
        this.service = service;
        this.favoritoService = favoritoService;
        this.comentarioService = comentarioService;
        this.avaliacaoService = avaliacaoService;
    }

    private boolean isAdmin(org.springframework.security.core.Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isOwner(Long id, org.springframework.security.core.Authentication auth) {
        return auth != null && service.isOwner(id, auth.getName());
    }

    /* LISTAR */
    @GetMapping({"", "/"})
    public String listar(Model model, org.springframework.security.core.Authentication auth) {
        model.addAttribute("receitas", service.findAllAsc());

        java.util.Set<Long> favIds = java.util.Collections.emptySet();
        if (auth != null) {
            favIds = favoritoService.idsReceitasFavoritasDoUsuario(auth.getName()); // e-mail do logado
        }
        model.addAttribute("favoritos", favIds); // Set<Long> com ids de receitas favoritas
        return "receitas";
    }

    /* FORM NOVA */
    @GetMapping("/new")
    public String formNovo(Model model) {
        model.addAttribute("receita", new ReceitaDTO());
        model.addAttribute("modo", "create");
        return "receita_form";
    }

    /* CRIAR */
    @PostMapping("")
    public String criar(@Valid @ModelAttribute("receita") ReceitaDTO dto,
                        BindingResult br, Model model,
                        RedirectAttributes ra,
                        org.springframework.security.core.Authentication auth) {
        if (br.hasErrors()) {
            model.addAttribute("modo", "create");
            return "receita_form";
        }
        try {
            if (auth == null) {
                ra.addFlashAttribute("errorMessage", "É necessário estar logado.");
                return "redirect:/login";
            }
            service.criar(dto, auth.getName()); // passa o dono
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
    public String detalhes(@PathVariable Long id,
                        Model model,
                        RedirectAttributes ra,
                        org.springframework.security.core.Authentication auth) {

        var r = service.findById(id).orElse(null);
        if (r == null) {
            ra.addFlashAttribute("errorMessage", "Receita não encontrada.");
            return "redirect:/receitas";
        }
        model.addAttribute("receita", r);

        model.addAttribute("mediaNota", r.getRatingMedia());
        model.addAttribute("qtdeNotas", r.getRatingCount());

        Integer minhaNota = (auth != null)
                ? /* injete AvaliacaoService e chame */ avaliacaoService.notaDoUsuario(auth.getName(), id)
                : null;
        model.addAttribute("minhaNota", minhaNota);

        // ADMIN vê inclusive os apagados; outros só os não-apagados
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("comentarios", comentarioService.listarPorReceita(id, isAdmin));

        // status de favorito do logado
        boolean favoritada = auth != null && auth.isAuthenticated()
                && favoritoService.isFavorita(auth.getName(), r.getId());
        model.addAttribute("favoritada", favoritada);

        return "receita_detalhes";
    }

    /* FORM EDITAR — ADMIN ou DONO */
    @GetMapping("/{id}/edit")
    public String formEditar(@PathVariable Long id,
                             Model model,
                             RedirectAttributes ra,
                             org.springframework.security.core.Authentication auth) {
        if (!(isAdmin(auth) || isOwner(id, auth))) {
            ra.addFlashAttribute("errorMessage", "Sem permissão para editar esta receita.");
            return "redirect:/receitas";
        }
        Receita r = service.findById(id).orElse(null);
        if (r == null) {
            ra.addFlashAttribute("errorMessage", "Receita não encontrada.");
            return "redirect:/receitas";
        }

        ReceitaDTO dto = new ReceitaDTO();
        dto.setId(r.getId());
        dto.setNome(r.getNome());
        dto.setModoPreparo(r.getModoPreparo());

        // List<Ingrediente> -> String (um por linha)
        String ingredientesTexto = r.getIngredientes().stream()
                .map(Ingrediente::getNome) // ajuste o getter conforme sua classe
                .collect(java.util.stream.Collectors.joining("\n"));
        dto.setIngredientes(ingredientesTexto);

        model.addAttribute("receita", dto);
        model.addAttribute("modo", "edit");
        model.addAttribute("id", id);
        return "receita_form";
    }

    /* ATUALIZAR — ADMIN ou DONO */
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("receita") ReceitaDTO dto,
                            BindingResult br, Model model,
                            RedirectAttributes ra,
                            org.springframework.security.core.Authentication auth) {
        if (!(isAdmin(auth) || isOwner(id, auth))) {
            ra.addFlashAttribute("errorMessage", "Sem permissão para editar esta receita.");
            return "redirect:/receitas";
        }
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

    /* EXCLUIR — ADMIN ou DONO */
    @PostMapping("/{id}/delete")
    public String excluir(@PathVariable Long id,
                          RedirectAttributes ra,
                          org.springframework.security.core.Authentication auth) {
        if (!(isAdmin(auth) || isOwner(id, auth))) {
            ra.addFlashAttribute("errorMessage", "Sem permissão para excluir esta receita.");
            return "redirect:/receitas";
        }
        try {
            service.excluir(id);
            ra.addFlashAttribute("successMessage", "Receita excluída!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/receitas";
    }
}
