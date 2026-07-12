package com.contaCorrente.controller;


import com.contaCorrente.dto.*;
import com.contaCorrente.entity.ContaCorrente;
import com.contaCorrente.entity.Movimentacao;
import com.contaCorrente.service.ContaService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Path("/contas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContaController {

    @Inject
    ContaService contaService;

    @POST
    @Path("/abrir")
    public Response abrirConta(AbrirContaRequest request) {
        ContaCorrente conta = contaService.abrirConta(request);
        return Response.status(Response.Status.CREATED)
                .entity(AbrirContaResponse.from(conta))
                .build();
    }

    @POST
    @Path("/deposito")
    public Response depositar(MovimentacaoRequest request) {
        Movimentacao mov = contaService.depositar(request);
        return Response.ok(MovimentacaoResponse.from(mov)).build();
    }

    @POST
    @Path("/saque")
    public Response sacar(MovimentacaoRequest request) {
        Movimentacao mov = contaService.sacar(request);
        return Response.ok(MovimentacaoResponse.from(mov)).build();
    }

    @POST
    @Path("/transferencia")
    public Response transferir(TransferenciaRequest request) {
        contaService.transferir(request);
        return Response.ok().build();
    }

    @GET
    @Path("/{numero}/extrato")
    public Response extrato(
            @PathParam("numero") Long numero,
            @QueryParam("inicio") String inicio,
            @QueryParam("fim") String fim,
            @QueryParam("pagina") @DefaultValue("0") int pagina,
            @QueryParam("tamanhoPagina") @DefaultValue("20") int tamanhoPagina) {

        Instant dataInicio = inicio != null
                ? LocalDate.parse(inicio).atStartOfDay(ZoneOffset.UTC).toInstant()
                : null;

        Instant dataFim = fim != null
                ? LocalDate.parse(fim).atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant()
                : null;

        ExtratoResponse extrato = contaService.consultarExtrato(numero, dataInicio, dataFim, pagina, tamanhoPagina);
        return Response.ok(extrato).build();
    }
}