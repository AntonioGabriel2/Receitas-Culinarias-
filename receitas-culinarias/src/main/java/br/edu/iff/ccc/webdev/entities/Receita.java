package br.edu.iff.ccc.webdev.entities;

import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Receita implements Serializable {

    private static final long SerialVersionUID = 1L;

    @NotNull
    @Id
    private Long id;

    @NotNull(message = "O nome nao pode ser nulo")
    private String nome;

    private String ingredientes;

    private String modoPreparo;


    public Receita(){

    }

    public Receita(@NotNull Long id, @NotNull(message = "O nome nao pode ser nulo") String nome, String ingredientes,
            String modoPreparo) {
        this.id = id;
        this.nome = nome;
        this.ingredientes = ingredientes;
        this.modoPreparo = modoPreparo;
    }

    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIngredientes() { return ingredientes; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }

    public String getModoPreparo() { return modoPreparo; }
    public void setModoPreparo(String modoPreparo) { this.modoPreparo = modoPreparo; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        result = prime * result + ((ingredientes == null) ? 0 : ingredientes.hashCode());
        result = prime * result + ((modoPreparo == null) ? 0 : modoPreparo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Receita other = (Receita) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        if (ingredientes == null) {
            if (other.ingredientes != null)
                return false;
        } else if (!ingredientes.equals(other.ingredientes))
            return false;
        if (modoPreparo == null) {
            if (other.modoPreparo != null)
                return false;
        } else if (!modoPreparo.equals(other.modoPreparo))
            return false;
        return true;
    }

    
}

