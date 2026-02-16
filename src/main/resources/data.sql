DELETE FROM users_friendship;
DELETE FROM films_likes;
DELETE FROM films_genres;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM mpa_ratings;

ALTER TABLE mpa_ratings ALTER COLUMN id RESTART WITH 1;
INSERT INTO mpa_ratings (name) VALUES
('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
INSERT INTO genres (name) VALUES
('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
INSERT INTO users (name, email, login, birthday) VALUES
('Иван Иванов', 'ivan@mail.com', 'ivan_ivanov', '1990-05-15'),
('Мария Петрова', 'maria@mail.com', 'maria_pet', '1992-08-22'),
('Алексей Сидоров', 'alex@mail.com', 'alex_sid', '1988-11-30'),
('Елена Сидорова', 'elena@mail.com', 'elena_sid', '1988-11-30');

ALTER TABLE films ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users_friendship ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films_likes ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films_genres ALTER COLUMN id RESTART WITH 1;

