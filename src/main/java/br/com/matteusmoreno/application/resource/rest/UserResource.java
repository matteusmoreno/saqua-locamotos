package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.application.common.RequestParam;
import br.com.matteusmoreno.domain.controller.ContractController;
import br.com.matteusmoreno.domain.controller.UserController;
import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.dto.request.UpdateUserRequestDto;
import br.com.matteusmoreno.domain.dto.response.ContractResponseDto;
import br.com.matteusmoreno.domain.dto.response.UserResponseDto;
import br.com.matteusmoreno.domain.entity.Contract;
import br.com.matteusmoreno.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    private final ContractController contractController;

    public UserResource(UserController userController, ContractController contractController) {
        this.userController = userController;
        this.contractController = contractController;
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
    @Path("/{userId}/contracts")
    public Response findContractsByUserId(@PathParam(RequestParam.USER_ID) String userId) {
        List<Contract> contracts = this.contractController.findContractsByUserId(userId);

        return Response.status(Response.Status.OK).entity(contracts.stream().map(ContractResponseDto::new).toList()).build();
    }

    @GET
    @Path("/customers/all")
    public Response findAllCustomers() {
        List<User> customers = this.userController.findAllUsers();

        return Response.status(Response.Status.OK).entity(customers.stream().map(UserResponseDto::new)).build();
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
    public Response uploadPicture(
            @PathParam(RequestParam.USER_ID) String userId,
            @RestForm(RequestParam.FILE) @NotNull(message = "File is required") FileUpload file) throws IOException {

        byte[] fileBytes = Files.readAllBytes(file.uploadedFile());
        if (fileBytes.length == 0) return Response.status(Response.Status.BAD_REQUEST).entity("File must not be empty").build();

        User user = this.userController.uploadPicture(userId, fileBytes);
        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @DELETE
    @Path("/{userId}/delete-picture")
    public Response deletePicture(@PathParam(RequestParam.USER_ID) String userId) {
        User user = this.userController.deletePicture(userId);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }
}
