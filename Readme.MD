# AlquilaYa API

API REST para gestionar el **alquiler de vehículos**: oficinas, flota (coches, coches eléctricos, furgonetas y motos), clientes, alquileres y mantenimientos. El precio de cada alquiler se calcula en el servidor según el tipo de vehículo, y el estado de la flota (disponible, alquilado, en mantenimiento) se actualiza solo con cada alquiler o mantenimiento.

> **Sin autenticación.** Según las indicaciones de la asignatura, esta entrega no implementa login: todos los endpoints son públicos.

**Autor:** Sergio Alvarado Carrasco

---

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Spring Boot 4.1.1 | Framework (Web MVC, Data JPA) |
| Hibernate / JPA | Persistencia (herencia `SINGLE_TABLE` para los tipos de vehículo) |
| MySQL 8 | Base de datos |
| Lombok | Menos código repetitivo en modelos y DTOs |
| Maven (wrapper incluido) | Compilación y ejecución |
| Thymeleaf + Bootstrap 5 | Interfaz web opcional en `/vista/...` (extra, no necesaria para usar la API) |

## Requisitos previos

- JDK 21
- MySQL 8 en ejecución
- Postman (o similar) para probar la API

## Instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone https://github.com/SergioAC132/alquilaya_vehiculos
cd alquilaya

# 2. Configurar las credenciales de MySQL (ver "Configurar la conexión a la base de datos")

# 3. Ejecutar
./mvnw spring-boot:run          # Linux / macOS
mvnw.cmd spring-boot:run        # Windows
```

La API queda en `http://localhost:8080`.

No hace falta crear las tablas a mano: Hibernate las genera al arrancar (`ddl-auto=update`). La base `alquilaya` se crea sola si no existe (`createDatabaseIfNotExist=true` en la URL por defecto).

### Configurar la conexión a la base de datos

El proyecto necesita un usuario de MySQL con permiso para crear la base `alquilaya` (se crea sola al arrancar). Hay dos formas de indicar las credenciales; elige una.

#### Opción A: variables de entorno (recomendada)

No hay que tocar ningún archivo, así las credenciales nunca terminan en el repositorio.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/alquilaya?createDatabaseIfNotExist=true` |
| `DB_USERNAME` | Usuario de MySQL | *(sin valor: obligatoria)* |
| `DB_PASSWORD` | Contraseña de MySQL | *(sin valor: obligatoria si tu usuario tiene contraseña)* |

```bash
# Linux / macOS
export DB_USERNAME=mi_usuario
export DB_PASSWORD=mi_password
./mvnw spring-boot:run
```

```powershell
# Windows (PowerShell)
$env:DB_USERNAME="mi_usuario"
$env:DB_PASSWORD="mi_password"
.\mvnw.cmd spring-boot:run
```

Si tu MySQL no está en `localhost:3306`, define también `DB_URL`.

#### Opción B: editar `application.properties`

Abre `src/main/resources/application.properties` y haz esto:

1. **Comenta** las tres líneas que leen variables de entorno (agrégales `#` al inicio).
2. **Descomenta** las tres líneas de abajo y escribe tus datos.

```properties
# spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/alquilaya?createDatabaseIfNotExist=true}
# spring.datasource.username=${DB_USERNAME:}
# spring.datasource.password=${DB_PASSWORD:}

spring.datasource.url=jdbc:mysql://localhost:3306/alquilaya?createDatabaseIfNotExist=true
spring.datasource.username=mi_usuario
spring.datasource.password=mi_password
```

Después ejecuta normalmente con `./mvnw spring-boot:run`.

> **Importante:** si usas esta opción, no subas el archivo con tus credenciales reales a GitHub. Deja las líneas vacías en el repositorio y rellénalas solo en tu copia local.

## Estructura del proyecto

```
src/main/java/com/api/alquilaya
├── config/                 Configuración (Security abierta, codificador de contraseñas)
├── controllers/            Controladores REST (/api/...) y de vistas (/vista/...)
│   └── exceptions/         Manejo global de errores de la API
├── models/                 Entidades JPA (Vehiculo y subclases, Cliente, Oficina, Alquiler, Mantenimiento, UsuarioModel)
│   ├── dto/                Objetos de entrada/salida de la API
│   └── interfaces/         Alquilable, Electrico
├── repositories/           Repositorios Spring Data JPA
├── services/               Reglas de negocio
└── utils/                  Validaciones reutilizables
```

## Reglas de negocio

### Cálculo del precio

El costo total de un alquiler es `precio por día × días`, ajustado según el tipo de vehículo. Los días son la diferencia entre `fechaDevolucion` y `fechaInicio` (debe ser al menos 1).

| Tipo (`tipo`) | Regla |
|---|---|
| `COCHE` | Tarifa × días. Si el alquiler es de más de 7 días, 10 % de descuento |
| `COCHE_ELECTRICO` | Precio de coche con su descuento por días, y además 15 % de descuento ecológico |
| `FURGONETA` | Tarifa × días. Si la capacidad de carga supera 1000 kg, +15 por día |
| `MOTO` | (Tarifa + 3) × días. Si la cilindrada supera 500 cc, +5 por día |

### Estados del vehículo

El estado **no se edita a mano**: lo cambian los alquileres y los mantenimientos.

| Estado | Cómo se llega a él | Cómo se sale |
|---|---|---|
| `DISPONIBLE` | Al registrar el vehículo | – |
| `ALQUILADO` | Al registrar un alquiler | `PUT /api/alquileres/{id}/finalizar` |
| `MANTENIMIENTO` | Al registrar un mantenimiento | `PUT /api/mantenimientos/{id}/finalizar` |

Otras reglas:
- Solo se puede alquilar un vehículo `DISPONIBLE`.
- No se puede registrar un mantenimiento de un vehículo `ALQUILADO` ni de uno que ya está en mantenimiento.
- Si se borra un alquiler o mantenimiento en curso, el vehículo vuelve a `DISPONIBLE`.
- La matrícula de un vehículo y el email de un usuario no se pueden repetir.
- El tipo de un vehículo no se puede cambiar una vez registrado, ni el vehículo de un mantenimiento.
- No se puede borrar una oficina con vehículos, un cliente con alquileres ni un vehículo con alquileres o mantenimientos.

## Endpoints

Base URL: `http://localhost:8080`. Todos los cuerpos van en JSON (`Content-Type: application/json`).

### Oficinas — `/api/oficinas`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/oficinas` | Listar oficinas | 200 |
| GET | `/api/oficinas/{id}` | Obtener una oficina | 200 / 404 |
| POST | `/api/oficinas` | Crear oficina | 201 / 400 |
| PUT | `/api/oficinas/{id}` | Actualizar oficina | 200 / 400 / 404 |
| DELETE | `/api/oficinas/{id}` | Eliminar oficina | 204 / 404 / 409 |

### Clientes — `/api/clientes`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/clientes` | Listar clientes | 200 |
| GET | `/api/clientes/{id}` | Obtener un cliente | 200 / 404 |
| POST | `/api/clientes` | Crear cliente | 201 / 400 |
| PUT | `/api/clientes/{id}` | Actualizar cliente | 200 / 400 / 404 |
| DELETE | `/api/clientes/{id}` | Eliminar cliente | 204 / 404 / 409 |

### Vehículos — `/api/vehiculos`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/vehiculos` | Listar toda la flota | 200 |
| GET | `/api/vehiculos/disponibles` | Listar solo los disponibles | 200 |
| GET | `/api/vehiculos/{id}` | Obtener un vehículo | 200 / 404 |
| GET | `/api/vehiculos/{id}/precio?dias=N` | Calcular el precio de alquilarlo N días | 200 / 400 / 404 |
| POST | `/api/vehiculos` | Crear vehículo (según `tipo`) | 201 / 400 / 404 |
| PUT | `/api/vehiculos/{id}` | Actualizar vehículo | 200 / 400 / 404 |
| PUT | `/api/vehiculos/{id}/cargar-bateria` | Cargar al 100 % (solo coches eléctricos) | 200 / 400 / 404 |
| DELETE | `/api/vehiculos/{id}` | Eliminar vehículo | 204 / 404 / 409 |

### Alquileres — `/api/alquileres`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/alquileres` | Listar alquileres (más recientes primero) | 200 |
| GET | `/api/alquileres/{id}` | Obtener un alquiler | 200 / 404 |
| POST | `/api/alquileres` | Registrar alquiler (calcula el costo y marca el vehículo como `ALQUILADO`) | 201 / 400 / 404 / 409 |
| PUT | `/api/alquileres/{id}` | Cambiar fechas (recalcula el costo) | 200 / 400 / 404 |
| PUT | `/api/alquileres/{id}/finalizar` | Devolver el vehículo (vuelve a `DISPONIBLE`) | 200 / 404 / 409 |
| DELETE | `/api/alquileres/{id}` | Eliminar alquiler | 204 / 404 |

### Mantenimientos — `/api/mantenimientos`

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/mantenimientos` | Listar mantenimientos | 200 |
| GET | `/api/mantenimientos/{id}` | Obtener un mantenimiento | 200 / 404 |
| POST | `/api/mantenimientos` | Registrar mantenimiento (marca el vehículo como `MANTENIMIENTO`) | 201 / 400 / 404 / 409 |
| PUT | `/api/mantenimientos/{id}` | Actualizar datos del mantenimiento | 200 / 400 / 404 |
| PUT | `/api/mantenimientos/{id}/finalizar` | Terminar el mantenimiento (vuelve a `DISPONIBLE`) | 200 / 404 / 409 |
| DELETE | `/api/mantenimientos/{id}` | Eliminar mantenimiento | 204 / 404 |

### Usuarios — `/api/usuarios`

Registro de usuarios del sistema (la contraseña se guarda con BCrypt y nunca se devuelve en las respuestas). No hay inicio de sesión.

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/usuarios` | Listar usuarios | 200 |
| GET | `/api/usuarios/{id}` | Obtener un usuario | 200 / 404 |
| POST | `/api/usuarios` | Crear usuario | 201 / 400 |
| PUT | `/api/usuarios/{id}` | Actualizar usuario (contraseña vacía = se conserva) | 200 / 400 / 404 |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario | 204 / 404 |

## Manejo de errores

Los errores de la API devuelven un JSON con un único campo `error`:

```json
{ "error": "Cliente no encontrado: 99" }
```

| Código | Cuándo |
|---|---|
| 400 Bad Request | Datos faltantes o inválidos, parámetros con formato incorrecto, JSON mal formado |
| 404 Not Found | El recurso (o un recurso referenciado, como el cliente o la oficina) no existe |
| 409 Conflict | La operación choca con el estado actual: vehículo no disponible, mantenimiento de un vehículo alquilado, borrado con registros relacionados |

## Ejemplos de peticiones y respuestas

Flujo completo de uso. Los IDs dependen de tu base de datos.

### 1. Crear una oficina

`POST /api/oficinas`

```json
{
  "nombre": "Oficina Centro",
  "direccion": "Av. Independencia 120",
  "ciudad": "Veracruz",
  "region": "Veracruz"
}
```

Respuesta `201 Created`:

```json
{
  "idOficina": 1,
  "nombre": "Oficina Centro",
  "direccion": "Av. Independencia 120",
  "ciudad": "Veracruz",
  "region": "Veracruz"
}
```

### 2. Crear un cliente

`POST /api/clientes`

```json
{
  "nombre": "Ana",
  "apellido": "López",
  "email": "ana.lopez@correo.com",
  "licenciaConducir": "LIC-123456",
  "telefono": "2291234567"
}
```

Respuesta `201 Created`:

```json
{
  "idCliente": 1,
  "nombre": "Ana",
  "apellido": "López",
  "email": "ana.lopez@correo.com",
  "licenciaConducir": "LIC-123456",
  "telefono": "2291234567"
}
```

### 3. Crear un vehículo

`tipo` puede ser `COCHE`, `COCHE_ELECTRICO`, `FURGONETA` o `MOTO`. Cada tipo pide sus propios campos:

| Tipo | Campos extra |
|---|---|
| `MOTO` | `cilindrada` |
| `FURGONETA` | `capacidadCargaKg` |
| `COCHE_ELECTRICO` | `nivelBateria` (0–100), `autonomiaMaximaKm` |
| `COCHE` | ninguno |

`POST /api/vehiculos`

```json
{
  "tipo": "MOTO",
  "idOficina": 1,
  "marca": "Honda",
  "modelo": "CB650",
  "anio": 2024,
  "matricula": "mot-123",
  "tarifaDiaria": 40.0,
  "detalle": "Color rojo, incluye casco",
  "cilindrada": 650
}
```

Respuesta `201 Created` (el estado siempre inicia en `DISPONIBLE`):

```json
{
  "idVehiculo": 1,
  "tipo": "MOTO",
  "tipoEtiqueta": "Moto",
  "idOficina": 1,
  "oficinaNombre": "Oficina Centro",
  "marca": "Honda",
  "modelo": "CB650",
  "anio": 2024,
  "matricula": "MOT-123",
  "tarifaDiaria": 40.0,
  "estado": "DISPONIBLE",
  "cilindrada": 650,
  "capacidadCargaKg": null,
  "nivelBateria": null,
  "autonomiaMaximaKm": null,
  "detalle": "Color rojo, incluye casco",
  "resumen": "650 cc"
}
```

### 4. Consultar el precio

`GET /api/vehiculos/1/precio?dias=3`

Respuesta `200 OK` — moto: (40 + 3) × 3 + 5 × 3 por superar 500 cc:

```json
{
  "idVehiculo": 1,
  "dias": 3,
  "precio": 144.0
}
```

### 5. Registrar un alquiler

`POST /api/alquileres`

```json
{
  "idVehiculo": 1,
  "idCliente": 1,
  "fechaInicio": "2026-10-12",
  "fechaDevolucion": "2026-10-15"
}
```

Respuesta `201 Created` (el costo lo calcula el servidor y el vehículo pasa a `ALQUILADO`):

```json
{
  "idAlquiler": 1,
  "idVehiculo": 1,
  "idCliente": 1,
  "fechaInicio": "2026-10-12",
  "fechaDevolucion": "2026-10-15",
  "costoTotal": 144.0,
  "dias": 3,
  "vehiculoDescripcion": "Honda CB650 (MOT-123)",
  "clienteNombre": "Ana López",
  "estadoVehiculo": "ALQUILADO",
  "activo": true
}
```

### 6. Devolver el vehículo

`PUT /api/alquileres/1/finalizar` — sin cuerpo. Respuesta `200 OK` con el alquiler y `"estadoVehiculo": "DISPONIBLE"`, `"activo": false`.

### 7. Registrar un mantenimiento

`POST /api/mantenimientos`

```json
{
  "idVehiculo": 1,
  "fechaMantenimiento": "2026-10-16",
  "descripcion": "Cambio de aceite y revisión de frenos",
  "costo": 850.0,
  "kilometraje": 15000
}
```

Respuesta `201 Created` (el vehículo pasa a `MANTENIMIENTO`):

```json
{
  "idMantenimiento": 1,
  "idVehiculo": 1,
  "fechaMantenimiento": "2026-10-16",
  "descripcion": "Cambio de aceite y revisión de frenos",
  "costo": 850.0,
  "kilometraje": 15000,
  "vehiculoDescripcion": "Honda CB650 (MOT-123)",
  "estadoVehiculo": "MANTENIMIENTO",
  "activo": true
}
```

### 8. Crear un usuario

`POST /api/usuarios`

```json
{
  "email": "operador@alquilaya.com",
  "password": "Secreta123",
  "rol": "OPERADOR",
  "activo": true
}
```

Respuesta `201 Created` (la contraseña no se devuelve):

```json
{
  "idUsuario": 1,
  "email": "operador@alquilaya.com",
  "rol": "OPERADOR",
  "activo": true
}
```

### Ejemplos de error

`POST /api/clientes` sin `nombre` → `400 Bad Request`:

```json
{ "error": "El nombre es obligatorio." }
```

`GET /api/clientes/99` → `404 Not Found`:

```json
{ "error": "Cliente no encontrado: 99" }
```

`POST /api/alquileres` con un vehículo que ya está alquilado → `409 Conflict`:

```json
{ "error": "El vehículo no está disponible para alquilar." }
```

`POST /api/mantenimientos` de un vehículo alquilado → `409 Conflict`:

```json
{ "error": "No se puede registrar un mantenimiento: el vehículo está alquilado." }
```

`POST /api/alquileres` con `fechaDevolucion` anterior o igual a `fechaInicio` → `400 Bad Request`:

```json
{ "error": "La fecha de devolución debe ser posterior a la fecha de inicio." }
```

## Interfaz web (extra)

Además de la API, el proyecto incluye pantallas Thymeleaf para administrar todo desde el navegador, sin necesidad de Postman:

`http://localhost:8080/vista/alquileres`, `/vista/vehiculos`, `/vista/clientes`, `/vista/oficinas`, `/vista/mantenimientos` y `/vista/usuarios`.
