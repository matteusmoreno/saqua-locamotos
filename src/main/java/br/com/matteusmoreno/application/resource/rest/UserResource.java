package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.application.common.RequestParam;
import br.com.matteusmoreno.domain.controller.UserController;
import br.com.matteusmoreno.domain.dto.request.CreateUserRequestDto;
import br.com.matteusmoreno.domain.dto.response.UserResponseDto;
import br.com.matteusmoreno.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

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

        return Response.ok(new UserResponseDto(user)).build();
    }
}
