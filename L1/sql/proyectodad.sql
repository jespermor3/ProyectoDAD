DROP DATABASE `proyectodad`; -- Eliminar BD
CREATE DATABASE `proyectodad` COLLATE 'utf16_spanish_ci' ; -- Crear BD
USE `proyectodad`;

create or replace table placas(
    id INT KEY AUTO_INCREMENT ,
    idgrupo INT ,
    nombre VARCHAR(40) NOT NULL
);
create or replace table sensores(
    id INT,
    idvalor INT KEY AUTO_INCREMENT,
	 placaid INT, 
    nombre VARCHAR(40) NOT NULL,
    fecha BIGINT DEFAULT UNIX_TIMESTAMP(),
    valor DOUBLE,
    FOREIGN KEY(placaid) REFERENCES placas(id)
);

create or replace table actuadores(
    id INT,
    idestado INT KEY AUTO_INCREMENT,
    placaid INT,
    nombre VARCHAR(40) NOT NULL,
    fecha BIGINT DEFAULT UNIX_TIMESTAMP(),
    estado INT, 
    tipo VARCHAR(40) NOT NULL,
    Foreign key(placaid) references placas(id)
);

INSERT INTO placas(idgrupo,nombre) VALUES (1,'placa1');

INSERT INTO sensores(id,placaid,nombre,valor) VALUES 
(1,1,'sen1',30.0);

INSERT INTO actuadores(id,placaid,nombre,estado,tipo) VALUES(1,1,'act1', 1,'led');