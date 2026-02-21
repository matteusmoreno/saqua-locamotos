package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.client.ViaCepClient;
import br.com.matteusmoreno.application.client.ViaCepRequestDto;
import br.com.matteusmoreno.application.client.ViaCepResponseDto;
import br.com.matteusmoreno.domain.dto.request.CreateAddressRequestDto;
import br.com.matteusmoreno.domain.model.Address;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Slf4j
public class AddressService {

    private final ViaCepClient viaCepClient;

    public AddressService(@RestClient ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public Address getAddress(CreateAddressRequestDto request) {
        log.info("Fetching address for CEP: {}", request.cep());

        return Address.builder()
                .zipCode(request.cep())
                .street(request.street())
                .number(request.number())
                .complement(request.complement())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .build();
    }

    public Address getAddressFromZipcode(String zipCode, String number, String complement) {
        log.info("Fetching address from ViaCEP for CEP: {}", zipCode);

        ViaCepResponseDto response = this.viaCepClient.getAddress(zipCode);
        return Address.builder()
                .zipCode(response.getZipCode())
                .street(response.getStreet())
                .neighborhood(response.getNeighborhood())
                .city(response.getCity())
                .state(response.getState())
                .number(number)
                .complement(complement)
                .build();

    }
}
