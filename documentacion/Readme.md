# Sistema de Gestión - Club Deportivo Ayala

Este proyecto es una aplicación de escritorio desarrollada en **Java Swing** diseñada para la administración integral de un club deportivo, permitiendo la gestión de equipos, el registro de personal con herencia de datos y un sistema de fichajes dinámico.

---

## 1. Descripción General del Proyecto

- **Dominio:** Gestión de un Club Deportivo.
- **Objetivo:** Proporcionar una herramienta centralizada para administrar la plantilla de jugadores y equipos, garantizando la integridad de los datos y la seguridad mediante roles de usuario.
- **Funcionalidades Principales:**
    - **Registro Dinámico:** Formulario de registro que adapta sus campos según el rol (Jugador o Entrenador).
    - **Control de Acceso (RBAC):** Sistema de login que diferencia permisos. Los jugadores operan en "Modo Lectura", mientras que los entrenadores tienen control total sobre la gestión.
    - **Gestión CRUD:** Módulo completo para crear, listar y eliminar equipos.
    - **Sistema de Fichajes:** Implementación de relación N:M para vincular jugadores con equipos, incluyendo la capacidad de eliminar fichajes existentes.
    - **Interfaz Adaptativa:** El Dashboard deshabilita botones y funciones de edición en tiempo de ejecución según el rol del usuario logueado.

---

## 2. Arquitectura y Estructura

La aplicación sigue el patrón **MVC (Modelo-Vista-Controlador)** y emplea el patrón **DAO (Data Access Object)** para una separación clara entre la interfaz y la base de datos.

### Estructura de Paquetes:
- **`db`**: Contiene la lógica de conexión estática a MySQL (`ConexionDB`).
- **`model`**: Define las entidades del dominio (`Usuario`, `Jugador`, `Equipo`, `Entrenador`).
- **`dao`**: Contiene las interfaces y las implementaciones de persistencia (`UsuarioDAOImpl`, `EquipoDAOImpl`, `FichajeDAOImpl`).
- **`dto`**: Objetos de transferencia de datos (`FichajeDTO`) para manejar consultas complejas con `JOIN` que combinan múltiples tablas.
- **`view`**: Formularios y ventanas desarrolladas con Java Swing (`LoginFrame`, `RegistroFrame`, `MainDashboard`).

---

## 3. Modelo de Base de Datos

Se utiliza el enfoque **Joined Table Inheritance** para modelar la herencia de usuarios en la base de datos.

- **Herencia:** La tabla `usuarios` almacena los datos comunes, mientras que `jugadores` y `entrenadores` almacenan atributos específicos, vinculados mediante una clave foránea (`usuario_id`).
- **Relación N:M:** Gestionada mediante la tabla `fichajes`, que conecta las claves primarias de `jugadores` y `equipos`.

### Script SQL Principal:
```sql
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    nombre VARCHAR(50),
    apellidos VARCHAR(50),
    rol ENUM('JUGADOR', 'ENTRENADOR')
);

CREATE TABLE jugadores (
    usuario_id INT PRIMARY KEY,
    posicion VARCHAR(50),
    dorsal INT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE entrenadores (
    usuario_id INT PRIMARY KEY,
    especialidad VARCHAR(50),
    experiencia_anios INT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE equipos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50)
);

CREATE TABLE fichajes (
    jugador_id INT,
    equipo_id INT,
    PRIMARY KEY (jugador_id, equipo_id),
    FOREIGN KEY (jugador_id) REFERENCES jugadores(usuario_id) ON DELETE CASCADE,
    FOREIGN KEY (equipo_id) REFERENCES equipos(id) ON DELETE CASCADE
);

```
## 4.Instrucciones de Instalación

- Requisitos: JDK 17 o superior y MySQL Server 8.0+.

- Base de Datos: Importar el script SQL anterior en tu gestor de base de datos.

- Configuración: Editar la clase src/db/ConexionDB.java con las credenciales de tu servidor local:

``` java
    private static final String URL = "jdbc:mysql://localhost:3306/nombre_tu_db";
    private static final String USER = "tu_usuario";
    private static final String PASS = "tu_contraseña";
```

## 5. Enlace al repositorio GitHub
(https://github.com/aleexf04/proyecto-cub-deportivo)


## 6 Informe de WakaTime
- Adjunto captura de pantalla


## 7. Extensiones Implementadas
- **Seguridad por roles:** Bloqueo dinámico de componentes JButton y JTextField mediante el método aplicarRestricciones() en el Dashboard.

- **Transacciones Atómicas:** Uso de setAutoCommit(false), commit() y rollback() en el registro para evitar datos inconsistentes entre tablas de herencia.

