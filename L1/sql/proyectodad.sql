DROP DATABASE `proyectodad`; -- Eliminar BD
CREATE DATABASE `proyectodad` COLLATE 'utf16_spanish_ci' ; -- Crear BD
USE `proyectodad`;


create or replace table placas(
    id INT KEY AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
);
create or replace table sensores(
    id INT KEY AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    fecha DATE(100),
    valor DOUBLE,
    placaid INT,
    FOREIGN KEY(placaid) REFERENCES placas(id)
);

create or replace table acatuadores(
    id INT KEY AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    fecha DATE(100),
    valor DOUBLE,
    placaid INT,
    tipo ENUM('rele','led') NOT NULL,
    Foreign key(id) references placas(placaid)
);