package br.unitins.tp2.fincontrol.dto;

import org.hibernate.validator.constraints.Length;

import br.unitins.tp2.fincontrol.model.AtividadeMei;
import br.unitins.tp2.fincontrol.model.PerfilFiscal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(

    @NotBlank(message = "O campo email deve ser informado.")
    @Email(message = "Informe um endereço de e-mail válido.")
    @Length(max = 254, message = "O e-mail deve conter no máximo 254 caracteres.")
    String email,

    @NotBlank(message = "O campo nome deve ser informado.")
    @Length(max = 100, message = "O nome deve conter no máximo 100 caracteres.")
    String nome,

    @NotBlank(message = "O campo password deve ser informado.")
    @Length(min = 6, max = 100,
            message = "A senha deve conter entre 6 e 100 caracteres.")
    String password,

    @NotNull(message = "O perfil fiscal deve ser informado.")
    PerfilFiscal perfilFiscal,

    AtividadeMei atividadeMei

) {
}