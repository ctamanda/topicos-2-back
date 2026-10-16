package br.unitins.tp2.fincontrol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ParametroFiscalRequestDTO(

        @NotBlank(message = "A chave do parâmetro deve ser informada.")
        @Length(max = 50, message = "A chave deve conter no máximo 50 caracteres.")
        String chave,

        @NotNull(message = "O valor do parâmetro deve ser informado.")
        @Positive(message = "O valor deve ser maior que zero.")
        @Digits(
                integer = 13,
                fraction = 2,
                message = "O valor deve ter no máximo 13 dígitos inteiros e 2 casas decimais."
        )
        BigDecimal valor,

        @NotNull(message = "A data inicial da vigência deve ser informada.")
        LocalDate vigenciaInicio,

        @NotNull(message = "A data final da vigência deve ser informada.")
        LocalDate vigenciaFim

) {

    @AssertTrue(message = "A data final da vigência deve ser igual ou posterior à data inicial.")
    public boolean isVigenciaValida() {
        if (vigenciaInicio == null || vigenciaFim == null) {
            return true;
        }

        return !vigenciaFim.isBefore(vigenciaInicio);
    }
}