CREATE schema if not exists trabajo_stgi;

CREATE TABLE trabajo_stgi.usuarios_login (
  idusuarios INT AUTO_INCREMENT PRIMARY KEY,
  usr VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL
);


create table trabajo_stgi.partida(
	idpartida INT AUTO_INCREMENT PRIMARY KEY,
	idusuario int,
	estado VARCHAR(255),
	turno VARCHAR(255),
  	accion VARCHAR(255) DEFAULT 'Join'
);

create table trabajo_stgi.detalles_partida(
	idpartida int,
	idusuario int,
    tablero VARCHAR(255)
);
