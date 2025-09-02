package br.edu.iff.ccc.webdev.entities;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "receitas")
public class Receita implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 5000)        // ajuste se quiser
    private String ingredientes;

    @Column(length = 10000)       // ajuste se quiser
    private String modoPreparo;

    protected Receita() {} // JPA

    public Receita(String nome, String ingredientes, String modoPreparo) {
        this.nome = nome;
        this.ingredientes = ingredientes;
        this.modoPreparo = modoPreparo;
    }

    // getters/setters
    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIngredientes() { return ingredientes; }
    public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }

    public String getModoPreparo() { return modoPreparo; }
    public void setModoPreparo(String modoPreparo) { this.modoPreparo = modoPreparo; }

    @Override public int hashCode() { return (id == null) ? 0 : id.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receita)) return false;
        Receita other = (Receita) o;
        if (this.id == null || other.id == null) return false;
        return this.id.equals(other.id);
    }
}
