package br.edu.iff.ccc.webdev.exception;

public class NotaInvalidaException extends RuntimeException {
    public NotaInvalidaException(int valor) {
        super("Nota inválida: " + valor + ". Use valores entre 1 e 5.");
    }
}
