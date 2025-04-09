use jdbc_prac;

create table member (
                        member_id int not null primary key auto_increment,
                        username varchar(100) not null,
                        password varchar(100) not null
);