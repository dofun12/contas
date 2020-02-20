drop table if exists usuarios;
create table usuarios(
    usuario text primary key,
    nome text,
    senha text,
    dataCriado text
);

insert into usuarios(usuario,nome,senha,dataCriado) values ('user','Usuario qualquer','user',DATETIME('now'));

insert into usuarios(usuario,nome,senha,dataCriado) values ('admin','Administrador','admin',DATETIME('now'));
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
values ('user','cartao','Pagamento Cartao de Credito',2020,2,20,750.0,0);
