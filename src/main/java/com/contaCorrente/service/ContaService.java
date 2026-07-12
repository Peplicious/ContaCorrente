package com.contaCorrente.service;

import com.contaCorrente.dto.*;
import com.contaCorrente.entity.ContaCorrente;
import com.contaCorrente.entity.Movimentacao;
import com.contaCorrente.exception.ContaNaoEncontradaException;
import com.contaCorrente.exception.SaldoInsuficienteException;
import com.contaCorrente.exception.TransferenciaInvalidaException;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@ApplicationScoped
public class ContaService {

    @Transactional
    public ContaCorrente abrirConta(AbrirContaRequest request) {
        if (request.documento == null || request.documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento (CPF/CNPJ) é obrigatório");
        }

        Long numero = gerarNumeroConta();

        ContaCorrente conta = new ContaCorrente();
        conta.numero = numero;
        conta.digitoVerificador = ContaCorrente.calcularDigitoVerificador(numero);
        conta.documento = request.documento;

        conta.persist();
        return conta;
    }

    @Transactional
    public Movimentacao depositar(MovimentacaoRequest request) {
        validarValor(request.valor);

        ContaCorrente conta = ContaCorrente.buscarComLockPorNumero(request.numeroConta);
        if (conta == null) {
            throw new ContaNaoEncontradaException(request.numeroConta);
        }

        conta.saldo = conta.saldo.add(request.valor);

        Movimentacao mov = new Movimentacao();
        mov.conta = conta;
        mov.tipo = Movimentacao.TipoMovimentacao.DEPOSITO;
        mov.valor = request.valor;
        mov.saldoApos = conta.saldo;
        mov.persist();

        return mov;
    }

    @Transactional
    public Movimentacao sacar(MovimentacaoRequest request) {
        validarValor(request.valor);

        ContaCorrente conta = ContaCorrente.buscarComLockPorNumero(request.numeroConta);
        if (conta == null) {
            throw new ContaNaoEncontradaException(request.numeroConta);
        }

        if (conta.saldo.compareTo(request.valor) < 0) {
            throw new SaldoInsuficienteException(request.numeroConta);
        }

        conta.saldo = conta.saldo.subtract(request.valor);

        Movimentacao mov = new Movimentacao();
        mov.conta = conta;
        mov.tipo = Movimentacao.TipoMovimentacao.SAQUE;
        mov.valor = request.valor;
        mov.saldoApos = conta.saldo;
        mov.persist();

        return mov;
    }

    @Transactional
    public void transferir(TransferenciaRequest request) {
        validarValor(request.valor);

        if (request.numeroContaOrigem == null || request.numeroContaDestino == null) {
            throw new TransferenciaInvalidaException("Conta de origem e destino são obrigatórias");
        }

        if (request.numeroContaOrigem.equals(request.numeroContaDestino)) {
            throw new TransferenciaInvalidaException("Conta de origem e destino não podem ser iguais");
        }

        Long menorNumero = Math.min(request.numeroContaOrigem, request.numeroContaDestino);
        Long maiorNumero = Math.max(request.numeroContaOrigem, request.numeroContaDestino);

        ContaCorrente primeira = ContaCorrente.buscarComLockPorNumero(menorNumero);
        ContaCorrente segunda = ContaCorrente.buscarComLockPorNumero(maiorNumero);

        if (primeira == null || segunda == null) {
            throw new ContaNaoEncontradaException(primeira == null ? menorNumero : maiorNumero);
        }

        ContaCorrente origem = primeira.numero.equals(request.numeroContaOrigem) ? primeira : segunda;
        ContaCorrente destino = primeira.numero.equals(request.numeroContaOrigem) ? segunda : primeira;

        if (origem.saldo.compareTo(request.valor) < 0) {
            throw new SaldoInsuficienteException(origem.numero);
        }

        UUID transferenciaId = UUID.randomUUID();

        origem.saldo = origem.saldo.subtract(request.valor);
        Movimentacao debito = new Movimentacao();
        debito.conta = origem;
        debito.tipo = Movimentacao.TipoMovimentacao.TRANSFERENCIA_ENVIADA;
        debito.valor = request.valor;
        debito.saldoApos = origem.saldo;
        debito.contaRelacionada = destino;
        debito.transferenciaId = transferenciaId;
        debito.persist();

        destino.saldo = destino.saldo.add(request.valor);
        Movimentacao credito = new Movimentacao();
        credito.conta = destino;
        credito.tipo = Movimentacao.TipoMovimentacao.TRANSFERENCIA_RECEBIDA;
        credito.valor = request.valor;
        credito.saldoApos = destino.saldo;
        credito.contaRelacionada = origem;
        credito.transferenciaId = transferenciaId;
        credito.persist();
    }


    public ExtratoResponse consultarExtrato(Long numeroConta, Instant inicio, Instant fim, int pagina, int tamanhoPagina) {
        ContaCorrente conta = ContaCorrente.find("numero", numeroConta).firstResultOptional()
                .map(c -> (ContaCorrente) c)
                .orElseThrow(() -> new ContaNaoEncontradaException(numeroConta));

        Instant dataInicio = inicio != null ? inicio : Instant.EPOCH;
        Instant dataFim = fim != null ? fim : Instant.now();

        PanacheQuery<Movimentacao> query = Movimentacao.find(
                "conta = ?1 and dataHora >= ?2 and dataHora <= ?3",
                Sort.by("dataHora").descending(),
                conta, dataInicio, dataFim
        );

        long total = query.count();

        List<Movimentacao> movimentacoes = query
                .page(Page.of(pagina, tamanhoPagina))
                .list();

        ExtratoResponse response = new ExtratoResponse();
        response.numeroConta = conta.numero;
        response.saldoAtual = conta.saldo;
        response.movimentacoes = movimentacoes.stream()
                .map(MovimentacaoResponse::from)
                .collect(Collectors.toList());
        response.pagina = pagina;
        response.tamanhoPagina = tamanhoPagina;
        response.totalMovimentacoes = total;

        return response;
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
    }

    private Long gerarNumeroConta() {
        long numero;
        do {
            numero = ThreadLocalRandom.current().nextLong(100000, 999999);
        } while (ContaCorrente.count("numero", numero) > 0);
        return numero;
    }
}