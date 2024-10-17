# java-filmorate
Template repository for Filmorate project.

Выполнила ТЗ 10 без дополнительного задания
 
Выполнила ТЗ 11

Выполнила ТЗ 12

## Схема БД

![filmorateDB](https://github.com/vl4ks/java-filmorate/commit/dc5234d183ddbb3ec4e75f6899b7d7e1bb585393)

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
SELECT u.* 
FROM USERS u 
JOIN FRIENDS f ON u.id = f.friend_id 
WHERE f.user_id = ?;
```


### Получение общих друзей двух пользователей:

```
SELECT u.*
FROM USERS u 
JOIN FRIENDS f1 ON u.id = f1.friend_id 
JOIN FRIENDS f2 ON u.id = f2.friend_id 
WHERE f1.user_id = ? AND f2.user_id = ?;
```

## SQL-запросы для модели **Film**

### Получение всех фильмов:

`SELECT * FROM FILMS;`

### Получение фильма по ID:

`SELECT * FROM FILMS WHERE id = ?;`

### Создание нового фильма:

```
INSERT INTO FILMS (name, description, release_date, duration) 
VALUES (?, ?, ?, ?);
```

### Обновление фильма:

```
UPDATE FILMS 
SET name = ?, description = ?, release_date = ?, duration = ? 
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
SELECT f.*, 
COUNT(fl.user_id) AS like_count
FROM FILMS AS f
LEFT JOIN LIKES AS fl ON f.id = fl.film_id
GROUP BY f.id
ORDER BY like_count DESC, f.id ASC
LIMIT ?;
```

