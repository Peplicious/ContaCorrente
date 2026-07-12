package com.contaCorrente.exception.mapper;

import com.contaCorrente.dto.ErroResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        e.printStackTrace();
        ErroResponse erro = new ErroResponse("Erro interno no servidor", 500);
        return Response.status(500).entity(erro).build();
    }
}