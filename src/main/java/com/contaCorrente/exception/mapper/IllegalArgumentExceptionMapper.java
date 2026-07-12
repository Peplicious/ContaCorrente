package com.contaCorrente.exception.mapper;

import com.contaCorrente.dto.ErroResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException e) {
        ErroResponse erro = new ErroResponse(e.getMessage(), 400);
        return Response.status(400).entity(erro).build();
    }
}