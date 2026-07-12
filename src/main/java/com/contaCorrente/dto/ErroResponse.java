package com.contaCorrente.dto;


import java.time.Instant;

public class ErroResponse {
    public String mensagem;
    public int status;
    public Instant timestamp;

    public ErroResponse(String mensagem, int status) {
        this.mensagem = mensagem;
        this.status = status;
        this.timestamp = Instant.now();
    }
}