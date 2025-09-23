package br.edu.iff.ccc.webdev.exception;

public class ReceitaNaoEncontrada extends RuntimeException{
    
    public ReceitaNaoEncontrada(String mensagem) {
        super(mensagem);
    }

    public ReceitaNaoEncontrada(Long id) {
        super("Receita com id = " + id + " não encontrada.");
    }
}
