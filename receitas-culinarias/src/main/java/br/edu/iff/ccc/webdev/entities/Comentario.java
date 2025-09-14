package br.edu.iff.ccc.webdev.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id", nullable = false)
    private Receita receita;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(nullable = false, length = 2000)
    private String texto;

    @Column(nullable = false)
    private boolean apagado = false;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apagado_por_id")
    private Usuario apagadoPor;

    @Column(name = "apagado_em")
    private LocalDateTime apagadoEm;

    protected Comentario() {}

    public Comentario(Receita receita, Usuario autor, String texto) {
        this.receita = receita;
        this.autor = autor;
        this.texto = texto;
    }

    @PrePersist
    void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }

    // getters/setters básicos
    public Long getId() { return id; }
    public Receita getReceita() { return receita; }
    public Usuario getAutor() { return autor; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public boolean isApagado() { return apagado; }
    public void setApagado(boolean apagado) { this.apagado = apagado; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public Usuario getApagadoPor() { return apagadoPor; }
    public void setApagadoPor(Usuario apagadoPor) { this.apagadoPor = apagadoPor; }
    public LocalDateTime getApagadoEm() { return apagadoEm; }
    public void setApagadoEm(LocalDateTime apagadoEm) { this.apagadoEm = apagadoEm; }
}
