package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do contexto do usuário autenticado")
public class UserContextDTO {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Email do usuário", example = "contato@simplesdental.com")
    private String email;

    @Schema(description = "Papel do usuário no sistema", example = "admin", allowableValues = {"admin", "user"})
    private String role;

    public UserContextDTO() {}

    public UserContextDTO(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}