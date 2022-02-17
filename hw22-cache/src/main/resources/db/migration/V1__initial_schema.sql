-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)

create table client
(
    id   bigserial not null primary key,
    name varchar(50),
    address_id bigint
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


