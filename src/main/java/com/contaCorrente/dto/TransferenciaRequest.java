package com.contaCorrente.dto;

import java.math.BigDecimal;

public class TransferenciaRequest {
    public Long numeroContaOrigem;
    public Long numeroContaDestino;
    public BigDecimal valor;
}