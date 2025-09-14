package com.simplesdental.product.service;

import com.simplesdental.product.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User save(User user);

    User createUser(String name, String email, String password, String role);

    void updatePassword(Long userId, String newPassword);

    boolean existsByEmail(String email);

    String encodePassword(String rawPassword);

    boolean isPasswordValid(String rawPassword, String encodedPassword);

}