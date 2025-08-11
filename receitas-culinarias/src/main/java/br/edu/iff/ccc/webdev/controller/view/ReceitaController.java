package br.edu.iff.ccc.webdev.controller.view;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class ReceitaController {
    @GetMapping("/receitas")
    public String listarReceitas(Model model) {
        
        model.addAttribute("receitas", new String[]{"Bolo de Cenoura", "Feijoada", "Lasanha"});
        return "listar"; 
    }

    @GetMapping("/receitas/{id}")
    public String detalhesReceita(@PathVariable Long id, Model model) {
        
        model.addAttribute("receita", "Receita Exemplo ID: " + id);
        return "detalhes"; 
    }

    @GetMapping("/receitas/nova")
    public String novaReceita() {
        return "formulario"; 
    }

    @PostMapping("/receitas")
    public String salvarReceita() {
        // Aqui depois salvaremos no banco
        return "receitas";
    }
}
