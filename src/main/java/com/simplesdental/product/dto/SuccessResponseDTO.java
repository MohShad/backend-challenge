package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resposta padrão para operações realizadas com sucesso")
public class SuccessResponseDTO {

    @Schema(description = "Mensagem de sucesso", example = "Operação realizada com sucesso")
    private String message;

    @Schema(description = "Data e hora da operação", example = "2025-09-15T13:30:00")
    private LocalDateTime timestamp;

    public SuccessResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponseDTO(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}