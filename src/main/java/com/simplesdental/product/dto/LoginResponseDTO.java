package com.simplesdental.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response do login com token JWT")
public class LoginResponseDTO {

    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo do token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Tempo de expiração do token em milissegundos", example = "86400000")
    private Long expiresIn;

    @Schema(description = "Usuário autenticado")
    private UserContextDTO user;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, Long expiresIn, UserContextDTO user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserContextDTO getUser() {
        return user;
    }

    public void setUser(UserContextDTO user) {
        this.user = user;
    }
}