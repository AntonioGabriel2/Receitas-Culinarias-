package br.edu.iff.ccc.webdev.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=2000)
    private String texto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receita_id", nullable = false)
    private Receita receita;

    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    protected Comentario() {} // JPA

    public Comentario(String texto, Usuario autor, Receita receita) {
        this.texto = texto;
        this.autor = autor;
        this.receita = receita;
    }

    // getters/setters
    public Long getId() { return id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public Receita getReceita() { return receita; }
    public void setReceita(Receita receita) { this.receita = receita; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
