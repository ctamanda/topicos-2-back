package br.unitins.tp2.fincontrol.dto;

import org.hibernate.validator.constraints.Length;

import br.unitins.tp2.fincontrol.model.TipoCategoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CategoriaRequestDTO(

    @NotBlank(message = "O campo nome deve ser informado.")
    @Length(min = 2, max = 60,
            message = "O nome deve conter entre 2 e 60 caracteres.")
    String nome,

    @NotNull(message = "O campo tipo deve ser informado.")
    TipoCategoria tipo,

    @Length(max = 20, message = "A cor deve conter no maximo 20 caracteres.")
    @Pattern(regexp = "^$|^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "A cor deve estar no formato hexadecimal (ex: #FFFFFF).")
    String cor

) {
}