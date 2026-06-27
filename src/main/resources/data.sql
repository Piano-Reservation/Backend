INSERT INTO students (student_number, name, password, birth_date, grade, practice_course, status, created_at, updated_at)
VALUES
    ('202312001', '김테스트', '$2y$10$pS2MvZ9sstTuOfb3E7u5keoVHLev6CcVyvabcVn964IHKTWZ81CAi', '2005-03-15', 'FRESHMAN',  'PRACTICE_1', 'ACTIVE', NOW(), NOW()),
    ('202212001', '이테스트', '$2y$10$w1QzsL6nDe5KGU8/9V6LTurmKSlOT336s4cUfQOyxCkyWq9vMnZya', '2004-07-22', 'SOPHOMORE', 'PRACTICE_2', 'ACTIVE', NOW(), NOW()),
    ('202112001', '박테스트', '$2y$10$Vy9kMt0eCan8sjxf2huoVOfBKEK3AsuKJec/OcaDTL7gtPuZJL0c6', '2003-11-08', 'JUNIOR',    'PRACTICE_3', 'ACTIVE', NOW(), NOW()),
    ('202012001', '최테스트', '$2y$10$pS2MvZ9sstTuOfb3E7u5keoVHLev6CcVyvabcVn964IHKTWZ81CAi', '2002-05-20', 'SENIOR',    'PRACTICE_7', 'ACTIVE', NOW(), NOW());

INSERT INTO rooms (id, floor, code, name, active, created_at, updated_at)
VALUES
    (1, 'FIRST', '101', '1층 연습실', true, NOW(), NOW()),
    (2, 'THIRD', '301', '예술체육대학2-301호', true, NOW(), NOW()),
    (3, 'THIRD', '302', '예술체육대학2-302호', true, NOW(), NOW()),
    (4, 'THIRD', '303', '예술체육대학2-303호', true, NOW(), NOW()),
    (5, 'THIRD', '304', '예술체육대학2-304호', true, NOW(), NOW()),
    (6, 'THIRD', '310', '예술체육대학2-310호', true, NOW(), NOW()),
    (7, 'THIRD', '311', '예술체육대학2-311호', true, NOW(), NOW());

INSERT INTO room_allowed_courses (room_id, practice_course, created_at, updated_at)
VALUES
    (2, 'PRACTICE_3', NOW(), NOW()),
    (2, 'PRACTICE_4', NOW(), NOW()),
    (4, 'PRACTICE_5', NOW(), NOW()),
    (4, 'PRACTICE_6', NOW(), NOW()),
    (5, 'PRACTICE_1', NOW(), NOW()),
    (5, 'PRACTICE_2', NOW(), NOW()),
    (6, 'PRACTICE_7', NOW(), NOW()),
    (6, 'PRACTICE_8', NOW(), NOW()),
    (7, 'PRACTICE_7', NOW(), NOW()),
    (7, 'PRACTICE_8', NOW(), NOW());
