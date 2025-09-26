// src/main/java/br/edu/iff/ccc/webdev/entities/Avaliacao.java
package br.edu.iff.ccc.webdev.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "avaliacoes",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_avaliacao_usuario_receita",
        columnNames = {"usuario_id", "receita_id"}
    )
)
public class Avaliacao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_avaliacao_usuario"))
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_avaliacao_receita"))
    private Receita receita;

    /** 1..5 estrelas */
    @Column(nullable = false)
    private int valor;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @Column
    private LocalDateTime atualizadoEm;

    protected Avaliacao() {}

    public Avaliacao(Usuario u, Receita r, int valor) {
        this.usuario = u;
        this.receita = r;
        this.valor = valor;
    }

    @PrePersist
    void prePersist() {
        var now = LocalDateTime.now();
        this.criadoEm = now;
        this.atualizadoEm = now;
    }

    @PreUpdate
    void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // getters/setters
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Receita getReceita() { return receita; }
    public int getValor() { return valor; }
    public void setValor(int valor) { this.valor = valor; }
}
