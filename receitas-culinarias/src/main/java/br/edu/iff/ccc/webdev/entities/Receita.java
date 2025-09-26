package br.edu.iff.ccc.webdev.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receitas")
public class Receita implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    
    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    @Column(length = 10000)
    private String modoPreparo;

    // >>> Dono da receita (muitas receitas para um usuário)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "criado_por_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_receita_criado_por")
    )

    private Usuario criadoPor;

    // src/main/java/br/edu/iff/ccc/webdev/entities/Receita.java
    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    @JsonManagedReference
    private List<Ingrediente> ingredientes = new ArrayList<>();


    // Receita.java

    protected Receita() {} // exigido pelo JPA

    public Receita(String nome, String modoPreparo) {
        this.nome = nome;
        this.modoPreparo = modoPreparo;
    }

    public Receita(String nome, java.util.List<Ingrediente> ingredientes, String modoPreparo) {
        this(nome, modoPreparo);
        setIngredientes(ingredientes); // usa helper pra setar a lista corretamente
    }

    // Se quiser já definir o dono no construtor:
    public Receita(String nome, String modoPreparo, Usuario criadoPor) {
        this(nome, modoPreparo);
        this.criadoPor = criadoPor;
    }

    public Receita(String nome, java.util.List<Ingrediente> ingredientes, String modoPreparo, Usuario criadoPor) {
        this(nome, ingredientes, modoPreparo);
        this.criadoPor = criadoPor;
    }

    // Opcional: conveniência com varargs
    public Receita(String nome, String modoPreparo, Usuario criadoPor, Ingrediente... ingredientes) {
        this(nome, modoPreparo, criadoPor);
        if (ingredientes != null) {
            for (Ingrediente i : ingredientes) addIngrediente(i);
        }
    }

    // getters/setters
    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getModoPreparo() { return modoPreparo; }
    public void setModoPreparo(String modoPreparo) { this.modoPreparo = modoPreparo; }

    public Usuario getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Usuario criadoPor) { this.criadoPor = criadoPor; }

    public List<Comentario> getComentarios() { return comentarios; }

    /** Getter padrão (JPA/Thymeleaf/JSON) */
    public List<Ingrediente> getIngredientes() {
        return java.util.Collections.unmodifiableList(ingredientes);
    }

    /** Opcional: helpers para manter o vínculo dos dois lados */
    public void addIngrediente(Ingrediente ing) {
        if (ing == null) return;
        ing.setReceita(this);
        ingredientes.add(ing);
    }

    public void removeIngrediente(Ingrediente ing) {
        if (ing == null) return;
        ingredientes.remove(ing);
        ing.setReceita(null);
    }

    /** Opcional: substituir conteúdo sem trocar a lista (bom para JPA) */
    public void setIngredientes(List<Ingrediente> novos) {
        ingredientes.clear();
        if (novos != null) {
            for (Ingrediente i : novos) addIngrediente(i);
        }
    }

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
