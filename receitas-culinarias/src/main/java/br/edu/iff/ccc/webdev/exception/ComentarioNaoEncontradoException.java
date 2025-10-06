package br.edu.iff.ccc.webdev.exception;

public class ComentarioNaoEncontradoException extends RuntimeException {
    public ComentarioNaoEncontradoException(Long id) {
        super("Comentário com id " + id + " não encontrado.");
    }
}
