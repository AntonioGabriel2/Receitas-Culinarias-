package br.edu.iff.ccc.webdev.exception;

public class SenhaObrigatoriaException extends RuntimeException {
    public SenhaObrigatoriaException() {
        super("Senha obrigatória no cadastro.");
    }
}
