package com.healthcare.platform.service;

import com.healthcare.platform.model.Faq;
import com.healthcare.platform.repository.FaqRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class FaqService {
    private final FaqRepository faqs;

    public FaqService(FaqRepository faqs) { this.faqs = faqs; }

    public List<Faq> publicFaqs() { return faqs.findByPublishedTrueOrderByDisplayOrderAscIdAsc(); }
    public List<Faq> allFaqs() { return faqs.findAllByOrderByDisplayOrderAscIdAsc(); }
    public Faq get(Long id) { return faqs.findById(id).orElseThrow(() -> new NoSuchElementException("FAQ not found")); }

    public Faq create(String question, String answer, boolean published, int displayOrder) {
        return faqs.save(populate(new Faq(), question, answer, published, displayOrder));
    }

    public Faq update(Long id, String question, String answer, boolean published, int displayOrder) {
        return faqs.save(populate(get(id), question, answer, published, displayOrder));
    }

    public void delete(Long id) { faqs.delete(get(id)); }

    private Faq populate(Faq faq, String question, String answer, boolean published, int displayOrder) {
        if (question == null || question.trim().isEmpty() || answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("A question and answer are required.");
        }
        faq.setQuestion(question.trim());
        faq.setAnswer(answer.trim());
        faq.setPublished(published);
        faq.setDisplayOrder(Math.max(0, displayOrder));
        return faq;
    }
}
