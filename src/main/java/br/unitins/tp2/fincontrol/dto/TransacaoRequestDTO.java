package br.unitins.tp2.fincontrol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import br.unitins.tp2.fincontrol.model.Escopo;
import br.unitins.tp2.fincontrol.model.TipoTransacao;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransacaoRequestDTO(
    @NotBlank(message = "O campo descricao deve ser informado.")
    @Length(min = 2, max = 150, message = "O campo deve conter entre 2 e 150 caracteres.")
    String descricao,

    @NotNull(message = "O campo valor deve ser informado.")
    @Positive(message = "O valor deve ser maior que zero.")
    @Digits(integer = 13, fraction = 2, message = "O valor deve ter no máximo 13 dígitos inteiros e 2 casas decimais.")
    BigDecimal valor,

    @NotNull(message = "O campo data deve ser informado.")
    LocalDate data,

    @NotNull(message = "O campo tipo deve ser informado.")
    TipoTransacao tipo,

    @NotNull(message = "O campo escopo deve ser informado.")
    Escopo escopo,

    @NotNull(message = "A categoria deve ser informada.")
    Long idCategoria
) {
}
