package br.edu.iff.ccc.webdev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReceitaDTO {

    private Long id; // útil para edição

    @NotBlank(message = "O nome não pode ser vazio ou só espaços")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
    private String nome;

    // Campo de formulário: cada linha = 1 ingrediente
    @Size(max = 5000, message = "Ingredientes muito longos")
    private String ingredientesTexto;

    @Size(max = 10000, message = "Modo de preparo muito longo")
    private String modoPreparo;

    public ReceitaDTO() {}

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getIngredientesTexto() { return ingredientesTexto; }
    public void setIngredientesTexto(String ingredientesTexto) { this.ingredientesTexto = ingredientesTexto; }

    public String getModoPreparo() { return modoPreparo; }
    public void setModoPreparo(String modoPreparo) { this.modoPreparo = modoPreparo; }
}
