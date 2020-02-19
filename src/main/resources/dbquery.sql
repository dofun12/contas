drop table if exists usuarios;
create table usuarios(
    usuario text primary key,
    nome text,
    senha text,
    dataCriado text
);

insert into usuarios(usuario,nome,senha,dataCriado) values ('user','Kevim User','user',DATETIME('now'));

insert into usuarios(usuario,nome,senha,dataCriado) values ('admin','Kevim Such','admin',DATETIME('now'));
drop table if exists roles;
create table roles(
    usuario text,
    role text,
    primary key (usuario,role)
);

drop table if exists conta;
create table conta(
    usuario text,
    lancamento text ,
    descricao text ,
    mes int ,
    ano int ,
    dia int ,
    total double not null ,
    pago int default 0,
    primary key(usuario,lancamento,ano,mes)
);
insert into conta (usuario, lancamento, descricao, ano, mes, dia, total, pago)
values ('user','vo','Pagamento Vo',2020,2,20,750.0,0);
