package br.edu.iff.ccc.webdev.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class ApiGlobalExceptionHandler {
    
    @ExceptionHandler(ReceitaNaoEncontrada.class)
    public ProblemDetail handleReceitaNaoEncontrada(HttpServletRequest req, ReceitaNaoEncontrada e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Receita Não Encontrada");
        problemDetail.setProperty("timestamp", ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toString());
        problemDetail.setProperty("path", req.getRequestURI());
        problemDetail.setProperty("exception", e.getClass().getName());
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(HttpServletRequest req, IllegalArgumentException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Dados inválidos");
        problemDetail.setProperty("timestamp", ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toString());
        problemDetail.setProperty("path", req.getRequestURI());
        problemDetail.setProperty("exception", e.getClass().getName());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class) // fallback para qualquer erro não tratado
    public ProblemDetail handleGeneralError(HttpServletRequest req, Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setTitle("Erro interno do servidor");
        problemDetail.setProperty("timestamp", ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toString());
        problemDetail.setProperty("path", req.getRequestURI());
        problemDetail.setProperty("exception", e.getClass().getName());
        return problemDetail;
    }

    // ============= Usuário =================
    @ExceptionHandler(UsuarioNaoEncontrado.class)
    public ProblemDetail handleUsuarioNaoEncontrado(HttpServletRequest req, UsuarioNaoEncontrado e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Usuário Não Encontrado");
        addDefaults(problemDetail, req, e);
        return problemDetail;
    }

    @ExceptionHandler(CpfJaCadastradoException.class)
    public ProblemDetail handleCpfJaCadastrado(HttpServletRequest req, CpfJaCadastradoException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("CPF já cadastrado");
        addDefaults(problemDetail, req, e);
        return problemDetail;
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ProblemDetail handleEmailJaCadastrado(HttpServletRequest req, EmailJaCadastradoException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("E-mail já cadastrado");
        addDefaults(problemDetail, req, e);
        return problemDetail;
    }

    @ExceptionHandler(SenhaObrigatoriaException.class)
    public ProblemDetail handleSenhaObrigatoria(HttpServletRequest req, SenhaObrigatoriaException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Senha obrigatória");
        addDefaults(problemDetail, req, e);
        return problemDetail;
    }

    // ============= Helper =================
    private void addDefaults(ProblemDetail pd, HttpServletRequest req, Exception e) {
        pd.setProperty("timestamp", ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toString());
        pd.setProperty("path", req.getRequestURI());
        pd.setProperty("exception", e.getClass().getName());
    }

    @ExceptionHandler(UsuarioNaoAutenticadoException.class)
    public ProblemDetail handleUsuarioNaoAutenticado(HttpServletRequest req, UsuarioNaoAutenticadoException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setTitle("Usuário não autenticado");
        problemDetail.setProperty("timestamp", ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toString());
        problemDetail.setProperty("path", req.getRequestURI());
        problemDetail.setProperty("exception", e.getClass().getName());
        return problemDetail;
    }
    @ExceptionHandler(AvaliacaoInvalidaException.class)
    public ProblemDetail handleAvaliacaoInvalida(HttpServletRequest req, AvaliacaoInvalidaException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Avaliação inválida");
        problemDetail.setProperty("timestamp", ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).toString());
        problemDetail.setProperty("path", req.getRequestURI());
        problemDetail.setProperty("exception", e.getClass().getName());
        return problemDetail;
    }
    @ExceptionHandler(ComentarioNaoEncontradoException.class)
    public ProblemDetail handleComentarioNaoEncontrado(HttpServletRequest req, ComentarioNaoEncontradoException e) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Comentário não encontrado");
        pd.setDetail(e.getMessage());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }
    @ExceptionHandler(NotaInvalidaException.class)
    public ResponseEntity<String> handleNotaInvalida(NotaInvalidaException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(TextoObrigatorioException.class)
    public ResponseEntity<String> handleTextoObrigatorio(TextoObrigatorioException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(PermissaoNegadaException.class)
    public ResponseEntity<String> handlePermissaoNegada(PermissaoNegadaException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }




}
