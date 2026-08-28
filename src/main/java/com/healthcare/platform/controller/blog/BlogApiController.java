package com.healthcare.platform.controller.blog;
import com.healthcare.platform.model.blog.*;
import com.healthcare.platform.dto.blog.*;
import com.healthcare.platform.service.blog.*;

import com.healthcare.platform.model.auth.AuthUser;
import com.healthcare.platform.repository.auth.AuthUserJdbcRepository;
import com.healthcare.platform.model.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * JSON API for the Health Blog & Community feature (Sprint 4 task: Create
 * Post, Comment System). No role restriction beyond being logged in - see
 * {@link BlogService} for why. Reads the logged-in user via
 * {@link AuthUserJdbcRepository}, the same pattern {@code ReviewApiController}
 * and {@code HealthProfileApiController} use.
 */
@RestController
public class BlogApiController {

    private final BlogService blogService;
    private final AuthUserJdbcRepository authUsers;

    public BlogApiController(BlogService blogService, AuthUserJdbcRepository authUsers) {
        this.blogService = blogService;
        this.authUsers = authUsers;
    }

    // ---------------------------------------------------------------------
    // Posts - "Create Post"
    // ---------------------------------------------------------------------

    @GetMapping("/api/blog/posts")
    public List<PostResponse> feed() {
        return blogService.getFeed().stream().map(PostResponse::from).toList();
    }

    @GetMapping("/api/blog/posts/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(PostResponse.from(blogService.getPost(id)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/api/blog/posts")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostCreateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            Post saved = blogService.createPost(me.getId(), request.title(), request.content());
            return ResponseEntity.status(HttpStatus.CREATED).body(PostResponse.from(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/api/blog/posts/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @Valid @RequestBody PostUpdateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            Post updated = blogService.updatePost(id, me.getId(), request.title(), request.content());
            return ResponseEntity.ok(PostResponse.from(updated));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/api/blog/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            blogService.deletePost(id, me.getId(), me.getRole() == UserRole.ADMIN);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    // ---------------------------------------------------------------------
    // Comments - "Comment System"
    // ---------------------------------------------------------------------

    @GetMapping("/api/blog/posts/{postId}/comments")
    public ResponseEntity<?> comments(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(blogService.getComments(postId).stream().map(CommentResponse::from).toList());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/api/blog/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @Valid @RequestBody CommentCreateRequest request, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            Comment saved = blogService.addComment(postId, me.getId(), request.content());
            return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(saved));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/api/blog/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        AuthUser me = authUsers.findByEmail(authentication.getName().trim().toLowerCase()).orElseThrow();
        try {
            blogService.deleteComment(id, me.getId(), me.getRole() == UserRole.ADMIN);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }
}
