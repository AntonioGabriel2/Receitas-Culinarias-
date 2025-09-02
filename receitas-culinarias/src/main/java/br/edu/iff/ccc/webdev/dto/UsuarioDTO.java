package br.edu.iff.ccc.webdev.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public class UsuarioDTO {

    private Long id; // null no cadastro; preencha na edição

    @NotBlank
    @Size(min = 2, max = 100)
    private String nome;

    @NotBlank
    @CPF
    private String cpf; // pode vir formatado; normalize no service/entity

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;

    // Senha: obrigatória no CADASTRO; opcional na EDIÇÃO (deixe em branco p/ não mudar)
    @Size(min = 6, max = 50, message = "A senha deve ter entre 6 e 50 caracteres")
    private String senha;

    public UsuarioDTO() {}

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
