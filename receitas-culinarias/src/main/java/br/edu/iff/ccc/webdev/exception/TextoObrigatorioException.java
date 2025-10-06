package br.edu.iff.ccc.webdev.exception;

public class TextoObrigatorioException extends RuntimeException {
    public TextoObrigatorioException() {
        super("Texto obrigatório.");
    }
}
