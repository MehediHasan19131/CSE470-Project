-- Demo medical history + allergies, so the Health Profile page has something
-- to show right after startup. Executed automatically right after
-- review-data.sql (see spring.sql.init.data-locations in application.properties).
--
-- Looked up by email rather than hard-coded id, same reason review-data.sql
-- does it - still works even if data.sql's demo accounts get reordered.
--
-- Uses "WHERE NOT EXISTS" (rather than INSERT IGNORE) because, unlike
-- `reviews`, neither table below has a unique key to make INSERT IGNORE
-- naturally idempotent - a patient can legitimately have several entries,
-- so nothing stops two different rows from having the same condition/allergen.
-- The NOT EXISTS guard just stops *this seed script* from re-inserting the
-- same demo rows every time the app restarts.

INSERT INTO medical_history (patient_id, condition_name, diagnosed_on, notes)
SELECT p.id, 'Type 2 Diabetes', '2022-03-14', 'Managed with metformin and diet. Reviewed every 6 months.'
FROM users p
WHERE p.email = 'patient@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM medical_history m WHERE m.patient_id = p.id AND m.condition_name = 'Type 2 Diabetes'
  );

INSERT INTO medical_history (patient_id, condition_name, diagnosed_on, notes)
SELECT p.id, 'Appendectomy', '2015-08-02', 'Laparoscopic appendectomy, no complications.'
FROM users p
WHERE p.email = 'patient@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM medical_history m WHERE m.patient_id = p.id AND m.condition_name = 'Appendectomy'
  );

INSERT INTO medical_history (patient_id, condition_name, diagnosed_on, notes)
SELECT p.id, 'Mild Asthma', '2010-01-20', 'Occasional inhaler use during high-pollen season.'
FROM users p
WHERE p.email = 'patient@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM medical_history m WHERE m.patient_id = p.id AND m.condition_name = 'Mild Asthma'
  );

INSERT INTO allergies (patient_id, allergen, severity, reaction)
SELECT p.id, 'Penicillin', 'SEVERE', 'Hives and difficulty breathing - avoid entirely, carries an EpiPen.'
FROM users p
WHERE p.email = 'patient@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM allergies a WHERE a.patient_id = p.id AND a.allergen = 'Penicillin'
  );

INSERT INTO allergies (patient_id, allergen, severity, reaction)
SELECT p.id, 'Peanuts', 'MODERATE', 'Swelling and stomach upset.'
FROM users p
WHERE p.email = 'patient@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM allergies a WHERE a.patient_id = p.id AND a.allergen = 'Peanuts'
  );

INSERT INTO allergies (patient_id, allergen, severity, reaction)
SELECT p.id, 'Dust', 'MILD', 'Sneezing and watery eyes.'
FROM users p
WHERE p.email = 'patient@health.test'
  AND NOT EXISTS (
      SELECT 1 FROM allergies a WHERE a.patient_id = p.id AND a.allergen = 'Dust'
  );
