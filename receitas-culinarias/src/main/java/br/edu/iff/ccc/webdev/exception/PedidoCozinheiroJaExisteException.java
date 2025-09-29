// PedidoCozinheiroJaExisteException.java
package br.edu.iff.ccc.webdev.exception;

public class PedidoCozinheiroJaExisteException extends RuntimeException {
    public PedidoCozinheiroJaExisteException(Long usuarioId) {
        super("Usuário com id " + usuarioId + " já possui um pedido de cozinheiro pendente.");
    }
}
