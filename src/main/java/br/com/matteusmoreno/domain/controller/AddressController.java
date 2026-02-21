package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.application.client.ViaCepRequestDto;
import br.com.matteusmoreno.domain.model.Address;
import br.com.matteusmoreno.domain.service.AddressService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    public Address getAddressByZipCode(String zipCode, String number, String complement) {
        return this.addressService.getAddressFromZipcode(zipCode, number, complement);
    }
}
