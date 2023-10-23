DELETE
FROM meals;
DELETE
FROM user_role;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date_time, description, calories)
VALUES (100000, '2023-10-21 10:00', 'Завтрак User', 500),
       (100000, '2023-10-21 14:00', 'Обед User', 900),
       (100000, '2023-10-21 19:00', 'Ужин User', 800),
       (100001, '2023-10-20 09:00', 'Завтрак Admin', 300),
       (100001, '2023-10-20 13:00', 'Обед Admin', 850),
       (100001, '2023-10-20 18:00', 'Ужин Admin', 800),
       (100000, '2023-10-23 09:30', 'Завтрак User', 350),
       (100000, '2023-10-23 13:45', 'Обед User', 1000),
       (100000, '2023-10-23 19:00', 'Ужин User', 600),
       (100001, '2023-10-22 10:15', 'Завтрак Admin', 300),
       (100001, '2023-10-22 14:00', 'Обед Admin', 850),
       (100001, '2023-10-22 19:30', 'Ужин Admin', 1000);
