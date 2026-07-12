package com.contaCorrente.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AbrirContaResponse {
    public UUID id;
    public Long numero;
    public String digitoVerificador;
    public String documento;
    public BigDecimal saldo;
    public Instant dataAbertura;

    public static AbrirContaResponse from(com.contaCorrente.entity.ContaCorrente conta) {
        AbrirContaResponse r = new AbrirContaResponse();
        r.id = conta.id;
        r.numero = conta.numero;
        r.digitoVerificador = conta.digitoVerificador;
        r.documento = conta.documento;
        r.saldo = conta.saldo;
        r.dataAbertura = conta.dataAbertura;
        return r;
    }
}