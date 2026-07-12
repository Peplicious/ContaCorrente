package com.contaCorrente.exception.mapper;

import com.contaCorrente.dto.ErroResponse;
import com.contaCorrente.exception.SaldoInsuficienteException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SaldoInsuficienteExceptionMapper implements ExceptionMapper<SaldoInsuficienteException> {
    @Override
    public Response toResponse(SaldoInsuficienteException e) {
        ErroResponse erro = new ErroResponse(e.getMessage(), 422);
        return Response.status(422).entity(erro).build();
    }
}