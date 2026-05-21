package com.security.graphqlinjection.service;

import com.security.graphqlinjection.model.Post;
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
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;
    // RANJIVO : IDOR
    public List<Post> findAll() {
        return postRepository.findAll()
                .stream().filter(p -> !p.getCategory().equals("hidden"))
                .collect(java.util.stream.Collectors.toList());
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    // RANJIVO: JPQL injection kroz category parametar
    @SuppressWarnings("unchecked")
    public List<Post> findByCategory(String category) {
        String jpql = "SELECT p FROM Post p WHERE p.category = '" + category + "'";
        System.out.println("[VULNERABLE] Executing JPQL: " + jpql);
        return entityManager.createQuery(jpql).getResultList();
    }

    public Post createPost(String title, String content, String category, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found: " + authorId));
        Post post = Post.builder()
                .title(title)
                .content(content)
                .category(category)
                .author(author)
                .build();
        return postRepository.save(post);
    }

    public boolean deletePost(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public Optional<Post> updatePost(Long id, String title, String content, String category) {
        return postRepository.findById(id).map(post -> {
            if (title != null) post.setTitle(title);
            if (content != null) post.setContent(content);
            if (category != null) post.setCategory(category);
            return postRepository.save(post);
        });
    }
}