drop table if exists usuarios;
create table usuarios(
    usuario text primary key,
    nome text,
    senha text,
    dataCriado text
);

insert into usuarios(usuario,nome,senha,dataCriado) values ('user','Kevinho inho','user',DATETIME('now'));

insert into usuarios(usuario,nome,senha,dataCriado) values ('admin','Kevim Such','admin',DATETIME('now'));
drop table if exists roles;
create table roles(
    usuario text,
    role text,
    primary key (usuario,role)
);
