package com.contaCorrente.service;

import com.contaCorrente.dto.AbrirContaRequest;
import com.contaCorrente.dto.MovimentacaoRequest;
import com.contaCorrente.dto.TransferenciaRequest;
import com.contaCorrente.entity.ContaCorrente;
import com.contaCorrente.exception.SaldoInsuficienteException;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class ContaServiceTest {

    @Inject
    ContaService contaService;

    @Test
    @TestTransaction
    void deveLancarExcecaoAoSacarComSaldoInsuficiente() {
        AbrirContaRequest abrirRequest = new AbrirContaRequest();
        abrirRequest.documento = "12345678900";
        ContaCorrente conta = contaService.abrirConta(abrirRequest);

        MovimentacaoRequest saqueRequest = new MovimentacaoRequest();
        saqueRequest.numeroConta = conta.numero;
        saqueRequest.valor = new BigDecimal("100.00");
        SaldoInsuficienteException exception = assertThrows(
                SaldoInsuficienteException.class,
                () -> contaService.sacar(saqueRequest)
        );
        assertTrue(exception.getMessage().contains(conta.numero.toString()));


        ContaCorrente contaRecarregada = ContaCorrente.findById(conta.id);
        assertEquals(0, contaRecarregada.saldo.compareTo(BigDecimal.ZERO));
    }

    @Test
    @TestTransaction
    void deveTransferirValorCorretamenteEntreContas() {
        AbrirContaRequest origemRequest = new AbrirContaRequest();
        origemRequest.documento = "11111111111";
        ContaCorrente contaOrigem = contaService.abrirConta(origemRequest);

        MovimentacaoRequest depositoRequest = new MovimentacaoRequest();
        depositoRequest.numeroConta = contaOrigem.numero;
        depositoRequest.valor = new BigDecimal("100.00");
        contaService.depositar(depositoRequest);

        AbrirContaRequest destinoRequest = new AbrirContaRequest();
        destinoRequest.documento = "22222222222";
        ContaCorrente contaDestino = contaService.abrirConta(destinoRequest);

        TransferenciaRequest transferenciaRequest = new TransferenciaRequest();
        transferenciaRequest.numeroContaOrigem = contaOrigem.numero;
        transferenciaRequest.numeroContaDestino = contaDestino.numero;
        transferenciaRequest.valor = new BigDecimal("30.00");

        contaService.transferir(transferenciaRequest);

        ContaCorrente origemRecarregada = ContaCorrente.findById(contaOrigem.id);
        ContaCorrente destinoRecarregada = ContaCorrente.findById(contaDestino.id);

        assertEquals(0, origemRecarregada.saldo.compareTo(new BigDecimal("70.00")));
        assertEquals(0, destinoRecarregada.saldo.compareTo(new BigDecimal("30.00")));
    }

    @Test
    @TestTransaction
    void deveLancarExcecaoAoTransferirValorMaiorQueSaldo() {
        AbrirContaRequest origemRequest = new AbrirContaRequest();
        origemRequest.documento = "44444444444";
        ContaCorrente contaOrigem = contaService.abrirConta(origemRequest);

        MovimentacaoRequest depositoRequest = new MovimentacaoRequest();
        depositoRequest.numeroConta = contaOrigem.numero;
        depositoRequest.valor = new BigDecimal("50.00");
        contaService.depositar(depositoRequest);

        AbrirContaRequest destinoRequest = new AbrirContaRequest();
        destinoRequest.documento = "55555555555";
        ContaCorrente contaDestino = contaService.abrirConta(destinoRequest);

        TransferenciaRequest transferenciaRequest = new TransferenciaRequest();
        transferenciaRequest.numeroContaOrigem = contaOrigem.numero;
        transferenciaRequest.numeroContaDestino = contaDestino.numero;
        transferenciaRequest.valor = new BigDecimal("999.00");

        SaldoInsuficienteException exception = assertThrows(
                SaldoInsuficienteException.class,
                () -> contaService.transferir(transferenciaRequest)
        );

        assertTrue(exception.getMessage().contains(contaOrigem.numero.toString()));

        ContaCorrente origemRecarregada = ContaCorrente.findById(contaOrigem.id);
        ContaCorrente destinoRecarregada = ContaCorrente.findById(contaDestino.id);

        assertEquals(0, origemRecarregada.saldo.compareTo(new BigDecimal("50.00")));
        assertEquals(0, destinoRecarregada.saldo.compareTo(BigDecimal.ZERO));
    }
}