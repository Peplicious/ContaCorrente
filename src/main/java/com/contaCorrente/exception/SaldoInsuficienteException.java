package com.contaCorrente.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(Long numero) {
        super("Saldo insuficiente na conta: " + numero);
    }
}