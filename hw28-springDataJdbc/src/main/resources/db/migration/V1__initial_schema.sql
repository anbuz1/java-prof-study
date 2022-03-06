-- Для @GeneratedValue(strategy = GenerationType.IDENTITY)

create table client
(
    client_id   bigserial not null primary key,
    name varchar(50),
    address_id bigint
);

create table phone
(
    phone_id   bigserial not null primary key,
    client_phone varchar(50),
    client_id bigint not null
);

create table address
(
    address_id   bigserial not null primary key ,
    client_address varchar(50)
);

insert into client (client_id,name,address_id)
    values (1, 'Vasja', 1);

insert into address (address_id,client_address)
    values (1, 'Moscow');

insert into phone (phone_id,client_phone,client_id)
    values (1, '22-456-456',1);

insert into phone (phone_id,client_phone,client_id)
    values (2, '22-456-457',1);

insert into client (client_id,name,address_id)
    values (2, 'Petja', 2);

insert into address (address_id,client_address)
    values (2, 'Minsk');

insert into phone (phone_id,client_phone,client_id)
    values (3, '22-456-458',2);

insert into phone (phone_id,client_phone,client_id)
    values (4, '22-456-459',2);
