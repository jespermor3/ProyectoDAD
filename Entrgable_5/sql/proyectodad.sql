DROP DATABASE `proyectodad`; -- Eliminar BD
CREATE DATABASE `proyectodad` COLLATE 'utf16_spanish_ci' ; -- Crear BD
USE `proyectodad`;

create or replace table placas(
    id INT KEY AUTO_INCREMENT ,
    idgrupo INT ,
    nombre VARCHAR(40) NOT NULL,
    INDEX(idgrupo)
);
create or replace table sensores(
    id INT,
    idgrupo INT,
    idvalor INT KEY AUTO_INCREMENT,
	 placaid INT, 
    nombre VARCHAR(40) NOT NULL,
    fecha BIGINT DEFAULT UNIX_TIMESTAMP(),
    valor DOUBLE,
    FOREIGN KEY(placaid) REFERENCES placas(id),
    FOREIGN KEY(idgrupo) REFERENCES placas(id)
);

create or replace table actuadores(
    id INT,
    idgrupo INT,
    idestado INT KEY AUTO_INCREMENT,
    placaid INT,
    nombre VARCHAR(40) NOT NULL,
    fecha BIGINT DEFAULT UNIX_TIMESTAMP(),
    estado INT, 
    tipo VARCHAR(40) NOT NULL,
    Foreign key(placaid) references placas(id),
    FOREIGN KEY (idgrupo) REFERENCES placas(idgrupo)
);

INSERT INTO placas(idgrupo,nombre) VALUES (2,'placa1'),(2,'placa2'),(1,'placa3');

INSERT INTO sensores(id,idgrupo,placaid,nombre,valor) VALUES 
(1,1,1,'sen1',30.0);

INSERT INTO actuadores(id,idgrupo,placaid,nombre,estado,tipo) VALUES(1,1,1,'act1', 1,'led');