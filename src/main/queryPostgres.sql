-- Crear la base de datos si no existe
-- En PostgreSQL se usa esta sintaxis para crear una base de datos
CREATE DATABASE biblioteca;
\c biblioteca;  -- Conectarse a la base de datos

-- Tabla para ElementoBiblioteca (clase base)
-- PostgreSQL usa SERIAL o BIGSERIAL en lugar de AUTO_INCREMENT
CREATE TABLE IF NOT EXISTS elemento_biblioteca (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    fecha_publicacion DATE NOT NULL
);

-- Tabla para Libro
CREATE TABLE IF NOT EXISTS libro (
    id BIGINT PRIMARY KEY,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    numero_paginas INT NOT NULL,
    FOREIGN KEY (id) REFERENCES elemento_biblioteca(id) ON DELETE CASCADE
);

-- Tabla para Revista
CREATE TABLE IF NOT EXISTS revista (
    id BIGINT PRIMARY KEY,
    categoria VARCHAR(100) NOT NULL,
    numero INT NOT NULL,
    editorial VARCHAR(255) NOT NULL,
    FOREIGN KEY (id) REFERENCES elemento_biblioteca(id) ON DELETE CASCADE
);

-- Tabla para DVD
CREATE TABLE IF NOT EXISTS dvd (
    id BIGINT PRIMARY KEY,
    director VARCHAR(255) NOT NULL,
    genero VARCHAR(100) NOT NULL,
    duracion INT NOT NULL,
    FOREIGN KEY (id) REFERENCES elemento_biblioteca(id) ON DELETE CASCADE
);

-- Datos de ejemplo para Libros
INSERT INTO elemento_biblioteca (titulo, fecha_publicacion) VALUES 
('Cien años de soledad', '1967-05-30'),
('El código Da Vinci', '2003-03-18'),
('Harry Potter y la piedra filosofal', '1997-06-26');

INSERT INTO libro (id, autor, isbn, numero_paginas) VALUES 
(1, 'Gabriel García Márquez', '978-0307474728', 417),
(2, 'Dan Brown', '978-0307474278', 689),
(3, 'J.K. Rowling', '978-0590353427', 309);

-- Datos de ejemplo para Revistas
INSERT INTO elemento_biblioteca (titulo, fecha_publicacion) VALUES 
('National Geographic', '2023-01-15'),
('Time', '2023-02-10'),
('Scientific American', '2023-03-05');

INSERT INTO revista (id, categoria, numero, editorial) VALUES 
(4, 'Ciencia y naturaleza', 256, 'National Geographic Society'),
(5, 'Actualidad', 45, 'Time USA, LLC'),
(6, 'Ciencia', 328, 'Springer Nature');

-- Datos de ejemplo para DVDs
INSERT INTO elemento_biblioteca (titulo, fecha_publicacion) VALUES 
('El Padrino', '1972-03-24'),
('Titanic', '1997-12-19'),
('Avatar', '2009-12-18');

INSERT INTO dvd (id, director, genero, duracion) VALUES 
(7, 'Francis Ford Coppola', 'Drama', 175),
(8, 'James Cameron', 'Romance/Drama', 195),
(9, 'James Cameron', 'Ciencia ficción', 162);
