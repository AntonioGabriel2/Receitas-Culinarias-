package br.edu.iff.ccc.webdev.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComentarioDTO {

    @NotBlank(message = "Comentário não pode ser vazio.")
    @Size(max = 2000, message = "Comentário pode ter no máximo 2000 caracteres.")
    private String texto;

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}
