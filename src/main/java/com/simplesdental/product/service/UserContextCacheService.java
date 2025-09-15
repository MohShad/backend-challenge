package com.simplesdental.product.service;

import com.simplesdental.product.dto.UserContextDTO;
import com.simplesdental.product.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserContextCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UserContextCacheService.class);

    private final UserService userService;

    @Autowired
    public UserContextCacheService(UserService userService) {
        this.userService = userService;
    }

    @Cacheable(value = "userContext", key = "#userId")
    public UserContextDTO getCachedUserContext(Long userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            logger.info("=== CACHE MISS - EXECUTING SQL for user: {} (ID: {}) ===", email, userId);

            Optional<User> userOptional = userService.findById(userId);

            if (userOptional.isEmpty()) {
                logger.warn("User not found in database: ID {}", userId);
                throw new IllegalArgumentException("Usuário não encontrado");
            }

            User user = userOptional.get();
            UserContextDTO response = new UserContextDTO(user.getId(), user.getEmail(), user.getRole());

            logger.info("Cache populated successfully for user: {} - Response: {}", email, response);
            return response;

        } catch (Exception e) {
            logger.error("Error in cacheable method", e);
            throw e;
        }
    }
}