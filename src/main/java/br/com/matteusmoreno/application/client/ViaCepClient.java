package br.com.matteusmoreno.application.client;

import br.com.matteusmoreno.application.common.RequestParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "viacep")
public interface ViaCepClient {


    @GET
    @Path("/ws/{cep}/json/")
    ViaCepResponseDto getAddress(@PathParam(RequestParam.CEP) String cep);
}
