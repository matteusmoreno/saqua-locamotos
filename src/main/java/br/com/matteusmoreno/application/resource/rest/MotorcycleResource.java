package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.domain.dto.MotorcycleResponseDto;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.controller.MotorcycleController;
import br.com.matteusmoreno.domain.dto.CreateMotorcycleRequestDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/motorcycles")
public class MotorcycleResource {

    private final MotorcycleController motorcycleController;

    public MotorcycleResource(MotorcycleController motorcycleController) {
        this.motorcycleController = motorcycleController;
    }

    @POST
    @Path("/create")
    public Response create(@Valid CreateMotorcycleRequestDto request) {
        Motorcycle motorcycle = this.motorcycleController.createMotorcycle(request);

        return  Response.status(Response.Status.CREATED).entity(new MotorcycleResponseDto(motorcycle)).build();
    }
}
