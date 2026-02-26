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
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @RolesAllowed("ADMIN")
    public Response create(@Valid CreateUserRequestDto request) {
        User user = this.userController.createUser(request);

        return Response.status(Response.Status.CREATED).entity(new UserResponseDto(user)).build();
    }

    @GET
    @Path("/{userId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public Response findById(@PathParam(RequestParam.USER_ID) String userId) {
        User user = this.userController.findUserById(userId);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @GET
    @Path("/{userId}/contracts")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public Response findContractsByUserId(@PathParam(RequestParam.USER_ID) String userId) {
        List<Contract> contracts = this.contractController.findContractsByUserId(userId);

        return Response.status(Response.Status.OK).entity(contracts.stream().map(ContractResponseDto::new).toList()).build();
    }

    @GET
    @Path("/customers/all")
    @RolesAllowed("ADMIN")
    public Response findAllCustomers() {
        List<User> customers = this.userController.findAllUsers();

        return Response.status(Response.Status.OK).entity(customers.stream().map(UserResponseDto::new)).build();
    }

    @PUT
    @Path("/update")
    @RolesAllowed("ADMIN")
    public Response update(@Valid UpdateUserRequestDto request) {
        User user = this.userController.updateUser(request);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @GET
    @Path("/verify-email")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public Response verifyEmail(@RestQuery("token") String token) {
        if (token == null || token.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Token is required").build();
        }

        this.userController.verifyEmail(token);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/{userId}/send-verification-email")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public Response sendVerificationEmail(@PathParam(RequestParam.USER_ID) String userId) {
        this.userController.sendVerificationEmail(userId);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/{userId}/upload-picture")
    @RolesAllowed("ADMIN")
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
    @RolesAllowed("ADMIN")
    public Response deletePicture(@PathParam(RequestParam.USER_ID) String userId) {
        User user = this.userController.deletePicture(userId);

        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @POST
    @Path("/{userId}/upload-documents")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadDocuments(
            @PathParam(RequestParam.USER_ID) String userId,
            @RestForm("cnh") FileUpload cnh,
            @RestForm("cpf") FileUpload cpf,
            @RestForm("rg") FileUpload rg,
            @RestForm("proof_of_residence") FileUpload proofOfResidence,
            @RestForm("criminal_record") FileUpload criminalRecord,
            @RestForm("passport") FileUpload passport) throws IOException {

        Map<String, byte[]> documents = new HashMap<>();
        addDocument(documents, "cnh", cnh);
        addDocument(documents, "cpf", cpf);
        addDocument(documents, "rg", rg);
        addDocument(documents, "proof_of_residence", proofOfResidence);
        addDocument(documents, "criminal_record", criminalRecord);
        addDocument(documents, "passport", passport);

        if (documents.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("At least one document file is required").build();
        }

        User user = this.userController.uploadDocuments(userId, documents);
        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    @DELETE
    @Path("/{userId}/delete-documents")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public Response deleteDocuments(
            @PathParam(RequestParam.USER_ID) String userId,
            @RestQuery("types") List<String> documentTypes) {

        if (documentTypes == null || documentTypes.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("At least one document type is required").build();
        }

        User user = this.userController.deleteDocuments(userId, documentTypes);
        return Response.status(Response.Status.OK).entity(new UserResponseDto(user)).build();
    }

    protected void addDocument(Map<String, byte[]> map, String key, FileUpload file) throws IOException {
        if (file != null && file.uploadedFile() != null && file.size() > 0) {
            map.put(key, Files.readAllBytes(file.uploadedFile()));
        }
    }
}
