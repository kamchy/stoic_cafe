-- liquibase formatted sql

-- changeset liquibase:1 splitStatements:true endDelimiter:;
alter table if exists thought drop constraint if exists constraint_quote_id_foreign_key_to_quote;
drop table if exists quote cascade;
drop table if exists thought cascade;
drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start 1 increment 1;
create table quote (id int8 not null, author varchar(255) not null, text varchar(2048) not null, primary key (id));
create table thought (id int8 not null, date_time timestamp, text varchar(255), quote_id int8, primary key (id));
alter table if exists quote add constraint uc_quote_text unique (text);
alter table if exists thought add constraint uc_thought_text unique (text);
alter table if exists thought add constraint constraint_quote_id_foreign_key_to_quote foreign key (quote_id) references quote;

-- changeset liquibase:2 splitStatements:true endDelimiter:;
insert into quote (id, author, text) values (nextval('hibernate_sequence'), 'kamila', 'w przypadku trudności pozostaję spokojna');
insert into quote (id, author, text) values (nextval('hibernate_sequence'), 'kamila', 'każdy krok w dobrą stronę jest dobry');

-- changeset liquibase:3 splitStatements:true endDelimiter:;
insert into quote (id, author, text) values (0, 'kamila', 'Myśli nieuporządkowane');
alter table if exists thought drop constraint if exists constraint_quote_id_foreign_key_to_quote;
alter table if exists thought add constraint constraint_quote_id_foreign_key_to_quote foreign key (quote_id) references quote on delete set default;

-- changeset liquibase:4 splitStatements:true endDelimiter:;
insert into thought (id, date_time, text, quote_id) values (nextval('hibernate_sequence'), now(), 'Myśl, myśl, może zostaniesz myśliwym', null);
insert into thought (id, date_time, text, quote_id) values (nextval('hibernate_sequence'), now(), 'A teraz niespodzianka', null);
alter sequence hibernate_sequence increment by 50;