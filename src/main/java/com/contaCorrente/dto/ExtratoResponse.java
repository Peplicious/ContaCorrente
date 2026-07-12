package com.contaCorrente.dto;


import java.math.BigDecimal;
import java.util.List;

public class ExtratoResponse {
    public Long numeroConta;
    public BigDecimal saldoAtual;
    public List<MovimentacaoResponse> movimentacoes;
    public int pagina;
    public int tamanhoPagina;
    public long totalMovimentacoes;
}