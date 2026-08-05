-- Reference copy - identical to src/main/resources/review-data.sql, which is
-- the one Spring Boot actually executes automatically on startup.
--
-- Demo reviews, so the review/rating pages have something to show right after
-- startup. Executed automatically right after data.sql (see
-- spring.sql.init.data-locations in application.properties).
--
-- Looked up by email rather than hard-coded id, so this still works even if
-- the demo accounts in data.sql get reordered or added to later.
INSERT IGNORE INTO reviews (reviewer_id, target_id, rating, comment)
SELECT p.id, d.id, 5, 'Dr. Khan was thorough and explained everything clearly. Highly recommend.'
FROM users p, users d
WHERE p.email = 'patient@health.test' AND d.email = 'doctor@health.test';

INSERT IGNORE INTO reviews (reviewer_id, target_id, rating, comment)
SELECT p.id, h.id, 4, 'Clean facility and friendly staff, but the wait time was a bit long.'
FROM users p, users h
WHERE p.email = 'patient@health.test' AND h.email = 'hospital@health.test';

INSERT IGNORE INTO reviews (reviewer_id, target_id, rating, comment)
SELECT p.id, ph.id, 5, 'Fast delivery and the pharmacist was very helpful with dosage questions.'
FROM users p, users ph
WHERE p.email = 'patient@health.test' AND ph.email = 'pharmacy@health.test';

-- Keep the `ratings` aggregate table in sync with the demo reviews above, the
-- same way ReviewJdbcRepository.refreshRatingSummary(...) does at runtime.
INSERT INTO rating_summaries (target_id, average_rating, total_reviews)
SELECT target_id, AVG(rating), COUNT(*) FROM reviews GROUP BY target_id
ON DUPLICATE KEY UPDATE
    average_rating = VALUES(average_rating),
    total_reviews  = VALUES(total_reviews);
