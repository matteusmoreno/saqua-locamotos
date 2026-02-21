package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.application.common.RequestParam;
import br.com.matteusmoreno.domain.controller.UserController;
import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdateUserRequestDto;
import br.com.matteusmoreno.domain.dto.response.UserResponseDto;
import br.com.matteusmoreno.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Path("/users")
public class UserResource {

    private final UserController userController;

    public UserResource(UserController userController) {
        this.userController = userController;
    }

    @POST
    @Path("/customer/create")
    public Response create(@Valid CreateUserRequestDto request) {
        User user = this.userController.createUser(request);

        return Response.status(Response.Status.CREATED).entity(new UserResponseDto(user)).build();
    }

    @GET
    @Path("/{userId}")
    public Response findById(@PathParam(RequestParam.USER_ID) String userId) {
        User user = this.userController.findUserById(userId);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @GET
    @Path("/customers/all")
    public Response findAllCustomers() {
        List<User> customers = this.userController.findAllUsers();

        return Response.status(Response.Status.OK).entity(customers.stream().map(UserResponseDto::new)).build();
    }

    @PATCH
    @Path("/{userId}/add-motorcycle/{motorcycleId}")
    public Response addMotorcycle(@PathParam(RequestParam.USER_ID) String userId, @PathParam(RequestParam.MOTORCYCLE_ID) String motorcycleId) {
        User user = this.userController.addMotorcycle(userId, motorcycleId);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @PATCH
    @Path("/{userId}/remove-motorcycle/{motorcycleId}")
    public Response removeMotorcycle(@PathParam(RequestParam.USER_ID) String userId, @PathParam(RequestParam.MOTORCYCLE_ID) String motorcycleId) {
        User user = this.userController.removeMotorcycle(userId, motorcycleId);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @PUT
    @Path("/update")
    public Response update(@Valid UpdateUserRequestDto request) {
        User user = this.userController.updateUser(request);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @POST
    @Path("/{userId}/upload-picture")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPicture(@PathParam(RequestParam.USER_ID) String userId, @RestForm("file") FileUpload file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.uploadedFile());
        User user = this.userController.uploadPicture(userId, fileBytes);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }
}
