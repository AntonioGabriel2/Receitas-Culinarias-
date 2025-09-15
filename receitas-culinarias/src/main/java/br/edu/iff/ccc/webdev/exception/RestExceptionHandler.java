package br.edu.iff.ccc.webdev.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ResponseEntity<String> handleCpfJaCadastrado(CpfJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro: CPF já cadastrado (" + ex.getMessage() + ")");
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<String> handleEmailJaCadastrado(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro: E-mail já cadastrado (" + ex.getMessage() + ")");
    }

    @ExceptionHandler(SenhaObrigatoriaException.class)
    public ResponseEntity<String> handleSenhaObrigatoria(SenhaObrigatoriaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Erro: Senha obrigatória.");
    }

    @ExceptionHandler(UsuarioNaoEncontrado.class)
    public ResponseEntity<String> handleUsuarioNaoEncontrado(UsuarioNaoEncontrado ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Erro: Usuário não encontrado (" + ex.getMessage() + ")");
    }

    // fallback para qualquer outra exceção não tratada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro inesperado: " + ex.getMessage());
    }
}
