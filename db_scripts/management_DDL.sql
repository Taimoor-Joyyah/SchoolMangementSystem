drop database if exists management;
create database management;
use management;

create table admin
(
    id       int auto_increment
        primary key,
    username varchar(45)  not null,
    password varchar(256) not null,
    email    varchar(45)  not null
);

create table grade
(
    id    int auto_increment
        primary key,
    title varchar(45) not null,
    constraint grade_title_UNIQUE
        unique (title)
);

create table memo
(
    id          int auto_increment
        primary key,
    title       varchar(45)   null,
    description varchar(1024) not null,
    `to`        char          not null
);

create table parent
(
    cnic     char(13)     not null
        primary key,
    name     varchar(45)  not null,
    phone    char(11)     not null,
    email    varchar(45)  not null,
    password varchar(256) not null,
    constraint phone_UNIQUE
        unique (phone)
);

create table student
(
    registration_no int auto_increment
        primary key,
    username        varchar(45)  not null,
    first_name      varchar(45)  not null,
    last_name       varchar(45)  not null,
    password        varchar(256) not null,
    birth_date      date         not null,
    gender          char         not null,
    address         varchar(256) not null,
    email           varchar(45)  not null,
    cnic            char(13)     not null,
    parent_cnic     char(13)     not null,
    grade_id        int          not null,
    constraint cnic_UNIQUE
        unique (cnic),
    constraint username_UNIQUE
        unique (username),
    constraint fk_students_grades1
        foreign key (grade_id) references grade (id),
    constraint fk_students_parents1
        foreign key (parent_cnic) references parent (cnic)
);

create table fee
(
    serial_no               int auto_increment
        primary key,
    amount                  decimal                       not null,
    due_date                date                          not null,
    late_charges            decimal                       not null,
    status                  varchar(45) default 'pending' not null,
    student_registration_no int                           not null,
    constraint fk_fees_students1
        foreign key (student_registration_no) references student (registration_no)
);

create index fk_fees_students1_idx
    on fee (student_registration_no);

create index fk_students_grades1_idx
    on student (grade_id);

create index fk_students_parents1_idx
    on student (parent_cnic);

create definer = root@localhost trigger student_after_insert
    after insert
    on student
    for each row
begin
    insert into student_subject_attendence(student_registration_no, subject_id)
    select New.registration_no, id
    from subject
    where grade_id = NEW.grade_id;
end;

create table teacher
(
    teacher_id int auto_increment
        primary key,
    username   varchar(45)  not null,
    first_name varchar(45)  not null,
    last_name  varchar(45)  not null,
    password   varchar(256) not null,
    birth_date date         not null,
    email      varchar(45)  not null,
    gender     char         not null,
    phone      varchar(11)  not null,
    address    varchar(256) not null,
    cnic       char(13)     not null,
    constraint cnic_UNIQUE
        unique (cnic),
    constraint phone_UNIQUE
        unique (phone),
    constraint username_UNIQUE
        unique (username)
);

create table subject
(
    id         int auto_increment
        primary key,
    title      varchar(45) not null,
    teacher_id int         not null,
    grade_id   int         not null,
    constraint fk_subjects_grades1
        foreign key (grade_id) references grade (id),
    constraint fk_subjects_teachers
        foreign key (teacher_id) references teacher (teacher_id)
);

create table quiz
(
    no            int auto_increment,
    subject_id    int          not null,
    title         varchar(45)  not null,
    total_marks   int          not null,
    date_taken    date         not null,
    document_path varchar(256) null,
    primary key (no, subject_id),
    constraint fk_quizzes_subjects1
        foreign key (subject_id) references subject (id)
);

create table mark
(
    student_registration_no int            not null,
    subject_id              int            not null,
    quiz_no                 int            not null,
    obtained_marks          int default -1 not null,
    answer_path             varchar(256)   null,
    primary key (student_registration_no, subject_id, quiz_no),
    constraint fk_marks_quizzes1
        foreign key (quiz_no, subject_id) references quiz (no, subject_id),
    constraint fk_marks_students1
        foreign key (student_registration_no) references student (registration_no)
);

create index fk_marks_students1_idx
    on mark (student_registration_no);

create index fk_quizzes_subjects1_idx
    on quiz (subject_id);

create definer = root@localhost trigger quiz_after_insert
    after insert
    on quiz
    for each row
begin
    INSERT INTO mark(student_registration_no, subject_id, quiz_no)
    SELECT student,
           NEW.subject_id,
           NEW.no
    FROM subject_students ss
    WHERE ss.subject = NEW.subject_id;
end;

create index fk_subjects_grades1_idx
    on subject (grade_id);

create index fk_subjects_teachers_idx
    on subject (teacher_id);

create definer = root@localhost trigger subject_after_insert
    after insert
    on subject
    for each row
begin
    insert into subject_attendence_count(subject_id)
        value (New.id);
end;

create table subject_attendence_count
(
    subject_id int           not null
        primary key,
    count      int default 0 not null,
    constraint fk_subject_classes_subject1
        foreign key (subject_id) references subject (id)
);

create table student_subject_attendence
(
    student_registration_no int           not null,
    subject_id              int           not null,
    attendence              int default 0 not null,
    primary key (student_registration_no, subject_id),
    constraint fk_student_subject_attendence_student1
        foreign key (student_registration_no) references student (registration_no),
    constraint fk_student_subject_attendence_subject_attendence_count1
        foreign key (subject_id) references subject_attendence_count (subject_id)
);

create index fk_student_subject_attendence_subject_attendence_count1_idx
    on student_subject_attendence (subject_id);

create table timetable
(
    id      int auto_increment
        primary key,
    `table` varchar(1024) null
);

create definer = root@localhost view subject_students as
select `s`.`id` AS `subject`, `st`.`registration_no` AS `student`
from (`management`.`subject` `s` join `management`.`student` `st` on ((`s`.`grade_id` = `st`.`grade_id`)));

create
    definer = root@localhost procedure add_quiz(IN subject int, IN aTitle varchar(45), IN total int, IN taken date)
BEGIN
    INSERT INTO quiz(subject_id, title, total_marks, date_taken)
        VALUE (subject, aTitle, total, taken);
END;

create
    definer = root@localhost procedure admin_fees_detail()
begin
    select g.title                                as grade,
           s.registration_no                      as student_id,
           concat(s.first_name, ' ', s.last_name) as student_name,
           f.amount                               as fee,
           f.due_date                             as due_date,
           f.late_charges                         as late_charges,
           f.status                               as status
    from fee f
             join student s on s.registration_no = f.student_registration_no
             join grade g on g.id = s.grade_id;
end;

create
    definer = root@localhost procedure create_memo(IN atitle varchar(45), IN adescription varchar(1024), IN ato char)
BEGIN
    INSERT INTO memo(title, description, `to`) VALUE (atitle, adescription, ato);
END;

create
    definer = root@localhost function exists_admin(aUsername varchar(45)) returns tinyint(1) deterministic
BEGIN
    declare admin_id int default -1;
    select id into admin_id from admin where username = aUsername or email = aUsername;
    if admin_id = -1 then
        return false;
    end if;
    return true;
END;

create
    definer = root@localhost function exists_parent(acnic char(13)) returns tinyint(1) deterministic
BEGIN
    declare parent_cnic char(13) default 'xxxxxxxxxxxxx';
    select cnic into parent_cnic from parent where cnic = aCnic;
    if parent_cnic = 'xxxxxxxxxxxxx' then
        return false;
    end if;
    return true;
END;

create
    definer = root@localhost function exists_student(aCnic char(13)) returns tinyint(1) deterministic
BEGIN
    declare student_id int default -1;
    select registration_no into student_id from student where cnic = aCnic;
    if student_id = -1 then
        return false;
    end if;
    return true;
END;

create
    definer = root@localhost function exists_teacher(acnic char(13)) returns tinyint(1) deterministic
BEGIN
    declare id int default -1;
    select teacher_id into id from teacher where cnic = aCnic;
    if id = -1 then
        return false;
    end if;
    return true;
END;

create
    definer = root@localhost procedure get_quizzes_by_subject(IN subjectid int)
begin
    select no, subject_id, title, total_marks, date_taken, document_path
    from quiz
    where subject_id = subjectid;
end;

create
    definer = root@localhost procedure get_students_by_subject(IN subjectid int)
begin
    select s.registration_no                      as id,
           concat(s.first_name, ' ', s.last_name) as name,
           sac.count                              as total,
           ssa.attendence                         as present
    from student s
             join student_subject_attendence ssa
                  on s.registration_no = ssa.student_registration_no and ssa.subject_id = subjectid
             join subject_attendence_count sac on sac.subject_id = ssa.subject_id;
end;

create
    definer = root@localhost function get_teacher_subject_id(teacher int, grade int) returns int deterministic
BEGIN
    DECLARE subject INT DEFAULT -1;
    SELECT s.id
    INTO subject
    FROM subject s
             JOIN teacher t ON t.teacher_id = s.teacher_id
             JOIN grade g ON g.id = s.grade_id
    WHERE g.id = grade
      AND t.teacher_id = teacher;
    RETURN subject;
END;

create
    definer = root@localhost function login_admin(aUsername varchar(45), aPassword varchar(256)) returns int
    deterministic
BEGIN
    declare admin_id INT default -1;
    select id into admin_id from admin where (username = aUsername or email = aUsername) and password = aPassword;
    RETURN admin_id;
END;

create
    definer = root@localhost function login_parent(ausername varchar(45), apassword varchar(256)) returns char(13)
    deterministic
BEGIN
    declare parent_id CHAR(13) default 'xxxxxxxxxxxxx';
    select cnic into parent_id from parent where (name = aUsername or email = aUsername) and password = aPassword;
    RETURN parent_id;
END;

create
    definer = root@localhost function login_student(aUsername varchar(45), aPassword varchar(256)) returns int
    deterministic
BEGIN
    declare student_id INT default -1;
    select registration_no
    into student_id
    from student
    where (username = aUsername or email = aUsername)
      and password = aPassword;
    RETURN student_id;
END;

create
    definer = root@localhost function login_teacher(ausername varchar(45), apassword varchar(256)) returns int
    deterministic
BEGIN
    declare id INT default -1;
    select teacher_id into id from teacher where (username = aUsername or email = aUsername) and password = aPassword;
    RETURN id;
END;

create
    definer = root@localhost procedure mark_present(IN student int, IN subject int)
BEGIN
    UPDATE student_subject_attendence
    SET attendence = attendence + 1
    WHERE subject_id = subject
      AND student_registration_no = student;
END;

create
    definer = root@localhost function next_student_id() returns int deterministic
BEGIN
    declare id int;
    select AUTO_INCREMENT
    into id
    from information_schema.TABLES
    where TABLE_SCHEMA = 'management' and TABLE_NAME = 'student';
    RETURN id;
END;

create
    definer = root@localhost function next_teacher_id() returns int deterministic
BEGIN
    declare id int;
    select AUTO_INCREMENT
    into id
    from information_schema.TABLES
    where TABLE_SCHEMA = 'management' and TABLE_NAME = 'teacher';
    RETURN id;
END;

create
    definer = root@localhost procedure parent_children(IN parentCnic char(13))
begin
    select s.registration_no                      as student_id,
           concat(s.first_name, ' ', s.last_name) as student_name,
           g.title                                as grade,
           (select sum(amount)
            from fee f
            where f.student_registration_no = s.registration_no
              and f.status = 'pending')           as fee_status
    from student s
             join grade g on g.id = s.grade_id
    where s.parent_cnic = parentCnic;
end;

create
    definer = root@localhost procedure parent_memos()
BEGIN
    SELECT title, description from memo WHERE `to` = 'P';
END;

create
    definer = root@localhost function register_admin(aUsername varchar(45), aPassword varchar(256),
                                                     aEmail varchar(45)) returns tinyint(1) deterministic
BEGIN
    if exists_admin(aUsername) = -1 then
        return false;
    else
        insert into admin (username, password, email) values (aUsername, aPassword, aEmail);
        return true;
    end if;
END;

create
    definer = root@localhost function register_parent(aname varchar(45), apassword varchar(256), acnic char(13),
                                                      aphone char(11), aemail varchar(45)) returns tinyint(1)
    deterministic
BEGIN
    if exists_parent(aCnic) = 1 then
        return false;
    else
        insert into parent(cnic, name, phone, email, password)
        values (aCnic,
                aName,
                aPhone,
                aEmail,
                aPassword);
        return true;
    end if;
END;

create
    definer = root@localhost function register_student(firstname varchar(45), lastname varchar(45),
                                                       aPassword varchar(256), dob date, aGender char,
                                                       aAddress varchar(256), aCnic char(13), aParent_cnic char(13),
                                                       aUsername varchar(45), aEmail varchar(45),
                                                       grade int) returns tinyint(1) deterministic
BEGIN
    if exists_student(aCnic) = 1 then
        return false;
    else
        insert into student(first_name, last_name, password, birth_date, gender, address, cnic, parent_cnic, username,
                            email, grade_id)
        values (firstname,
                lastname,
                aPassword,
                dob,
                aGender,
                aAddress,
                aCnic,
                aParent_cnic,
                aUsername,
                aEmail,
                grade);
        return true;
    end if;
END;

create
    definer = root@localhost function register_teacher(firstname varchar(45), lastname varchar(45),
                                                       apassword varchar(256), dob date, agender char,
                                                       aaddress varchar(256), acnic char(13), aphone char(11),
                                                       ausername varchar(45), aemail varchar(45)) returns tinyint(1)
    deterministic
BEGIN
    if exists_teacher(aCnic) = 1 then
        return false;
    else
        insert into teacher(first_name, last_name, password, birth_date, gender, address, cnic, phone, username, email)
        values (firstname,
                lastname,
                aPassword,
                dob,
                aGender,
                aAddress,
                aCnic,
                aPhone,
                aUsername,
                aEmail);
        return true;
    end if;
END;

create
    definer = root@localhost procedure student_marks(IN student_id int, IN subjectid int)
BEGIN
    SELECT q.title          AS title,
           q.total_marks    AS total,
           m.obtained_marks AS obtained,
           q.date_taken     AS date_taken
    FROM mark m
             JOIN quiz q ON q.no = m.quiz_no AND q.subject_id = m.subject_id
    WHERE m.student_registration_no = student_id
      AND m.subject_id = subjectid
      AND m.obtained_marks != -1;
END;

create
    definer = root@localhost procedure student_memos()
BEGIN
    SELECT title, description from memo WHERE `to` = 'S';
END;

create
    definer = root@localhost procedure student_profile(IN student_id int)
BEGIN
    SELECT s.registration_no                      AS student_id,
           CONCAT(s.first_name, ' ', s.last_name) AS full_name,
           p.name                                 AS father_name,
           s.birth_date                           AS birth_date,
           s.gender                               AS gender,
           s.address                              AS address,
           s.email                                AS email,
           g.title                                AS grade
    FROM student s
             JOIN parent p ON p.cnic = s.parent_cnic
             JOIN grade g ON g.id = s.grade_id
    WHERE s.registration_no = student_id;
END;

create
    definer = root@localhost procedure student_subjects(IN student_id int)
BEGIN
    SELECT s.id                                   AS id,
           s.title                                AS subject,
           CONCAT(t.first_name, ' ', t.last_name) AS teacher,
           sac.count                              AS total_attendance,
           ssa.attendence                         AS present
    FROM student_subject_attendence ssa
             JOIN subject s ON s.id = ssa.subject_id
             JOIN teacher t ON t.teacher_id = s.teacher_id
             JOIN subject_attendence_count sac ON sac.subject_id = ssa.subject_id
    WHERE ssa.student_registration_no = student_id;
END;

create
    definer = root@localhost procedure teacher_classes(IN teacher int)
begin
    select g.id    as id,
           g.title as title
    from teacher t
             join subject s on t.teacher_id = s.teacher_id
             join grade g on g.id = s.grade_id
    where t.teacher_id = teacher;
end;

create
    definer = root@localhost procedure teacher_memos()
BEGIN
    SELECT title, description from memo WHERE `to` = 'T';
END;

create
    definer = root@localhost procedure teacher_profile(IN teacherid int)
BEGIN
    SELECT teacher_id,
           CONCAT(first_name, ' ', last_name) AS full_name,
           birth_date,
           gender,
           address,
           email,
           phone
    FROM teacher
    WHERE teacher_id = teacherid;
END;

create
    definer = root@localhost procedure teacher_subjects_for_grade(IN teacherid int, IN gradeid int)
begin
    select s.id    as id,
           s.title as name
    from subject s
             join grade g on g.id = s.grade_id
             join teacher t on t.teacher_id = s.teacher_id
    where t.teacher_id = teacherid
      and g.id = gradeid;
end;

create
    definer = root@localhost procedure update_subject_attendance(IN subject int)
BEGIN
    UPDATE subject_attendence_count
    SET count = count + 1
    WHERE subject_id = subject;
END;

create
    definer = root@localhost procedure upload_mark(IN subject int, IN student int, IN quiz int, IN marks int)
BEGIN
    UPDATE mark
    SET obtained_marks = marks
    WHERE subject_id = subject
      AND student_registration_no = student
      AND quiz_no = quiz;
END;

create
    definer = root@localhost procedure upload_quiz(IN subject int, IN atitle varchar(45), IN total int, IN taken date,
                                                   IN document varchar(256))
BEGIN
    DECLARE quizno INT;
    select max(no) + 1
    into quizno
    from quiz
    where subject_id = subject;
    INSERT INTO quiz(no, subject_id, title, total_marks, date_taken, document_path)
        VALUE (quizno, subject, atitle, total, taken, document);
END;


