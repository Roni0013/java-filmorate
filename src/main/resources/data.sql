MERGE INTO genre g
USING VALUES
(1,'Комедия'),
(2,'Драма'),
(3,'Мультфильм'),
(4,'Триллер'),
(5,'Документальный'),
(6,'Боевик') AS s (id, title)
ON g.id=s.id
WHEN NOT MATCHED THEN INSERT (id, title) VALUES (s.id, s.title);

MERGE INTO mpa m
USING VALUES
(1,'G'),
(2,'PG'),
(3,'PG-13'),
(4,'R'),
(5,'NC-17') AS s(id,code)
ON m.id=s.id
WHEN NOT MATCHED THEN INSERT (id,code) VALUES (s.id, s.code);
