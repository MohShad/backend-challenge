package com.simplesdental.product.controller;

import com.simplesdental.product.dto.ErrorResponse;
import com.simplesdental.product.dto.UpdatePasswordRequestDTO;
import com.simplesdental.product.model.User;
import com.simplesdental.product.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "API para gerenciamento de usuários")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/password")
    @Operation(summary = "Atualiza senha do usuário autenticado",
               description = "Permite ao usuário autenticado alterar sua senha atual fornecendo a senha atual e a nova senha",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Senha atualizada com sucesso"),
        @ApiResponse(responseCode = "400",
                    description = "Dados inválidos ou senha atual incorreta",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401",
                    description = "Token inválido ou expirado",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "Dados para atualização de senha", required = true)
            @Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequest) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Long userId = (Long) authentication.getDetails();

            logger.info("Password update requested for user: {} (ID: {})", email, userId);

            Optional<User> userOptional = userService.findById(userId);

            if (userOptional.isEmpty()) {
                logger.warn("User not found for password update: ID {}", userId);
                throw new IllegalArgumentException("Usuário não encontrado");
            }

            User user = userOptional.get();

            if (!userService.isPasswordValid(updatePasswordRequest.getCurrentPassword(), user.getPassword())) {
                logger.warn("Invalid current password for user: {} (ID: {})", email, userId);
                throw new IllegalArgumentException("Senha atual incorreta");
            }

            if (userService.isPasswordValid(updatePasswordRequest.getNewPassword(), user.getPassword())) {
                logger.warn("New password is same as current password for user: {} (ID: {})", email, userId);
                throw new IllegalArgumentException("A nova senha deve ser diferente da senha atual");
            }

            userService.updatePassword(userId, updatePasswordRequest.getNewPassword());

            logger.info("Password updated successfully for user: {} (ID: {})", email, userId);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            logger.error("Password update failed - {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during password update", e);
            throw new RuntimeException("Erro interno do servidor");
        }
    }
}