package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.application.client.ViaCepRequestDto;
import br.com.matteusmoreno.application.common.RequestParam;
import br.com.matteusmoreno.domain.controller.AddressController;
import br.com.matteusmoreno.domain.model.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/addresses")
public class AddressResource {

    private final AddressController addressController;

    public AddressResource(AddressController addressController) {
        this.addressController = addressController;
    }

    @GET
    public Response getAddress(
            @QueryParam(RequestParam.ZIP_CODE) @NotBlank(message = "zipCode is required") @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP must follow the format 12345-678 or 12345678") String zipCode,
            @QueryParam(RequestParam.NUMBER) String number,
            @QueryParam(RequestParam.COMPLEMENT) String complement) {

        ViaCepRequestDto request = new ViaCepRequestDto(zipCode, number, complement);
        Address address = this.addressController.getAddressByZipCode(request);

        return Response.status(Response.Status.OK).entity(address).build();
    }
}
