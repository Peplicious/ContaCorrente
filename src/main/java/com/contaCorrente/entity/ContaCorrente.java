package com.contaCorrente.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conta_corrente")
public class ContaCorrente extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false, unique = true)
    public Long numero;

    @Column(name = "digito_verificador", nullable = false)
    public String digitoVerificador;

    @Column(nullable = false)
    public String documento;

    @Column(nullable = false)
    public BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = "data_abertura", nullable = false)
    public Instant dataAbertura = Instant.now();


    public static String calcularDigitoVerificador(Long numero) {
        String num = String.valueOf(numero);
        int soma = 0;
        int peso = 2;
        for (int i = num.length() - 1; i >= 0; i--) {
            soma += Character.getNumericValue(num.charAt(i)) * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }
        int resto = soma % 11;
        int digito = 11 - resto;
        return (digito >= 10) ? "0" : String.valueOf(digito);
    }

    public static ContaCorrente buscarComLockPorNumero(Long numero) {
        return (ContaCorrente) find("numero", numero)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResultOptional()
                .orElse(null);
    }
}