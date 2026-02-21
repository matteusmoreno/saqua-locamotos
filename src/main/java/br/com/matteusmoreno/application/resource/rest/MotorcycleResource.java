package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.application.common.RequestParam;
import br.com.matteusmoreno.domain.dto.response.MotorcycleResponseDto;
import br.com.matteusmoreno.domain.dto.request.UpdateMotorcycleRequestDto;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.controller.MotorcycleController;
import br.com.matteusmoreno.domain.dto.request.CreateMotorcycleRequestDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

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

    @GET
    @Path("/{motorcycleId}")
    public Response findById(@PathParam(RequestParam.MOTORCYCLE_ID) String motorcycleId) {
        Motorcycle motorcycle = this.motorcycleController.findMotorcycleById(motorcycleId);

        return Response.status(Response.Status.OK).entity(new MotorcycleResponseDto(motorcycle)).build();
    }

    @GET
    @Path("/all")
    public Response findAll() {
        List<Motorcycle> motorcycles = this.motorcycleController.findAllMotorcycles();

        return Response.status(Response.Status.OK).entity(motorcycles.stream().map(MotorcycleResponseDto::new).toList()).build();
    }

    @GET
    @Path("/all/available")
    public Response findAllAvailable() {
        List<Motorcycle> motorcycles = this.motorcycleController.findAllAvailableMotorcycles();

        return Response.status(Response.Status.OK).entity(motorcycles.stream().map(MotorcycleResponseDto::new).toList()).build();
    }

    @PUT
    @Path("/update")
    public Response update(@Valid UpdateMotorcycleRequestDto request) {
        Motorcycle motorcycle = this.motorcycleController.updateMotorcycle(request);

        return Response.status(Response.Status.OK).entity(new MotorcycleResponseDto(motorcycle)).build();
    }

    @DELETE
    @Path("/delete/{motorcycleId}")
    public Response delete(@PathParam(RequestParam.MOTORCYCLE_ID) String motorcycleId) {
        this.motorcycleController.deleteMotorcycle(motorcycleId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
