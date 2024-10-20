-- Добавление тестовых пользователей
INSERT INTO USERS (email, login, name, birthday)
VALUES ('user1@example.com', 'user1login', 'User One', '1985-01-01'),
       ('user2@example.com', 'user2login', 'User Two', '1990-02-15'),
       ('user3@example.com', 'user3login', 'User Three', '1995-03-20');

-- Добавление тестовых друзей
INSERT INTO FRIENDS (user_id, friend_id)
VALUES (1, 2),
       (1, 3),
       (2, 3);

INSERT INTO MPA_RATINGS (name) VALUES ('G');
INSERT INTO MPA_RATINGS (name) VALUES ('PG');
INSERT INTO MPA_RATINGS (name) VALUES ('PG-13');

INSERT INTO GENRES (name) VALUES ('Комедия');
INSERT INTO GENRES (name) VALUES ('Драма');

INSERT INTO FILMS (name, description, release_date, duration, mpa_id)
VALUES ('Film One', 'Description for Film One', '2000-01-01', 120, 1);

INSERT INTO FILMS (name, description, release_date, duration, mpa_id)
VALUES ('Film Two', 'Description for Film Two', '2005-05-05', 150, 3);

INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (1, 1), (1, 2);
