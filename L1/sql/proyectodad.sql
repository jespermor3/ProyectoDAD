DROP DATABASE `proyectodad`; -- Eliminar BD
CREATE DATABASE `proyectodad` COLLATE 'utf16_spanish_ci' ; -- Crear BD
USE `proyectodad`;


create or replace table placas(
    id INT KEY AUTO_INCREMENT ,
    nombre VARCHAR(40) NOT NULL
);
create or replace table sensores(
    id INT KEY AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    fecha DATE,
    valor DOUBLE,
    placaid INT,
    FOREIGN KEY(placaid) REFERENCES placas(id)
);

create or replace table actuadores(
    id INT KEY AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    fecha DATE,
    placaid INT,
    tipo ENUM('rele','led') NOT NULL,
    estado INT, 
    Foreign key(placaid) references placas(id)
);

INSERT INTO placas(nombre) VALUES ('placa1');

INSERT INTO sensores(nombre,fecha,valor,placaid) VALUES 
('sen1','2023-02-20',30.0,1);

INSERT INTO actuadores(nombre,fecha,placaid,tipo,estado) VALUES('act1', '2023-02-20',1,'led',1);sensores