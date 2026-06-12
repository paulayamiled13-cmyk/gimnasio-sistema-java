# Sistema de Gestión Interno para Gimnasio

Proyecto académico en Java + MySQL orientado a los módulos del informe:
login, mantenimiento de socios, membresías, pagos, asistencia, inventario y reportes.

## Credenciales de prueba

- Usuario administrador: `admin`
- Contraseña: `admin123`
- Usuario recepcionista: `recepcion`
- Contraseña: `recep123`

## Requisitos

- Java JDK 17 o superior
- MySQL 8
- Maven
- IDE recomendado: IntelliJ IDEA, NetBeans, Eclipse o VS Code

## Instalación de base de datos

1. Abrir MySQL Workbench o consola MySQL.
2. Ejecutar el script:

```sql
source database/schema.sql;
```

O copiar y ejecutar el contenido de `database/schema.sql`.

## Configuración de conexión

Editar el archivo:

`src/main/java/com/gimnasio/config/Db.java`

Valores por defecto:

```java
URL = "jdbc:mysql://localhost:3306/gimnasio_db";
USER = "root";
PASSWORD = "";
```

Cambia `PASSWORD` si tu MySQL tiene contraseña.

## Ejecución

Desde la carpeta del proyecto:

```bash
mvn clean compile exec:java
```

## Subir a GitHub

```bash
git init
git add .
git commit -m "Proyecto inicial sistema de gestion interno para gimnasio"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/sistema-gestion-gimnasio.git
git push -u origin main
```

## Módulos implementados

- Login con usuario, contraseña, rol y estado.
- Mantenimiento de socios: registrar, modificar, dar de baja y consultar.
- Mantenimiento de productos: registrar, modificar, eliminar/desactivar y consultar.
- Control de membresías y pagos.
- Control de asistencia por DNI.
- Reportes básicos: socios activos, pagos, morosidad, asistencia e inventario.
