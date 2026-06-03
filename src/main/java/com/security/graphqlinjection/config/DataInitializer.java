package com.security.graphqlinjection.config;

import com.security.graphqlinjection.model.Post;
import com.security.graphqlinjection.model.User;
import com.security.graphqlinjection.repository.PostRepository;
import com.security.graphqlinjection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing data...");

        User admin = userRepository.save(User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password("admin123")
                .role("ADMIN")
                .build());

        User marko = userRepository.save(User.builder()
                .username("marko")
                .email("marko@gmail.com")
                .password("marko123")
                .role("USER")
                .build());

        User nikola = userRepository.save(User.builder()
                .username("nikola")
                .email("nikola@gmail.com")
                .password("nikola123")
                .role("USER")
                .build());

        User sminka = userRepository.save(User.builder()
                .username("sminka")
                .email("sminka@gmail.com")
                .password("sminka123")
                .role("USER")
                .build());

        postRepository.save(Post.builder()
                .title("Introduction to GraphQL")
                .content("GraphQL is a query language for APIs...")
                .category("tech")
                .author(marko)
                .build());

        postRepository.save(Post.builder()
                .title("Security Best Practices")
                .content("Always validate user input and use parameterized queries...")
                .category("security")
                .author(sminka)
                .build());

        postRepository.save(Post.builder()
                .title("Spring Boot Tutorial")
                .content("Spring Boot makes it easy to create stand-alone applications...")
                .category("tech")
                .author(nikola)
                .build());

        postRepository.save(Post.builder()
                .title("CONFIDENTIAL: Admin Notes")
                .content("Database credentials: root/toor, API key: sk-secret-key-12345")
                .category("hidden")
                .postPassword("admin123")
                .author(admin)
                .build());


        log.info("Data initialized!");
        log.info("H2 Console: http://localhost:8080/h2-console");
    }
}