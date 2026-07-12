package com.contaCorrente.exception;

public class TransferenciaInvalidaException extends RuntimeException {
    public TransferenciaInvalidaException(String mensagem) {
        super(mensagem);
    }
}