package com.security.graphqlinjection.config;

import com.security.graphqlinjection.model.Comment;
import com.security.graphqlinjection.model.Post;
import com.security.graphqlinjection.model.User;
import com.security.graphqlinjection.repository.CommentRepository;
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
    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing demo data...");

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

        Post post1 = postRepository.save(Post.builder()
                .title("Introduction to GraphQL")
                .content("GraphQL is a query language for APIs...")
                .category("tech")
                .author(marko)
                .build());

        Post post2 = postRepository.save(Post.builder()
                .title("Security Best Practices")
                .content("Always validate user input and use parameterized queries...")
                .category("security")
                .author(admin)
                .build());

        Post post3 = postRepository.save(Post.builder()
                .title("Spring Boot Tutorial")
                .content("Spring Boot makes it easy to create stand-alone applications...")
                .category("tech")
                .author(nikola)
                .build());

        postRepository.save(Post.builder()
                .title("CONFIDENTIAL: Admin Notes")
                .content("Database credentials: root/toor, API key: sk-secret-key-12345")
                .category("admin")
                .author(admin)
                .build());

        commentRepository.save(Comment.builder()
                .content("Great article, very informative!")
                .author(nikola)
                .post(post1)
                .build());

        commentRepository.save(Comment.builder()
                .content("I learned a lot from this post.")
                .author(sminka)
                .post(post1)
                .build());

        commentRepository.save(Comment.builder()
                .content("Security is very important indeed.")
                .author(marko)
                .post(post2)
                .build());

        log.info("Demo data initialized!");
        log.info("GraphiQL: http://localhost:8080/graphiql");
        log.info("H2 Console: http://localhost:8080/h2-console");
    }
}