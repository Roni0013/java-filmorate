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
(1,'G','Нет возрастных ограничений'),
(2,'PG','Просмотр с родителями'),
(3,'PG-13','Детям до 13 лет просмотр не желателен'),
(4,'R','До 17 лет просматривать фильм можно только в присутствии взрослого'),
(5,'NC-17','Лицам до 18 лет просмотр запрещён') AS s(id,code,description)
ON m.id=s.id
WHEN NOT MATCHED THEN INSERT (id,code,description) VALUES (s.id, s.code,s.description);