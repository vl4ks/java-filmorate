-- Начальные данные для таблицы GENRES
INSERT INTO GENRES (name) VALUES ('Комедия');
INSERT INTO GENRES (name) VALUES ('Драма');
INSERT INTO GENRES (name) VALUES ('Мультфильм');
INSERT INTO GENRES (name) VALUES ('Триллер');
INSERT INTO GENRES (name) VALUES ('Документальный');
INSERT INTO GENRES (name) VALUES ('Боевик');

-- Начальные данные для таблицы MPA_RATINGS
INSERT INTO MPA_RATINGS (name) VALUES ('G');
INSERT INTO MPA_RATINGS (name) VALUES ('PG');
INSERT INTO MPA_RATINGS (name) VALUES ('PG-13');
INSERT INTO MPA_RATINGS (name) VALUES ('R');
INSERT INTO MPA_RATINGS (name) VALUES ('NC-17');

-- Начальные данные для таблицы USERS
INSERT INTO USERS (email, login, name, birthday) VALUES ('user1@example.com', 'user1login', 'User One', '1985-01-01');
INSERT INTO USERS (email, login, name, birthday) VALUES ('user2@example.com', 'user2login', 'User Two', '1990-02-02');
INSERT INTO USERS (email, login, name, birthday) VALUES ('user3@example.com', 'user3login', 'User Three', '1995-03-03');

-- Начальные данные для таблицы FILMS
INSERT INTO FILMS (name, description, release_date, duration)
VALUES
('Film One', 'Description for Film One', '2000-01-01', 120),
('Film Two', 'Description for Film Two', '2005-05-05', 150),
('Film Three', 'Description for Film Three', '2010-10-10', 90);

-- Начальные данные для таблицы FILM_GENRES
INSERT INTO FILM_GENRES (film_id, genre_id)
VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 4);

-- Начальные данные для таблицы FILM_RATINGS
INSERT INTO FILM_RATINGS (film_id, mpa_rating_id)
VALUES
(1, 1),
(2, 3),
(3, 4);

-- Начальные данные для таблицы FRIENDS
INSERT INTO FRIENDS (user_id, friend_id)
VALUES
(1, 2),
(1, 3),
(2, 3);

-- Начальные данные для таблицы LIKES
INSERT INTO LIKES (film_id, user_id)
VALUES
(1, 1),
(2, 2),
(3, 3);