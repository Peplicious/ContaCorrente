package com.contaCorrente.dto;


import com.contaCorrente.entity.Movimentacao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class MovimentacaoResponse {
    public UUID id;
    public Long numeroConta;
    public String tipo;
    public BigDecimal valor;
    public BigDecimal saldoApos;
    public Instant dataHora;

    public static MovimentacaoResponse from(Movimentacao mov) {
        MovimentacaoResponse r = new MovimentacaoResponse();
        r.id = mov.id;
        r.numeroConta = mov.conta.numero;
        r.tipo = mov.tipo.name();
        r.valor = mov.valor;
        r.saldoApos = mov.saldoApos;
        r.dataHora = mov.dataHora;
        return r;
    }
}