package br.edu.iff.ccc.webdev.controller.view;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class CategoriaController {
    @GetMapping("/categorias")
    public String listarCategorias(Model model) {
        
        model.addAttribute("categorias","Bebidas");
        return "categoria"; 
    }
}
