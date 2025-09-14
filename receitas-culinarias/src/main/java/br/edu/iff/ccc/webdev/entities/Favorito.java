package br.edu.iff.ccc.webdev.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "favoritos",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_favoritos_usuario_receita",
        columnNames = {"usuario_id", "receita_id"}
    )
)
public class Favorito {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_favorito_usuario"))
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_favorito_receita"))
    private Receita receita;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Favorito() {} // JPA

    public Favorito(Usuario usuario, Receita receita) {
        this.usuario = usuario;
        this.receita = receita;
        this.createdAt = Instant.now();
    }

    // getters
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Receita getReceita() { return receita; }
    public Instant getCreatedAt() { return createdAt; }
}
