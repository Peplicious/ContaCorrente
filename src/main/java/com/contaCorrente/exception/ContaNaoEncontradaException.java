package com.contaCorrente.exception;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(Long numero) {
        super("Conta não encontrada: " + numero);
    }
}