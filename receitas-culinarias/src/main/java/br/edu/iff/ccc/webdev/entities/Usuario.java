package br.edu.iff.ccc.webdev.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuarios_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_usuarios_cpf",   columnNames = "cpf")
    }
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    // Imutável após criação
    @Column(nullable = false, length = 11, updatable = false)
    private String cpf; // armazenar só dígitos

    @Column(nullable = false, length = 150)
    private String email; // armazenar em minúsculas

    @Column(name = "senha_hash", nullable = false, length = 60)
    private String senhaHash; // BCrypt ~60 chars

    // Construtor exigido pelo JPA
    public Usuario() {}

    // Construtor de conveniência para criação
    public Usuario(String nome, String cpf, String email, String senhaHash) {
        this.nome = nome;
        this.cpf = cpf == null ? null : cpf.replaceAll("\\D", "");      // normaliza CPF
        this.email = email == null ? null : email.toLowerCase();         // normaliza e-mail
        this.senhaHash = senhaHash;
    }

    // Getters (sem setter para CPF!)
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }

    // Setters só para campos mutáveis
    public void setId(Long id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email == null ? null : email.toLowerCase(); }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    // equals/hashCode baseados no CPF (chave natural)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(cpf, usuario.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}
