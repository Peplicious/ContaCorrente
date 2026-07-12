package com.contaCorrente.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "movimentacao", indexes = {@Index(name = "idx_movimentacao_conta_data", columnList = "conta_id, data_hora")})
public class Movimentacao extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    public ContaCorrente conta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TipoMovimentacao tipo;

    @Column(nullable = false)
    public BigDecimal valor;

    @Column(name = "saldo_apos", nullable = false)
    public BigDecimal saldoApos;

    @Column(name = "data_hora", nullable = false)
    public Instant dataHora = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_relacionada_id")
    public ContaCorrente contaRelacionada;

    @Column(name = "transferencia_id")
    public UUID transferenciaId;

    public enum TipoMovimentacao {
        DEPOSITO, SAQUE, TRANSFERENCIA_ENVIADA, TRANSFERENCIA_RECEBIDA
    }
}