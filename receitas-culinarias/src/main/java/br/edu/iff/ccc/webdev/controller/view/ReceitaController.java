package br.edu.iff.ccc.webdev.controller.view;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.edu.iff.ccc.webdev.controller.service.ReceitaService;
import br.edu.iff.ccc.webdev.entities.Receita;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping(path = "receitas")
public class ReceitaController {

    @Autowired
    ReceitaService receitaService;

    @GetMapping("/")
    public String listarReceitas(Model model) {
        model.addAttribute("receitas", receitaService.findAllReceitas());
        return "listar"; 
    }

    @GetMapping("/{id}")
    public String detalhesReceita(@PathVariable("id") Long id, Model model) {
        Receita receita = receitaService.findReceitaById(id);
        if (receita == null) {
            model.addAttribute("errorMessage", "Receita nao encontrada");
            return "erro";
        }
        model.addAttribute("receita", receita);
        return "detalhes"; 
    }

    @GetMapping("/nova")
    public String novaReceita() {
        return "formulario"; 
    }

    @PostMapping("")
    public String salvarReceita(@Valid Receita receita,BindingResult error, Model model) {
        if(error.hasErrors()){
            model.addAttribute("errorMessage", "Erro ao salvar");
            return "erro";
        }
        receitaService.saveReceita(receita);
        model.addAttribute("successMessage", "Produto salvo");

        return "receitas";
    }
}
