package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.application.common.RequestParam;
import br.com.matteusmoreno.domain.controller.ContractController;
import br.com.matteusmoreno.domain.dto.request.CreateContractRequestDto;
import br.com.matteusmoreno.domain.dto.response.ContractResponseDto;
import br.com.matteusmoreno.domain.entity.Contract;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;

@Path("/contracts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContractResource {

    private final ContractController contractController;

    public ContractResource(ContractController contractController) {
        this.contractController = contractController;
    }

    @POST
    @Path("/create")
    @RolesAllowed("ADMIN")
    public Response create(@Valid CreateContractRequestDto request) {
        Contract contract = contractController.createContract(request);

        return Response.status(Response.Status.CREATED).entity(new ContractResponseDto(contract)).build();
    }

    @GET
    @Path("/all")
    @RolesAllowed("ADMIN")
    public Response findAll() {
        return Response.ok(contractController.findAllContracts()
                .stream().map(ContractResponseDto::new).toList()).build();
    }

    @GET
    @Path("/{contractId}")
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    public Response findById(@PathParam(RequestParam.CONTRACT_ID) String contractId) {
        Contract contract = contractController.findContractById(contractId);

        return Response.status(Response.Status.OK).entity(new ContractResponseDto(contract)).build();
    }

    @PATCH
    @Path("/{contractId}/finish")
    @RolesAllowed("ADMIN")
    public Response finish(@PathParam(RequestParam.CONTRACT_ID) String contractId, @QueryParam("refundDeposit") @NotNull(message = "refundDeposit is required") Boolean refundDeposit) {
        Contract contract = contractController.finishContract(contractId, refundDeposit);

        return Response.status(Response.Status.OK).entity(new ContractResponseDto(contract)).build();
    }

    @PATCH
    @Path("/{contractId}/cancel")
    @RolesAllowed("ADMIN")
    public Response cancel(@PathParam(RequestParam.CONTRACT_ID) String contractId) {
        Contract contract = contractController.cancelContract(contractId);

        return Response.status(Response.Status.OK).entity(new ContractResponseDto(contract)).build();
    }

    @POST
    @Path("/{contractId}/upload-file")
    @RolesAllowed("ADMIN")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam(RequestParam.CONTRACT_ID) String contractId, @RestForm("file") @NotNull(message = "File is required") FileUpload file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.uploadedFile());
        if (fileBytes.length == 0) return Response.status(Response.Status.BAD_REQUEST).entity("File must not be empty").build();

        Contract contract = contractController.uploadContractFile(contractId, fileBytes);
        return Response.status(Response.Status.OK).entity(new ContractResponseDto(contract)).build();
    }
}
