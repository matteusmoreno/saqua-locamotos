package br.com.matteusmoreno.domain.dto.response;

import br.com.matteusmoreno.domain.constant.MaritalStatus;
import br.com.matteusmoreno.domain.constant.UserRole;
import br.com.matteusmoreno.domain.entity.User;
import br.com.matteusmoreno.domain.model.Address;

public record UserResponseDto(
        String customerId,
        String name,
        String email,
        Boolean emailVerified,
        String phone,
        String cpf,
        String rg,
        String occupation,
        MaritalStatus maritalStatus,
        Address address,
        String pictureUrl,
        UserRole role,
        String createdAt,
        String updatedAt
) {

    public UserResponseDto(User user) {
        this(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getEmailVerified(),
                user.getPhone(),
                user.getCpf(),
                user.getRg(),
                user.getOccupation(),
                user.getMaritalStatus(),
                user.getAddress(),
                user.getPictureUrl(),
                user.getRole(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
        );
    }
}
