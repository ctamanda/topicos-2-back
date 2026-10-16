package br.unitins.tp2.fincontrol.dto;

import org.hibernate.validator.constraints.Length;

import br.unitins.tp2.fincontrol.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
    @NotBlank(message = "O campo username deve ser informado.")
    @Length(min = 3, max = 50, message = "O campo deve conter entre 3 e 50 caracteres.")
    String username,

    @NotBlank(message = "O campo password deve ser informado.")
    @Length(min = 6, max = 100, message = "O campo deve conter entre 6 e 100 caracteres.")
    String password,

    @NotNull(message = "O campo role deve ser informado.")
    Role role
) {
}