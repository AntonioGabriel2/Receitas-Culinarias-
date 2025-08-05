package br.edu.iff.ccc.webdev.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "principal")
public class MainViewController {

    @GetMapping("/{id}")
    public String getRecipePage(@PathVariable("id") String id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("nomeReceita", "Bolo de Chocolate");
        model.addAttribute("ingredientes", "Farinha, Açúcar, Chocolate em pó, Ovos, Leite, Fermento");
        model.addAttribute("modoPreparo", "Misture os ingredientes, asse em forno pré-aquecido a 180ºC por 40 minutos.");
        model.addAttribute("tempoPreparo", "1 hora");
        model.addAttribute("categoriaReceita", "Sobremesas");

        return "index.html";
    }
}