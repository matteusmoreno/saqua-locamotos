package br.com.matteusmoreno.application.resource.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class TestResource {

    @GET
    public Response test() {
        return Response.ok("API is working!").build();
    }
}
