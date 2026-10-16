package br.unitins.tp2.fincontrol.dto;

public record LoginResponseDTO(
    String token,
    long expiresIn
) {
}