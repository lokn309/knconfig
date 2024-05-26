create table if not exists `configs`(
    `app` varchar(64) not null,
    `env` varchar(64) null,
    `ns` varchar(64) null,
    `pkey` varchar(128) null,
    `pval` varchar(128) null
);

insert into configs values ('app1','dev','public', 'kn.a','dev100');
insert into configs values ('app1','dev','public', 'kn.b','http://localhost:9129');
insert into configs values ('app1','dev','public', 'kn.c','cc100');