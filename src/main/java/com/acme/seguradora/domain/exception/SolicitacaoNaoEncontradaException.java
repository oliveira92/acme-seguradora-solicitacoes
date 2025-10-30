package com.acme.seguradora.domain.exception;

public class SolicitacaoNaoEncontradaException extends RuntimeException {
    public SolicitacaoNaoEncontradaException(String message) {
        super(message);
    }
}
