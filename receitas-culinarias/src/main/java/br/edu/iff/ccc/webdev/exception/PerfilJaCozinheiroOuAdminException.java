// PerfilJaCozinheiroOuAdminException.java
package br.edu.iff.ccc.webdev.exception;

public class PerfilJaCozinheiroOuAdminException extends RuntimeException {
    public PerfilJaCozinheiroOuAdminException(Long usuarioId) {
        super("Usuário com id " + usuarioId + " já é COZINHEIRO ou ADMIN.");
    }
}
