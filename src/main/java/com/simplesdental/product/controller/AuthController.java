package com.simplesdental.product.controller;

import com.simplesdental.product.dto.*;
import com.simplesdental.product.model.User;
import com.simplesdental.product.service.UserContextCacheService;
import com.simplesdental.product.service.UserService;
import com.simplesdental.product.util.JwtUtil;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API para autenticação e gerenciamento de sessão")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserContextCacheService userContextCacheService;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, UserContextCacheService userContextCacheService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userContextCacheService = userContextCacheService;
    }

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Long) authentication.getDetails();
    }

    @PostMapping("/login")
    @Operation(summary = "Realiza login do usuário",
            description = "Autentica o usuário com email e senha, retornando um token JWT válido por 24 horas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401",
                    description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Dados de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LoginResponseDTO> login(
            @Parameter(description = "Credenciais de login", required = true)
            @Valid @RequestBody LoginRequestDTO loginRequest) {

        logger.info("Attempting login for email: {}", loginRequest.getEmail());

        try {
            Optional<User> userOptional = userService.findByEmail(loginRequest.getEmail());

            if (userOptional.isEmpty()) {
                logger.warn("Login failed - user not found: {}", loginRequest.getEmail());
                throw new IllegalArgumentException("Credenciais inválidas");
            }

            User user = userOptional.get();

            if (!userService.isPasswordValid(loginRequest.getPassword(), user.getPassword())) {
                logger.warn("Login failed - invalid password for user: {}", loginRequest.getEmail());
                throw new IllegalArgumentException("Credenciais inválidas");
            }

            String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
            UserContextDTO userContext = new UserContextDTO(user.getId(), user.getEmail(), user.getRole());

            LoginResponseDTO response = new LoginResponseDTO(token, jwtExpiration, userContext);

            logger.info("Login successful for user: {} (ID: {})", user.getEmail(), user.getId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Login failed for email: {} - {}", loginRequest.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login for email: {}", loginRequest.getEmail(), e);
            throw new RuntimeException("Erro interno do servidor");
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registra novo usuário",
            description = "Cria um novo usuário no sistema. Apenas administradores podem criar usuários.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserContextDTO.class))),
            @ApiResponse(responseCode = "400",
                    description = "Dados inválidos ou email já existe",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado - apenas administradores",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Token inválido ou expirado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserContextDTO> register(
            @Parameter(description = "Dados do novo usuário", required = true)
            @Valid @RequestBody RegisterRequestDTO registerRequest) {

        logger.info("Attempting to register new user with email: {}", registerRequest.getEmail());

        try {
            User user = userService.createUser(
                    registerRequest.getName(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getRole()
            );

            UserContextDTO response = new UserContextDTO(user.getId(), user.getEmail(), user.getRole());

            logger.info("User registered successfully: {} (ID: {})", user.getEmail(), user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            logger.error("Registration failed for email: {} - {}", registerRequest.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during registration for email: {}", registerRequest.getEmail(), e);
            throw new RuntimeException("Erro interno do servidor");
        }
    }

    @GetMapping("/context")
    @Operation(summary = "Obtém contexto do usuário autenticado",
            description = "Retorna os dados do usuário atualmente autenticado (ID, email e role)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Dados do usuário retornados com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserContextDTO.class))),
            @ApiResponse(responseCode = "401",
                    description = "Token inválido ou expirado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserContextDTO> getContext() {
        try {
            Long userId = getUserId();
            logger.info("AUTH/CONTEXT CALLED - Using UserContextCacheService");
            UserContextDTO response = userContextCacheService.getCachedUserContext(userId);
            logger.info("AUTH/CONTEXT FINISHED");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting user context", e);
            throw new RuntimeException("Erro ao obter contexto do usuário");
        }
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os usuários",
            description = "Retorna uma lista de todos os usuários do sistema. Apenas administradores podem acessar.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Lista de usuários retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserContextDTO.class))),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado - apenas administradores"),
            @ApiResponse(responseCode = "401",
                    description = "Token inválido ou expirado")
    })
    public ResponseEntity<List<UserContextDTO>> getAllUsers() {
        try {
            logger.info("Getting all users for admin");

            List<User> users = userService.findAll();
            List<UserContextDTO> response = users.stream()
                    .map(user -> new UserContextDTO(user.getId(), user.getEmail(), user.getRole()))
                    .collect(java.util.stream.Collectors.toList());

            logger.info("Found {} users", users.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting all users", e);
            throw new RuntimeException("Erro ao obter lista de usuários");
        }
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Busca usuário por ID",
            description = "Retorna um usuário específico pelo ID. Apenas administradores podem acessar.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserContextDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado - apenas administradores"),
            @ApiResponse(responseCode = "401",
                    description = "Token inválido ou expirado")
    })
    public ResponseEntity<UserContextDTO> getUserById(
            @Parameter(description = "ID do usuário", required = true, example = "1")
            @PathVariable Long id) {

        logger.info("Getting user by id: {}", id);

        try {
            Optional<User> userOptional = userService.findById(id);

            if (userOptional.isEmpty()) {
                logger.warn("User not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            UserContextDTO response = new UserContextDTO(user.getId(), user.getEmail(), user.getRole());

            logger.info("User found: {}", user.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting user by id: {}", id, e);
            throw new RuntimeException("Erro ao obter usuário");
        }
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deleta um usuário",
            description = "Remove um usuário do sistema. Apenas administradores podem deletar usuários.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Usuário deletado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponseDTO.class))),
            @ApiResponse(responseCode = "404",
                    description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400",
                    description = "Não é possível deletar o próprio usuário"),
            @ApiResponse(responseCode = "403",
                    description = "Acesso negado - apenas administradores"),
            @ApiResponse(responseCode = "401",
                    description = "Token inválido ou expirado")
    })
    public ResponseEntity<SuccessResponseDTO> deleteUser(
            @Parameter(description = "ID do usuário", required = true, example = "2")
            @PathVariable Long id) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long currentUserId = (Long) authentication.getDetails();

            logger.info("Delete user requested by admin (ID: {}) for user ID: {}", currentUserId, id);

            // Não permitir que o admin delete a si mesmo
            if (currentUserId.equals(id)) {
                logger.warn("Admin tried to delete themselves: ID {}", id);
                throw new IllegalArgumentException("Não é possível deletar o próprio usuário");
            }

            Optional<User> userOptional = userService.findById(id);

            if (userOptional.isEmpty()) {
                logger.warn("User not found for deletion with id: {}", id);
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            userService.deleteById(id);

            logger.info("User deleted successfully: {} (ID: {})", user.getEmail(), id);
            SuccessResponseDTO response = new SuccessResponseDTO("Usuário deletado com sucesso");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Delete user failed - {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting user: {}", id, e);
            throw new RuntimeException("Erro ao deletar usuário");
        }
    }
}