package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Atualização da senha")
public class UpdatePasswordRequestDTO {

    @NotBlank
    @Size(min = 8)
    @Schema(description = "Senha atual do usuário", example = "senhaatual123")
    private String currentPassword;

    @NotBlank
    @Size(min = 8)
    @Schema(description = "Nova senha do usuário", example = "novasenha456")
    private String newPassword;

    public UpdatePasswordRequestDTO() {}

    public UpdatePasswordRequestDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}