// src/main/java/br/edu/iff/ccc/webdev/entities/Ingrediente.java
package br.edu.iff.ccc.webdev.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 60)
    private String quantidade; // ex.: "2 xícaras", "1 pitada"

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receita_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_ingrediente_receita"))
    @JsonBackReference
    private Receita receita;

    protected Ingrediente() {}
    public Ingrediente(String nome, String quantidade, Receita receita) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.receita = receita;
    }

    // getters/setters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getQuantidade() { return quantidade; }
    public void setQuantidade(String quantidade) { this.quantidade = quantidade; }
    public Receita getReceita() { return receita; }
    public void setReceita(Receita receita) { this.receita = receita; }
}
