package br.edu.iff.ccc.webdev.controller.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import br.edu.iff.ccc.webdev.entities.Receita;

@Service
public class ReceitaService {
    public void saveReceita(Receita Receita){


    }

    public Receita findReceitaById(Long id) {
        Receita receita = new Receita();

        if(id == null){
            return null;
        }

        receita.setId(id);
        receita.setIngredientes("Arroz, Sal, agua");
        receita.setModoPreparo("Deixe a agua ferver e jogue o arroz e o sal :)");
        receita.setNome("Arroz Cozido");
        return receita;
        
    }
    
    public ArrayList<Receita> findAllReceitas() {
        Receita r1 = new Receita(1L, "feijao", "aaaa","ksksksk");
        Receita r2 = new Receita(1L, "feijao", "aaaa","ksksksk");
        Receita r3 = new Receita(1L, "feijao", "aaaa","ksksksk");

        ArrayList<Receita> receitas = new ArrayList<>();
        receitas.add(r1);
        receitas.add(r2);
        receitas.add(r3);
        return receitas;
        
    }
    
}
