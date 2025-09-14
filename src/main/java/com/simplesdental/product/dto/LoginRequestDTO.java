package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para login do usuário")
public class LoginRequestDTO {

    @NotBlank
    @Email
    @Schema(description = "Email do usuário", example = "contato@simplesdental.com")
    private String email;

    @NotBlank
    @Schema(description = "Senha do usuário", example = "123@Pol")
    private String password;

    public LoginRequestDTO() {}

    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}