package br.edu.iff.ccc.webdev.exception;

public class AvaliacaoInvalidaException extends RuntimeException {
    public AvaliacaoInvalidaException(int valor) {
        super("Valor de avaliação inválido: " + valor + ". Deve estar entre 1 e 5.");
    }
}
