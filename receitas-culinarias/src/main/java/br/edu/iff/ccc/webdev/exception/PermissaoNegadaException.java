package br.edu.iff.ccc.webdev.exception;

public class PermissaoNegadaException extends RuntimeException {
    public PermissaoNegadaException(String acao) {
        super("Sem permissão para " + acao + ".");
    }
}
