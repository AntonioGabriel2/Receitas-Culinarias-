package br.edu.iff.ccc.webdev.exception;

public class CpfJaCadastradoException extends RuntimeException {
    public CpfJaCadastradoException(String cpf) {
        super("O CPF " + cpf + " já está cadastrado.");
    }
    
}
