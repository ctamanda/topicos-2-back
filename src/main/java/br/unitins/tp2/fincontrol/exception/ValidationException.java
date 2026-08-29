package br.unitins.tp2.fincontrol.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

    private Map<String, String> erros = new HashMap<>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String campo, String mensagem) {
        super(mensagem);
        adicionarErro(campo, mensagem);
    }

    public void adicionarErro(String campo, String mensagem) {
        erros.put(campo, mensagem);
    }

    public Map<String, String> getErros() {
        return erros;
    }

    public static ValidationException of(String campo, String mensagem) {
        return new ValidationException(campo, mensagem);
    }
}