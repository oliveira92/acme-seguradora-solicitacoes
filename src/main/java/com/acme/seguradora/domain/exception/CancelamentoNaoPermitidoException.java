package com.acme.seguradora.domain.exception;

public class CancelamentoNaoPermitidoException extends RuntimeException {
    public CancelamentoNaoPermitidoException(String message) {
        super(message);
    }
}
