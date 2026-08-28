package com.healthcare.platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "faqs")
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(nullable = false)
    private boolean published = true;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Faq() { }

    public Faq(String question, String answer, int displayOrder) {
        this.question = question;
        this.answer = answer;
        this.displayOrder = displayOrder;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; touch(); }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; touch(); }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; touch(); }
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; touch(); }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    private void touch() { updatedAt = LocalDateTime.now(); }
}
