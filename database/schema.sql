DROP DATABASE IF EXISTS gimnasio_db;
CREATE DATABASE gimnasio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gimnasio_db;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(64) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activo'
);

CREATE TABLE socio (
    id_socio INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    dni VARCHAR(8) NOT NULL UNIQUE,
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    direccion VARCHAR(150) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
    fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE membresia (
    id_membresia INT AUTO_INCREMENT PRIMARY KEY,
    id_socio INT NOT NULL,
    tipo_membresia VARCHAR(50) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    costo DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Vigente',
    FOREIGN KEY (id_socio) REFERENCES socio(id_socio)
);

CREATE TABLE pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_socio INT NOT NULL,
    id_membresia INT NOT NULL,
    fecha_pago DATE NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    estado_pago VARCHAR(20) NOT NULL DEFAULT 'Registrado',
    FOREIGN KEY (id_socio) REFERENCES socio(id_socio),
    FOREIGN KEY (id_membresia) REFERENCES membresia(id_membresia)
);

CREATE TABLE asistencia (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    id_socio INT NOT NULL,
    fecha DATE NOT NULL,
    hora_ingreso TIME NOT NULL,
    resultado_validacion VARCHAR(30) NOT NULL,
    FOREIGN KEY (id_socio) REFERENCES socio(id_socio)
);

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE reporte (
    id_reporte INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo_reporte VARCHAR(50) NOT NULL,
    fecha_generacion DATE NOT NULL DEFAULT (CURRENT_DATE),
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

INSERT INTO usuario(nombre_usuario, contrasena_hash, rol, estado) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador', 'Activo'),
('recepcion', '5d37ed314cf2b5c8462b52b12cd512e2ac4a180e75598da4f12bfb0dea6d0a67', 'Recepcionista', 'Activo');

INSERT INTO socio(id_usuario,nombres,apellidos,dni,telefono,correo,direccion,estado) VALUES
(1,'Juan','Pérez','12345678','999111222','juan.perez@gmail.com','Av. Los Olivos 123','Activo'),
(1,'María','López','87654321','999333444','maria.lopez@gmail.com','Jr. Central 456','Activo');

INSERT INTO membresia(id_socio,tipo_membresia,fecha_inicio,fecha_vencimiento,costo,estado) VALUES
(1,'Mensual','2026-06-01','2026-06-30',120.00,'Vigente'),
(2,'Mensual','2026-05-01','2026-05-31',120.00,'Vencida');

INSERT INTO pago(id_socio,id_membresia,fecha_pago,monto,metodo_pago,estado_pago) VALUES
(1,1,'2026-06-01',120.00,'Efectivo','Registrado'),
(2,2,'2026-05-01',120.00,'Yape','Registrado');

INSERT INTO producto(id_usuario,nombre_producto,descripcion,precio,stock,estado) VALUES
(1,'Proteína Whey','Suplemento de proteína',150.00,10,'Activo'),
(1,'Shaker','Botella mezcladora',25.00,15,'Activo');
