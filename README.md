# java-filmorate
Template repository for Filmorate project.

![ER-диаграмма](/schema_db.png)

<h2>Примеры запросов:</h2>
<code>--Users
-- Map<Long, User> getUsers()
SELECT * 
FROM users;</code>
  
<code>-- List<User> getAllUsers()
SELECT * 
FROM users;</code>
  
<code>-- User getUser(Long userId)
SELECT * 
FROM users
WHERE userId = <userId>;</code>
  
<code>-- User createUser(@Valid @RequestBody User user)
INSERT into users (userId, email, login, name, birthday, friend_status)
VALUES (<userId>, <email>, <login>, <name>, <birthday>, <friend_status>);</code>
   
<code>-- User updateUser(@Valid @RequestBody User user)
DELETE FROM users 
WHERE userId = <userId>;</code>
  
<code>INSERT into users (userId, email, login, name, birthday, friend_status)
VALUES (<userId>, <email>, <login>, <name>, <birthday>, <friend_status>);</code>
  
<code>-- User addToFriends(Long userId, Long friendId) 
INSERT INTO friends (userId, friendId)
VALUES (<userId>, <friendId>);
</code>
  
<code>-- User removeFromFriends(Long userId, Long friendId)
DELETE FROM friends
WHERE userId = <userId> AND friendId = <friendId>;</code>
  
<code>-- List<User> getAllFriends(Long userId)
SELECT *
FROM users AS u
INNER JOIN friends AS f ON u.userId = f.userId
WHERE f.userId = <userId>;</code>
  
<code>-- Set<User> getAllCommonFriends(Long userId, Long otherId)
SELECT *
FROM users AS u
INNER JOIN friends AS f ON u.userId = f.userId
WHERE f.userId = <userId> 
AND f.friendId = <otherId>;</code>

<code>-- Films
-- Map<Long, Film> getFilms() 
SELECT * 
FROM films;</code>
  
<code>-- List<Film> getAllFilms() 
SELECT * 
FROM films;</code>
  
<code>-- Film getFilm(Long filmId)
SELECT * 
FROM films
WHERE filmId = <filmId>;</code>
  
<code>-- Film createFilm(Film film)
INSERT into films (filmId, title, description, releaseDate, duration)
VALUES (<filmId>, <title>, <description>, <releaseDate>, <duration>);</code>
  
<code>-- Film updateFilm(Film film)
DELETE FROM films 
WHERE filmId = <filmId>;</code>
  
<code>INSERT into films (filmId, title, description, releaseDate, duration)
VALUES (<filmId>, <title>, <description>, <releaseDate>, <duration>);</code>
  
<code>-- void addLike(Long filmId, Long userId)
INSERT INTO likes (filmId, userId)
VALUES (<filmId>, <userId>);</code>
  
<code>-- void removeLike(Long filmId, Long userId)
DELETE FROM likes
WHERE filmId = <filmId> AND userId = <userId>;</code>
  
<code>-- List<Film> getPopular(Long count)
SELECT * 
FROM films AS f
INNER JOIN likes as l ON f.filmId = l.filmId 
ORDER BY COUNT(l.userId) 
LIMIT count;</code>
