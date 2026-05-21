package com.security.graphqlinjection.service;

import com.security.graphqlinjection.model.User;
import com.security.graphqlinjection.repository.PostRepository;
import com.security.graphqlinjection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // RANJIVO: direktna string konkatenacija u JPQL upitu
    @SuppressWarnings("unchecked")
    public List<User> searchUsers(String searchTerm) {
        String jpql = "SELECT u FROM User u WHERE u.username LIKE '%" + searchTerm + "%'";
        System.out.println("[VULNERABLE] Executing JPQL: " + jpql);
        return entityManager.createQuery(jpql).getResultList();
    }

    // RANJIVO: lozinka se cuva kao plaintext
    public User createUser(String username, String email, String password, String role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(role != null ? role : "USER")
                .build();
        return userRepository.save(user);
    }

    // RANJIVO: nema autorizacije, bilo ko moze menjati bilo koga
    public Optional<User> updateUser(Long id, String username, String email, String password, String role) {
        return userRepository.findById(id).map(user -> {
            if (username != null) user.setUsername(username);
            if (email != null) user.setEmail(email);
            if (password != null) user.setPassword(password);
            if (role != null) user.setRole(role);
            return userRepository.save(user);
        });
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {

            postRepository.deleteAll(postRepository.findByAuthorId(id));

            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // RANJIVO: plaintext poredjenje lozinki
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }
}