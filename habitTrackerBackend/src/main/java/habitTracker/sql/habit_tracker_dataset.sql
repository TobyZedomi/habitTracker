INSERT INTO `users` (`username`, `display_name`, `email`, `password_hash`, `date_of_birth`, `is_admin`, `created_at`, `user_image`)
VALUES ('admin', 'adminUser123', 'tobyzedo7@gmail.com', '$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi', '2003-02-16', true, '2025-01-30 00:00:00','DefaultUserImage.jpg'),
       ('Andrew', 'andrewGamer123', 'andrew@gmail.com', '$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi', '2000-12-10', false, '2025-01-30 00:00:00','DefaultUserImage.jpg'),
       ('Toby', 'toby123', 'toby@gmail.com', '$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi',  '2001-12-08', false, '2025-01-30 00:00:00','DefaultUserImage.jpg'),
       ('Kate', 'kate123', 'kate@gmail.com', '$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi',  '2001-12-08', false, '2025-01-30 00:00:00','DefaultUserImage.jpg'),
       ('James', 'james123', 'james@gmail.com', '$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi', '1999-11-18', false, '2025-01-30 00:00:00','DefaultUserImage.jpg'),
       ('Alice','Alice','alicecunninghammorgan@hotmail.com','$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi','2000-11-13',false,'2025-03-03 23:24:22','DefaultUserImage.jpg'),
       ('Kevin','Kevin','Kevin@gmail.com','$2a$12$x4EwpUD5VU.vJW1.xICz1OnEJqEMfdYx/ttl/Gi/JxljZAsguzqbi','2003-11-13',true,'2025-03-03 18:26:25','DefaultUserImage.jpg');


INSERT INTO activity_types (name, activity_done)
VALUES
    ('Running','Currently Running'),
    ('Walking','Currently Walking'),
    ('Swimming','Currently Swimming'),
    ('Cycling','Currently Cycling'),
    ('Dancing','Currently Dancing'),
    ('Gym','Currently at the Gym');

INSERT INTO habits (username, activity_type_id, description, habit_reminder, habit_target, habit_frequency, is_active)
VALUES
    ('Andrew',1,'Morning run','08:00',5,7,TRUE),
    ('Toby',2,'Daily walk','18:00',7,7,TRUE),
    ('Kate',3,'Swim training','17:00',3,7,TRUE),
    ('James',4,'Cycling sessions','09:00',2,7,TRUE),
    ('Alice',6,'Gym workout','19:00',4,7,TRUE),
    ('Kevin',5,'Dance practice','20:00',3,7,TRUE);

INSERT INTO habit_tracker_log
(habit_id, date_of_activity, duration_minutes, distance_km, calories_burned, notes, created_at)
VALUES
    (1,'2026-03-10',30,5.00,300,'Morning park run','2026-03-10 08:30:00'),
    (1,'2026-03-11',28,4.80,280,'Felt good','2026-03-11 08:25:00'),
    (2,'2026-05-13',45,3.50,150,'Evening walk','2026-03-10 18:40:00'),
    (3,'2026-03-09',60,NULL,400,'Swimming laps','2026-03-09 17:50:00'),
    (4,'2026-03-08',50,15.00,500,'Road cycling','2026-03-08 09:50:00'),
    (5,'2026-03-11',70,NULL,450,'Leg day','2026-03-11 19:30:00'),
    (6,'2026-03-11',40,NULL,250,'Dance routine','2026-03-11 20:45:00');

INSERT INTO streaks (habit_id, current_streak, longest_streak, last_completed_date)
VALUES
    (1,2,5,'2026-03-11'),
    (2,1,3,'2026-03-10'),
    (3,1,4,'2026-03-09'),
    (4,1,2,'2026-03-08'),
    (5,1,6,'2026-03-11'),
    (6,1,3,'2026-03-11');

