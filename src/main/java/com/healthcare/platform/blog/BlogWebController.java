package com.healthcare.platform.blog;

import com.healthcare.platform.auth.AuthUser;
import com.healthcare.platform.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

/**
 * Server-rendered pages for Sprint 4's two assigned frontend items:
 * <p>
 * - {@code GET /blog} — "Blog Feed": every post, newest first, plus the
 * "Create Post" composer (backend item 1 lives here, the same way a social
 * feed's compose box sits above the feed itself).
 * <p>
 * - {@code GET /blog/posts/{id}} — "Post Details": one post in full, plus the
 * "Comment System" (backend item 2 lives here — the list of comments and the
 * add-comment form).
 * <p>
 * Editing a post re-uses the compose form on the Feed page in "edit" mode via
 * {@code ?editPost={id}}, the same query-string trick
 * {@code HealthProfileWebController} uses, rather than a third page.
 */
@Controller
public class BlogWebController {

    private final BlogService blogService;
    private final CurrentUserService currentUserService;

    public BlogWebController(BlogService blogService, CurrentUserService currentUserService) {
        this.blogService = blogService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/blog")
    public String feed(@RequestParam(required = false) Long editPost, Authentication authentication, Model model) {
        AuthUser me = currentUserService.get(authentication);
        model.addAttribute("me", me);
        model.addAttribute("posts", blogService.getFeed());

        if (editPost != null) {
            try {
                Post post = blogService.getPost(editPost);
                if (post.getAuthorId().equals(me.getId())) {
                    model.addAttribute("editPostEntry", post);
                }
            } catch (NoSuchElementException ignored) {
                // Stale link to a deleted post - just show the normal "add" composer.
            }
        }
        return "blog-feed";
    }

    @PostMapping("/blog/posts")
    public String createPost(@RequestParam String title, @RequestParam String content,
                              Authentication authentication, Model model) {
        AuthUser me = currentUserService.get(authentication);
        try {
            blogService.createPost(me.getId(), title, content);
            return "redirect:/blog?postSaved=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("me", me);
            model.addAttribute("posts", blogService.getFeed());
            model.addAttribute("postError", e.getMessage());
            return "blog-feed";
        }
    }

    @PostMapping("/blog/posts/{id}")
    public String updatePost(@PathVariable Long id, @RequestParam String title, @RequestParam String content,
                              Authentication authentication, Model model) {
        AuthUser me = currentUserService.get(authentication);
        try {
            blogService.updatePost(id, me.getId(), title, content);
            return "redirect:/blog?postSaved=true";
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            model.addAttribute("me", me);
            model.addAttribute("posts", blogService.getFeed());
            model.addAttribute("postError", e.getMessage());
            return "blog-feed";
        }
    }

    @PostMapping("/blog/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, Authentication authentication) {
        AuthUser me = currentUserService.get(authentication);
        try {
            blogService.deletePost(id, me.getId());
            return "redirect:/blog?postDeleted=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/blog?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/blog/posts/{id}")
    public String postDetails(@PathVariable Long id, Authentication authentication, Model model) {
        AuthUser me = currentUserService.get(authentication);
        try {
            model.addAttribute("me", me);
            model.addAttribute("post", blogService.getPost(id));
            model.addAttribute("comments", blogService.getComments(id));
            return "post-details";
        } catch (NoSuchElementException e) {
            return "redirect:/blog?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/blog/posts/{id}/comments")
    public String addComment(@PathVariable Long id, @RequestParam String content,
                              Authentication authentication, Model model) {
        AuthUser me = currentUserService.get(authentication);
        try {
            blogService.addComment(id, me.getId(), content);
            return "redirect:/blog/posts/" + id + "?commentSaved=true";
        } catch (NoSuchElementException e) {
            return "redirect:/blog?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            model.addAttribute("me", me);
            model.addAttribute("post", blogService.getPost(id));
            model.addAttribute("comments", blogService.getComments(id));
            model.addAttribute("commentError", e.getMessage());
            return "post-details";
        }
    }

    @PostMapping("/blog/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication) {
        AuthUser me = currentUserService.get(authentication);
        try {
            blogService.deleteComment(commentId, me.getId());
            return "redirect:/blog/posts/" + postId + "?commentDeleted=true";
        } catch (IllegalStateException | NoSuchElementException e) {
            return "redirect:/blog/posts/" + postId + "?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}
