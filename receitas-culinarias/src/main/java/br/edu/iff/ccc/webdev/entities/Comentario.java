package br.edu.iff.ccc.webdev.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receita_id")
    private Receita receita;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @Column(nullable = false, length = 2000)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    // ------ Soft delete ------
    @Column(nullable = false)
    private boolean apagado = false;

    private LocalDateTime apagadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apagado_por_id")
    private Usuario apagadoPor;

    @Column(length = 255)
    private String motivoExclusao;
    // --------------------------

    protected Comentario() {}

    public Comentario(Receita receita, Usuario autor, String texto) {
        this.receita = receita;
        this.autor = autor;
        this.texto = texto;
    }

    // getters/setters
    public Long getId() { return id; }
    public Receita getReceita() { return receita; }
    public void setReceita(Receita receita) { this.receita = receita; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public boolean isApagado() { return apagado; }
    public void setApagado(boolean apagado) { this.apagado = apagado; }
    public LocalDateTime getApagadoEm() { return apagadoEm; }
    public void setApagadoEm(LocalDateTime apagadoEm) { this.apagadoEm = apagadoEm; }
    public Usuario getApagadoPor() { return apagadoPor; }
    public void setApagadoPor(Usuario apagadoPor) { this.apagadoPor = apagadoPor; }
    public String getMotivoExclusao() { return motivoExclusao; }
    public void setMotivoExclusao(String motivoExclusao) { this.motivoExclusao = motivoExclusao; }
}
