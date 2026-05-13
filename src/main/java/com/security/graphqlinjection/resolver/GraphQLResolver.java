package com.security.graphqlinjection.resolver;

import com.security.graphqlinjection.model.Comment;
import com.security.graphqlinjection.model.Post;
import com.security.graphqlinjection.model.User;
import com.security.graphqlinjection.service.CommentService;
import com.security.graphqlinjection.service.PostService;
import com.security.graphqlinjection.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class GraphQLResolver {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @QueryMapping
    public List<User> users() {
        return userService.findAll();
    }

    @QueryMapping
    public User userById(@Argument String id) {
        return userService.findById(Long.parseLong(id)).orElse(null);
    }

    @QueryMapping
    public List<User> searchUsers(@Argument String searchTerm) {
        System.out.println("[VULNERABLE] searchUsers called with: " + searchTerm);
        return userService.searchUsers(searchTerm);
    }

    @QueryMapping
    public List<Post> posts() {
        return postService.findAll();
    }

    @QueryMapping
    public Post postById(@Argument String id) {
        return postService.findById(Long.parseLong(id)).orElse(null);
    }

    @QueryMapping
    public List<Post> postsByCategory(@Argument String category) {
        System.out.println("[VULNERABLE] postsByCategory called with: " + category);
        return postService.findByCategory(category);
    }

    @QueryMapping
    public List<Comment> comments() {
        return commentService.findAll();
    }

    @QueryMapping
    public List<Comment> commentsByPost(@Argument String postId) {
        return commentService.findByPostId(Long.parseLong(postId));
    }

    @MutationMapping
    public User createUser(@Argument Map<String, Object> input) {
        return userService.createUser(
                (String) input.get("username"),
                (String) input.get("email"),
                (String) input.get("password"),
                (String) input.getOrDefault("role", "USER")
        );
    }

    @MutationMapping
    public User updateUser(@Argument String id, @Argument Map<String, Object> input) {
        return userService.updateUser(
                Long.parseLong(id),
                (String) input.get("username"),
                (String) input.get("email"),
                (String) input.get("password"),
                (String) input.get("role")
        ).orElse(null);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument String id) {
        return userService.deleteUser(Long.parseLong(id));
    }

    @MutationMapping
    public Post createPost(@Argument Map<String, Object> input) {
        return postService.createPost(
                (String) input.get("title"),
                (String) input.get("content"),
                (String) input.get("category"),
                Long.parseLong(input.get("authorId").toString())
        );
    }

    @MutationMapping
    public Boolean deletePost(@Argument String id) {
        return postService.deletePost(Long.parseLong(id));
    }

    @MutationMapping
    public Comment createComment(@Argument Map<String, Object> input) {
        return commentService.createComment(
                (String) input.get("content"),
                Long.parseLong(input.get("authorId").toString()),
                Long.parseLong(input.get("postId").toString())
        );
    }

    @MutationMapping
    public Map<String, Object> login(@Argument String username, @Argument String password) {
        Optional<User> userOpt = userService.login(username, password);
        Map<String, Object> payload = new HashMap<>();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            payload.put("token", "fake-jwt-" + user.getId() + "-" + System.currentTimeMillis());
            payload.put("user", user);
            payload.put("rawPassword", password);
        } else {
            payload.put("token", null);
            payload.put("user", null);
            payload.put("rawPassword", null);
        }
        return payload;
    }

    @SchemaMapping(typeName = "User", field = "posts")
    public List<Post> userPosts(User user) {
        return user.getPosts();
    }

    @SchemaMapping(typeName = "Post", field = "author")
    public User postAuthor(Post post) {
        return post.getAuthor();
    }

    @SchemaMapping(typeName = "Post", field = "comments")
    public List<Comment> postComments(Post post) {
        return commentService.findByPostId(post.getId());
    }

    @SchemaMapping(typeName = "Comment", field = "author")
    public User commentAuthor(Comment comment) {
        return comment.getAuthor();
    }

    @SchemaMapping(typeName = "Comment", field = "post")
    public Post commentPost(Comment comment) {
        return comment.getPost();
    }
}