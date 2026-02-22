package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.application.common.RequestParam;
import br.com.matteusmoreno.domain.controller.PaymentController;
import br.com.matteusmoreno.domain.dto.request.CreatePaymentRequestDto;
import br.com.matteusmoreno.domain.dto.request.RegisterPaymentRequestDto;
import br.com.matteusmoreno.domain.dto.response.PaymentResponseDto;
import br.com.matteusmoreno.domain.entity.Payment;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private final PaymentController paymentController;

    public PaymentResource(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    @POST
    @Path("/create")
    @RolesAllowed("ADMIN")
    public Response create(@Valid CreatePaymentRequestDto request) {
        Payment payment = paymentController.createPayment(request);
        return Response.status(Response.Status.CREATED).entity(new PaymentResponseDto(payment)).build();
    }

    @GET
    @Path("/{paymentId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public Response findById(@PathParam(RequestParam.PAYMENT_ID) String paymentId) {
        Payment payment = paymentController.findPaymentById(paymentId);
        return Response.status(Response.Status.OK).entity(new PaymentResponseDto(payment)).build();
    }

    @GET
    @Path("/contract/{contractId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public Response findByContractId(@PathParam(RequestParam.CONTRACT_ID) String contractId) {
        List<Payment> payments = paymentController.findPaymentsByContractId(contractId);
        return Response.status(Response.Status.OK)
                .entity(payments.stream().map(PaymentResponseDto::new).toList())
                .build();
    }

    @PATCH
    @Path("/register")
    @RolesAllowed("ADMIN")
    public Response registerPayment(@Valid RegisterPaymentRequestDto request) {
        Payment payment = paymentController.registerPayment(request);
        return Response.status(Response.Status.OK).entity(new PaymentResponseDto(payment)).build();
    }

    @DELETE
    @Path("/{paymentId}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam(RequestParam.PAYMENT_ID) String paymentId) {
        paymentController.deletePayment(paymentId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}

