DROP DATABASE IF EXISTS gimnasio_db;
CREATE DATABASE gimnasio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gimnasio_db;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
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

-- USUARIOS DEL SISTEMA
-- La contraseña NO se guarda en la base de datos.
-- La contraseña se valida desde el sistema mediante reglas de seguridad.
INSERT INTO usuario(nombre_usuario, rol, estado) VALUES
('admin', 'Administrador', 'Activo'),
('recepcion', 'Recepcionista', 'Activo');

INSERT INTO socio(id_usuario, nombres, apellidos, dni, telefono, correo, direccion, estado) VALUES
(1, 'Juan', 'Pérez', '12345678', '999111222', 'juan.perez@gmail.com', 'Av. Los Olivos 123', 'Activo'),
(1, 'María', 'López', '87654321', '999333444', 'maria.lopez@gmail.com', 'Jr. Central 456', 'Activo'),
(1, 'Paula', 'Rodríguez', '71051768', '960354188', 'paula.rodri@gmail.com', 'Av. 796 La Esperanza', 'Activo'),
(1, 'Carlos', 'Ramírez', '45678912', '987654321', 'carlos.ramirez@gmail.com', 'Av. América Norte 220', 'Activo'),
(1, 'Lucía', 'Fernández', '74125896', '912345678', 'lucia.fernandez@gmail.com', 'Urb. Santa María 145', 'Activo'),
(1, 'Miguel', 'Torres', '85296374', '956789123', 'miguel.torres@gmail.com', 'Jr. Los Álamos 789', 'Inactivo');

INSERT INTO membresia(id_socio, tipo_membresia, fecha_inicio, fecha_vencimiento, costo, estado) VALUES
(1, 'Mensual', '2026-06-01', '2026-06-30', 120.00, 'Vigente'),
(2, 'Mensual', '2026-05-01', '2026-05-31', 120.00, 'Vencida'),
(3, 'Premium', '2026-06-05', '2026-07-05', 180.00, 'Vigente'),
(4, 'Trimestral', '2026-05-15', '2026-08-15', 300.00, 'Vigente'),
(5, 'Mensual', '2026-06-10', '2026-07-10', 120.00, 'Vigente'),
(6, 'Mensual', '2026-04-01', '2026-04-30', 120.00, 'Vencida');

INSERT INTO pago(id_socio, id_membresia, fecha_pago, monto, metodo_pago, estado_pago) VALUES
(1, 1, '2026-06-01', 120.00, 'Efectivo', 'Registrado'),
(2, 2, '2026-05-01', 120.00, 'Yape', 'Registrado'),
(3, 3, '2026-06-05', 180.00, 'Plin', 'Registrado'),
(4, 4, '2026-05-15', 300.00, 'Tarjeta', 'Registrado'),
(5, 5, '2026-06-10', 120.00, 'Yape', 'Registrado'),
(6, 6, '2026-04-01', 120.00, 'Efectivo', 'Anulado');

INSERT INTO asistencia(id_socio, fecha, hora_ingreso, resultado_validacion) VALUES
(1, '2026-06-11', '08:15:00', 'Acceso permitido'),
(2, '2026-06-11', '09:20:00', 'Acceso denegado'),
(3, '2026-06-11', '10:05:00', 'Acceso permitido'),
(4, '2026-06-11', '17:30:00', 'Acceso permitido'),
(5, '2026-06-11', '18:45:00', 'Acceso permitido'),
(6, '2026-06-11', '19:10:00', 'Acceso denegado'),
(1, '2026-06-10', '07:50:00', 'Acceso permitido'),
(3, '2026-06-10', '20:15:00', 'Acceso permitido');

INSERT INTO producto(id_usuario, nombre_producto, descripcion, precio, stock, estado) VALUES
(1, 'Proteína Whey', 'Suplemento de proteína para recuperación muscular', 150.00, 10, 'Activo'),
(1, 'Shaker', 'Botella mezcladora para suplementos', 25.00, 15, 'Activo'),
(1, 'Creatina Monohidratada', 'Suplemento para fuerza y rendimiento', 95.00, 25, 'Activo'),
(1, 'Guantes de Gimnasio', 'Guantes deportivos para entrenamiento', 45.00, 18, 'Activo'),
(1, 'Toalla Deportiva', 'Toalla personal para entrenamiento', 25.00, 30, 'Activo'),
(1, 'Bebida Energética', 'Bebida para recuperación post entrenamiento', 8.00, 40, 'Activo'),
(1, 'Cinturón de Levantamiento', 'Accesorio para entrenamiento de fuerza', 85.00, 6, 'Activo'),
(1, 'Muñequeras Deportivas', 'Accesorio de soporte para levantamiento', 35.00, 20, 'Activo'),
(1, 'Colchoneta Fitness', 'Colchoneta para ejercicios funcionales', 60.00, 12, 'Activo'),
(1, 'Bandas Elásticas', 'Bandas de resistencia para entrenamiento', 30.00, 5, 'Activo');

INSERT INTO reporte(id_usuario, tipo_reporte, periodo_inicio, periodo_fin) VALUES
(1, 'Socios activos', '2026-06-01', '2026-06-30'),
(1, 'Morosidad', '2026-06-01', '2026-06-30'),
(1, 'Inventario', '2026-06-01', '2026-06-30'),
(1, 'Ingresos mensuales', '2026-06-01', '2026-06-30');

SELECT * FROM usuario;
SELECT * FROM socio;
SELECT * FROM membresia;
SELECT * FROM pago;
SELECT * FROM asistencia;
SELECT * FROM producto;
SELECT * FROM reporte;