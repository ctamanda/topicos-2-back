package br.unitins.tp2.fincontrol.exception;

import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

// Traduz erros de leitura do JSON (corpo malformado, enum ou data inválidos)
// para uma lista de erros de campo legível, no mesmo formato da validação.
final class JsonInvalido {

    private JsonInvalido() {
    }

    static List<Problem.FieldError> erros(JsonProcessingException e) {
        if (!(e instanceof MismatchedInputException mie)) {
            return List.of(new Problem.FieldError("corpo", "O corpo da requisição não é um JSON válido."));
        }

        String campo = caminho(mie);
        Class<?> tipo = mie.getTargetType();

        if (mie instanceof InvalidFormatException && tipo != null && tipo.isEnum()) {
            String aceitos = Arrays.stream(tipo.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            return List.of(new Problem.FieldError(campo, "Valor inválido. Use um destes: " + aceitos + "."));
        }

        if (tipo != null && Temporal.class.isAssignableFrom(tipo)) {
            return List.of(new Problem.FieldError(campo, "Data inválida. Use o formato aaaa-mm-dd."));
        }

        return List.of(new Problem.FieldError(campo, "Valor em formato inválido para este campo."));
    }

    private static String caminho(JsonMappingException e) {
        String campo = e.getPath().stream()
                .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                .collect(Collectors.joining("."));
        return campo.isBlank() ? "corpo" : campo;
    }
}
