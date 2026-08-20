package com.healthcare.platform.blog;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business rules for the Health Blog & Community feature (Sprint 4 task:
 * Database: Posts, Comments · Backend: Create Post, Comment System ·
 * Frontend: Blog Feed, Post Details).
 * <p>
 * Open to every authenticated role, not just one - the task doesn't name an
 * RBAC restriction the way Sprint 3's Health Profile clearly implied
 * "per patient", so this reads it as a community space any logged-in account
 * (patient or provider) can post and comment in. See README for this call.
 * <p>
 * Only ownership is enforced: editing or deleting a post/comment requires
 * being its original author, the same check {@code ReviewService} uses for
 * "only the original reviewer may update it" and {@code HealthProfileService}
 * uses for "only the owning patient may update it".
 */
@Service
public class BlogService {

    private final BlogJdbcRepository blog;

    public BlogService(BlogJdbcRepository blog) {
        this.blog = blog;
    }

    // ---------------------------------------------------------------------
    // Posts - "Create Post"
    // ---------------------------------------------------------------------

    public List<Post> getFeed() {
        return blog.findAllPosts();
    }

    public Post getPost(Long id) {
        return blog.findPostById(id).orElseThrow(() -> new NoSuchElementException("Post not found."));
    }

    /** "Create Post" - publishes a new post authored by the given user. */
    public Post createPost(Long authorId, String title, String content) {
        Post post = new Post();
        post.setAuthorId(authorId);
        post.setTitle(requireTitle(title));
        post.setContent(requireContent(content));
        return blog.insertPost(post);
    }

    /** Extra beyond the assigned backend items - see README. Only the original author may edit it. */
    public Post updatePost(Long id, Long requesterId, String title, String content) {
        Post existing = blog.findPostById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found."));

        if (!existing.getAuthorId().equals(requesterId)) {
            throw new IllegalStateException("You can only edit your own posts.");
        }

        blog.updatePost(id, requireTitle(title), requireContent(content));
        return blog.findPostById(id).orElseThrow();
    }

    /** The original author may delete their own post; an admin may delete any post (moderation). */
    public void deletePost(Long id, Long requesterId, boolean isAdmin) {
        Post existing = blog.findPostById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found."));

        if (!isAdmin && !existing.getAuthorId().equals(requesterId)) {
            throw new IllegalStateException("You can only delete your own posts.");
        }

        blog.deletePost(id);
    }

    // ---------------------------------------------------------------------
    // Comments - "Comment System"
    // ---------------------------------------------------------------------

    public List<Comment> getComments(Long postId) {
        // Fail fast with a clear 404 if the post itself doesn't exist, rather
        // than silently returning an empty comment list for a bad id.
        blog.findPostById(postId).orElseThrow(() -> new NoSuchElementException("Post not found."));
        return blog.findCommentsByPost(postId);
    }

    public Comment addComment(Long postId, Long authorId, String content) {
        blog.findPostById(postId).orElseThrow(() -> new NoSuchElementException("Post not found."));

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setContent(requireContent(content));
        return blog.insertComment(comment);
    }

    /** The original author may delete their own comment; an admin may delete any comment (moderation). */
    public void deleteComment(Long id, Long requesterId, boolean isAdmin) {
        Comment existing = blog.findCommentById(id)
                .orElseThrow(() -> new NoSuchElementException("Comment not found."));

        if (!isAdmin && !existing.getAuthorId().equals(requesterId)) {
            throw new IllegalStateException("You can only delete your own comments.");
        }

        blog.deleteComment(id);
    }

    /** Called from {@code AdminUserService.deleteUser(...)} before deleting the user row - see that class for why. */
    public void deleteAllForAuthor(Long authorId) {
        blog.deletePostsAndCommentsForAuthor(authorId);
    }

    private String requireTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }
        return title.trim();
    }

    private String requireContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content can't be empty.");
        }
        return content.trim();
    }
}
