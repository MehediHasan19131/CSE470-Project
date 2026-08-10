-- Demo posts + comments, so the Blog Feed has something to show right after
-- startup. Executed automatically right after health-data.sql (see
-- spring.sql.init.data-locations in application.properties).
--
-- Looked up by email rather than hard-coded id, same reason health-data.sql
-- does it. Guarded with "WHERE NOT EXISTS" (same reason as health-data.sql -
-- `posts`/`comments` have no unique key that would make INSERT IGNORE
-- naturally idempotent on their own) so re-running this on every restart
-- doesn't insert duplicate rows.

INSERT INTO posts (author_id, title, content, created_at, updated_at)
SELECT u.id,
       '5 Everyday Habits That Actually Protect Your Heart',
       'Cardiovascular disease is still the leading cause of death worldwide, but most of the risk comes down to a handful of daily habits. Walking briskly for thirty minutes most days, cutting back on added salt and processed food, keeping blood pressure and cholesterol checked at least once a year, sleeping seven to eight hours a night, and finding a real outlet for stress all move the needle more than any supplement. None of this is exotic advice - it is the boring, repeatable stuff that compounds over years.',
       '2026-07-20 09:15:00', '2026-07-20 09:15:00'
FROM users u
WHERE u.email = 'doctor@health.test'
  AND NOT EXISTS (SELECT 1 FROM posts p WHERE p.title = '5 Everyday Habits That Actually Protect Your Heart');

INSERT INTO posts (author_id, title, content, created_at, updated_at)
SELECT u.id,
       'What to Expect During Your First Emergency Room Visit',
       'Walking into an ER for the first time can be disorienting, especially when you are worried about someone you love. After you check in, a triage nurse will ask a short set of questions and assign a priority level - this is why patients with less visible but more urgent problems are sometimes seen before someone who arrived earlier. Bring a list of current medications and allergies if you can, and do not hesitate to ask the staff for a rough time estimate; they would rather you ask than assume the worst.',
       '2026-07-24 14:40:00', '2026-07-24 14:40:00'
FROM users u
WHERE u.email = 'hospital@health.test'
  AND NOT EXISTS (SELECT 1 FROM posts p WHERE p.title = 'What to Expect During Your First Emergency Room Visit');

INSERT INTO posts (author_id, title, content, created_at, updated_at)
SELECT u.id,
       'Living With Type 2 Diabetes: What I Wish I Knew Earlier',
       'I was diagnosed three years ago and spent the first few months trying to overhaul everything at once, which mostly just left me exhausted and discouraged. What actually worked was smaller and slower: swapping white rice for a mix of rice and vegetables a few nights a week, walking after dinner instead of straight to the couch, and checking my blood sugar at the same times every day so the numbers actually meant something. Sharing this in case it saves someone else the trial and error.',
       '2026-07-29 19:05:00', '2026-07-29 19:05:00'
FROM users u
WHERE u.email = 'patient@health.test'
  AND NOT EXISTS (SELECT 1 FROM posts p WHERE p.title = 'Living With Type 2 Diabetes: What I Wish I Knew Earlier');

-- Comments on "5 Everyday Habits..."
INSERT INTO comments (post_id, author_id, content, created_at)
SELECT p.id, u.id, 'This is really helpful, thank you doctor! Do you have a recommendation for tracking blood pressure at home?', '2026-07-20 11:02:00'
FROM posts p, users u
WHERE p.title = '5 Everyday Habits That Actually Protect Your Heart' AND u.email = 'patient@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM comments c WHERE c.post_id = p.id AND c.author_id = u.id
      AND c.content LIKE 'This is really helpful, thank you doctor!%'
  );

INSERT INTO comments (post_id, author_id, content, created_at)
SELECT p.id, u.id, 'A validated upper-arm cuff monitor is fine for home use - avoid wrist monitors, they tend to be less accurate.', '2026-07-20 13:30:00'
FROM posts p, users u
WHERE p.title = '5 Everyday Habits That Actually Protect Your Heart' AND u.email = 'doctor@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM comments c WHERE c.post_id = p.id AND c.author_id = u.id
      AND c.content LIKE 'A validated upper-arm cuff monitor%'
  );

-- Comment on "Living With Type 2 Diabetes..."
INSERT INTO comments (post_id, author_id, content, created_at)
SELECT p.id, u.id, 'Thank you for sharing this - the "same time every day" tip is something I have not been doing consistently.', '2026-07-30 08:12:00'
FROM posts p, users u
WHERE p.title = 'Living With Type 2 Diabetes: What I Wish I Knew Earlier' AND u.email = 'hospital@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM comments c WHERE c.post_id = p.id AND c.author_id = u.id
      AND c.content LIKE 'Thank you for sharing this%'
  );
