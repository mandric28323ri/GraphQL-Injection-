package com.security.graphqlinjection.service;

import com.security.graphqlinjection.model.User;
import com.security.graphqlinjection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    @SuppressWarnings("unchecked")
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByUsernameContainingIgnoreCase(searchTerm);
    }


    public User createUser(String username, String email, String password, String role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password)) // BCrypt
                .role(role != null ? role : "USER")
                .build();
        return userRepository.save(user);
    }


    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }

    public Optional<User> updateUser(Long id, String username, String email, String password, String role) {
        return userRepository.findById(id).map(user -> {
            if (username != null) user.setUsername(username);
            if (email != null) user.setEmail(email);
            if (password != null) user.setPassword(password);
            if (role != null) user.setRole(role);
            return userRepository.save(user);
        });
    }
}