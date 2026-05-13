package com.security.graphqlinjection.service;

import com.security.graphqlinjection.model.Comment;
import com.security.graphqlinjection.model.Post;
import com.security.graphqlinjection.model.User;
import com.security.graphqlinjection.repository.CommentRepository;
import com.security.graphqlinjection.repository.PostRepository;
import com.security.graphqlinjection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment createComment(String content, Long authorId, Long postId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found: " + authorId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

        Comment comment = Comment.builder()
                .content(content)
                .author(author)
                .post(post)
                .build();
        return commentRepository.save(comment);
    }
}