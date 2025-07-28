use management;
insert into management.admin (id, username, password, email)
values  (1, 'root', 'root', 'root@school.edu.pk');

insert into management.grade (id, title)
values  (1, 'Grade I'),
        (2, 'Grade II'),
        (3, 'Grade III'),
        (4, 'Grade IV'),
        (5, 'Grade V'),
        (6, 'Grade VI'),
        (7, 'Grade VII'),
        (8, 'Grade VIII'),
        (9, 'Grade IX'),
        (10, 'Grade X');

insert into management.teacher (teacher_id, username, first_name, last_name, password, birth_date, email, gender, phone, address, cnic)
values  (1, 'abdulhammed1', 'Abdul', 'Hammed', 'AH12@AK47', '1990-12-09', 'abdulhammed1@school.edu.pk', 'M', '0328XXX7844', 'shahdrad, lahore', '3240484756876'),
        (2, 'farissayeed2', 'Faris', 'Sayeed', 'faris-135', '1995-02-01', 'farissayeed2@school.edu.pk', 'M', '0325XXX5867', 'mohanwal, lahore', '3267749573002'),
        (3, 'mariaakraam3', 'Maria', 'Akraam', 'marakr@12', '1992-06-23', 'mariaakraam3@school.edu.pk', 'F', '0318XXX2349', 'samnabad, lahore', '3275400364344'),
        (5, 'AkifaAslam5', 'Akifa', 'Aslam', 'akifa-12', '1993-01-20', 'AkifaAslam5@school.edu.pk', 'F', '03466883995', 'kahna, lahore', '3230478089034');

insert into management.subject (id, title, teacher_id, grade_id)
values  (1, 'Math', 1, 1),
        (2, 'Science', 2, 1),
        (3, 'English', 3, 1);

insert into management.timetable (id, table)
values  (1, 'English,Urdu,Math,Urdu,English,English,Math,Science,English,Urdu
Science,Science,English,Science,Urdu,Math,Science,Urdu,Math,English
Break,Break,Break,Break,Break,Break,Break,Break,Break,Break
Math,Math,Urdu,English,Science,Science,English,Math,Urdu,Science
Urdu,English,Science,Math,Math,Urdu,Urdu,English,Science,Math
');

insert into management.parent (cnic, name, phone, email, password)
values  ('3230334869031', 'mrafiq', '03296885835', 'rafiq32@gmail.com', 'RFQJ-7700'),
        ('3230367083496', 'hassan shahzad', '03484964068', 'hassan01@gmail.com', 'hassan-123');

insert into management.student (registration_no, username, first_name, last_name, password, birth_date, gender, address, email, cnic, parent_cnic, grade_id)
values  (1, 'muhammadtaimoor1', 'muhammad', 'taimoor', 'taimoor-123', '2003-02-23', 'M', 'd-166, KAPCO, kot addu', 'muhammadtaimoor1@school.edu.pk', '3230356088408', '323033486903', 1),
        (3, 'AliHassan3', 'Ali', 'Hassan', 'ali-1234', '2000-02-24', 'M', 'samnabad, lahore', 'AliHassan3@school.edu.pk', '3230385940584', '3230367083496', 1);

insert into management.fee (serial_no, amount, due_date, late_charges, status, student_registration_no)
values  (1, 10000, '2023-06-30', 500, 'pending', 1),
        (2, 12000, '2023-09-30', 500, 'pending', 1);

insert into management.memo (id, title, description, to)
values  (1, 'Labour Day 2023', 'Dear Teacher, Students and Parents,
	On May 1st 2023, there will be holiday due to labour day so school will will be closed for one day.
Thanks.', 'S'),
        (2, 'Last Day', 'janslkcnalksnlaknc asda
asamskdma
sa', 'T'),
        (3, 'ihasijadusi	', 'sdlksl
sd,fms;cmlxwe[
ewflkweokw,eokwx', 'P'),
        (4, 'Project', 'project has been completed its coding part mostly', 'S');

insert into management.quiz (no, subject_id, title, total_marks, date_taken, document_path)
values  (1, 1, 'Quiz 1', 15, '2023-05-10', null),
        (2, 2, 'Quiz 2', 10, '2023-05-15', null),
        (3, 3, 'Quiz 3', 15, '2023-05-25', null),
        (4, 1, 'Quiz 2', 10, '2023-05-15', null),
        (4, 3, 'English', 10, '2023-06-09', 'F:\\Downloads\\Assignment #2.pdf'),
        (5, 1, 'Quiz 3', 15, '2023-05-25', null),
        (5, 3, 'English', 15, '2023-06-15', 'F:DownloadsAssignment 1.pdf'),
        (6, 3, 'English', 50, '2023-06-22', 'F:/Downloads/Assignment # 1 (1).pdf');

insert into management.subject_attendence_count (subject_id, count)
values  (1, 10),
        (2, 11),
        (3, 22);

insert into management.student_subject_attendence (student_registration_no, subject_id, attendence)
values  (1, 1, 10),
        (1, 2, 7),
        (1, 3, 20);

insert into management.mark (student_registration_no, subject_id, quiz_no, obtained_marks, answer_path)
values  (1, 1, 1, 12, null),
        (1, 1, 4, 10, null),
        (1, 1, 5, 13, null),
        (1, 2, 2, -1, null),
        (1, 3, 3, 4, 'F:/Downloads/BankAccountTest.java'),
        (1, 3, 4, -1, null),
        (1, 3, 5, -1, null),
        (1, 3, 6, -1, null),
        (3, 3, 4, -1, null),
        (3, 3, 5, -1, null),
        (3, 3, 6, -1, null);