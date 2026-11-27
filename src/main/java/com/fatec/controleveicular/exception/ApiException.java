package com.fatec.controleveicular.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção usada pelos services para sinalizar erros de negócio
 * (404 não encontrado, 400 dado inválido, 409 conflito...).
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
