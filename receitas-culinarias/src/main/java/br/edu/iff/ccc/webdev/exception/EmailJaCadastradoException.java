package br.edu.iff.ccc.webdev.exception;

public class EmailJaCadastradoException extends RuntimeException{

    public EmailJaCadastradoException(String email) {
        super("O e-mail " + email + " já está cadastrado.");
    }
}
