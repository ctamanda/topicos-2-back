package br.unitins.tp2.fincontrol.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

    @NotBlank(message = "O campo email deve ser informado.")
    @Email(message = "Informe um endereço de e-mail válido.")
    @Length(max = 254, message = "O e-mail deve conter no máximo 254 caracteres.")
    String email,

    @NotBlank(message = "O campo password deve ser informado.")
    @Length(min = 6, max = 100,
            message = "A senha deve conter entre 6 e 100 caracteres.")
    String password

) {
}