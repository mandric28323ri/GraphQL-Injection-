package com.security.graphqlinjection.service;

import com.security.graphqlinjection.model.Post;
import com.security.graphqlinjection.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> findByCategory(String category) {
        return postRepository.findByCategory(category);
    }

}