package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Registrar o novo usuário")
public class RegisterRequestDTO {

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;

    @NotBlank
    @Email
    @Size(max = 150)
    @Schema(description = "Email do usuário", example = "joao@exemplo.com")
    private String email;

    @NotBlank
    @Size(min = 8)
    @Schema(description = "Senha do usuário", example = "minhasenha123")
    private String password;

    @NotBlank
    @Pattern(regexp = "^(admin|user)$", message = "Role deve ser 'admin' ou 'user'")
    @Schema(description = "Papel do usuário no sistema", example = "user", allowableValues = {"admin", "user"})
    private String role;

    public RegisterRequestDTO() {}

    public RegisterRequestDTO(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}