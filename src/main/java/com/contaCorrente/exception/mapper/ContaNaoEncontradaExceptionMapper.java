package com.contaCorrente.exception.mapper;


import com.contaCorrente.dto.ErroResponse;
import com.contaCorrente.exception.ContaNaoEncontradaException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ContaNaoEncontradaExceptionMapper implements ExceptionMapper<ContaNaoEncontradaException> {
    @Override
    public Response toResponse(ContaNaoEncontradaException e) {
        ErroResponse erro = new ErroResponse(e.getMessage(), 404);
        return Response.status(404).entity(erro).build();
    }
}