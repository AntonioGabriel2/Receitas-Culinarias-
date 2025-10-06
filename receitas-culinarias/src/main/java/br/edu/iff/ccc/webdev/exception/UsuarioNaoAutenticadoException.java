package br.edu.iff.ccc.webdev.exception;

public class UsuarioNaoAutenticadoException extends RuntimeException {
    public UsuarioNaoAutenticadoException() {
        super("Precisa estar logado para acessar este recurso.");
    }
}
