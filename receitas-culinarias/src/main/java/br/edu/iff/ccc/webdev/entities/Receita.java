package br.edu.iff.ccc.webdev.entities;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Receita implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    @NotBlank(message= "O nome não pode ser vazio ou só espaços")
    private String nome;

    private String ingredientes;

    private String modoPreparo;

    
    public Receita(){

    }

    public Receita(String nome, String ingredientes, String modoPreparo) {
        this.nome = nome;
        this.ingredientes = ingredientes;
        this.modoPreparo = modoPreparo;
    }

    
    // Getters e Setters
    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIngredientes() { return ingredientes; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }

    public String getModoPreparo() { return modoPreparo; }
    public void setModoPreparo(String modoPreparo) { this.modoPreparo = modoPreparo; }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Receita)) return false;
        Receita other = (Receita) obj;
        if (this.id == null || other.id == null) return false;
        return this.id.equals(other.id);
    }
    
}

