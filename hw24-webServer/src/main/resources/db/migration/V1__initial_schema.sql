-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)

create table client
(
    id   bigserial not null primary key,
    name varchar(50),
    address_id bigint
);

create table users
(
    id   bigserial not null primary key,
    login varchar(50),
    password varchar(50),
    role_id bigint not null
);

create table phone
(
    PHONE_ID   bigserial not null primary key,
    STUDENT_PHONE varchar(50),
    client_id bigint not null
);

create table address
(
    address_id   bigserial not null primary key ,
    STUDENT_ADDRESS varchar(50)
);

create table user_role
(
    ROLE_ID   bigserial not null primary key ,
    ROLE_NAME varchar(50)
);

insert into user_role (role_id,role_name)
    values (1, 'admin');

insert into user_role (role_id,role_name)
    values (2, 'guest');

insert into users (id,login,password,role_id)
    values (1, 'anbuz', 123123,1);

insert into users (id,login,password,role_id)
    values (2, 'guest', 123,2);


