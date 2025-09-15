package com.simplesdental.product.service;

import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeAdminPassword() {
        try {
            String adminEmail = "contato@simplesdental.com";
            String adminPassword = "KMbT%5wT*R!46i@@YHqx";

            Optional<User> adminUser = findByEmail(adminEmail);
            if (adminUser.isPresent()) {
                User user = adminUser.get();
                String newEncodedPassword = encodePassword(adminPassword);
                user.setPassword(newEncodedPassword);
                save(user);
                logger.info("Admin password updated successfully for user: {}", adminEmail);
            } else {
                logger.warn("Admin user not found with email: {}", adminEmail);
            }
        } catch (Exception e) {
            logger.error("Error updating admin password during startup", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User createUser(String name, String email, String password, String role) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Email já existe: " + email);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setRole(role);

        return save(user);
    }

    @Override
    public void updatePassword(Long userId, String newPassword) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        user.setPassword(encodePassword(newPassword));
        save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}