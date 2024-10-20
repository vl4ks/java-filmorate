# java-filmorate
Template repository for Filmorate project.

Выполнила ТЗ 10 без дополнительного задания

Выполнила ТЗ 11

Выполнила ТЗ 12

## Схема БД

![filmorate2](https://github.com/vl4ks/java-filmorate/commit/7a9ad9db6316f0113b66eca4e1789d5f380b0b5a)

## SQL-запросы для модели **User**

### Получение всех пользователей:

`SELECT * FROM USERS;`

### Получение пользователя по ID:

```
SELECT * FROM USERS WHERE id = ?;
```

### Создание нового пользователя:

```
INSERT INTO USERS (name, login, email, birthday) VALUES (?, ?, ?, ?);
```
### Обновление пользователя:

```
UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?;
```


### Удаление пользователя:

```
DELETE FROM USERS WHERE id = ?;
```

### Добавление друга:

```
INSERT INTO FRIENDS (user_id, friend_id) 
VALUES (?, ?);
```

### Удаление друга:

```
DELETE FROM FRIENDS 
WHERE user_id = ? AND friend_id = ?;
```

### Получение списка друзей пользователя:

```
SELECT friend_id 
FROM FRIENDS 
WHERE user_id = ?
```


### Получение общих друзей двух пользователей:

```
SELECT friend_id 
FROM FRIENDS 
WHERE user_id = ? 
AND friend_id IN (
    SELECT friend_id FROM FRIENDS WHERE user_id = ?
);
```

## SQL-запросы для модели **Film**

### Получение всех фильмов с рейтингами:

```
SELECT *
FROM FILMS, MPA_RATINGS
WHERE FILMS.mpa_id = MPA_RATINGS.id
""";
```

### Получение фильма по ID:

`SELECT * FROM FILMS WHERE id = ?;`

### Создание нового фильма:

```
INSERT INTO FILMS (name, description, release_date, duration, mpa_id) 
VALUES (?, ?, ?, ?, ?);
```

### Обновление фильма:

```
UPDATE FILMS 
SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? 
WHERE id = ?;
```

### Удаление фильма:

`DELETE FROM FILMS WHERE id = ?;`

### Добавление лайка к фильму:

```
INSERT INTO LIKES (film_id, user_id) 
VALUES (?, ?);
```

### Удаление лайка у фильма:

```
DELETE FROM LIKES 
WHERE film_id = ? AND user_id = ?;
```

### Получение списка популярных фильмов:

```
SELECT FILMS.*, COUNT(LIKES.user_id) AS like_count
FROM FILMS
LEFT JOIN LIKES ON FILMS.id = LIKES.film_id
GROUP BY FILMS.id
ORDER BY like_count DESC, FILMS.id ASC
LIMIT ?;
""";
```

### Получение количества лайков для фильма:

```
SELECT COUNT(user_id) 
FROM LIKES 
WHERE film_id = ?;
```
