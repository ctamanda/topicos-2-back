package br.unitins.tp2.fincontrol.dto;

import org.hibernate.validator.constraints.Length;

import br.unitins.tp2.fincontrol.model.AtividadeMei;
import br.unitins.tp2.fincontrol.model.PerfilFiscal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioUpdateDTO(

        @NotBlank(message = "O nome deve ser informado.")
        @Length(max = 100, message = "O nome deve conter no máximo 100 caracteres.")
        String nome,

        @NotNull(message = "O perfil fiscal deve ser informado.")
        PerfilFiscal perfilFiscal,

        AtividadeMei atividadeMei

) {
}