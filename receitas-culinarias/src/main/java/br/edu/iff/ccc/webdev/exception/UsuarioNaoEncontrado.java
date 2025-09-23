package br.edu.iff.ccc.webdev.exception;

public class UsuarioNaoEncontrado extends RuntimeException{
    public UsuarioNaoEncontrado(String mensagem) {
        super(mensagem);
    }

    public UsuarioNaoEncontrado(Long id) {
        super("Usuário com id = " + id + " não encontrado.");
    }
}
