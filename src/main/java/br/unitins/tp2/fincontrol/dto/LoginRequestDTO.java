package br.unitins.tp2.fincontrol.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "O campo username deve ser informado.")
    @Length(min = 3, max = 50, message = "O campo deve conter entre 3 e 50 caracteres.")
    String username,

    @NotBlank(message = "O campo password deve ser informado.")
    @Length(min = 6, max = 100, message = "O campo deve conter entre 6 e 100 caracteres.")
    String password
) {
}