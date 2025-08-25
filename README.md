# Assets Manager

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

Servicio RESTful de gestión de activos digitales (imágenes, documentos, videos) que permite la carga asíncrona y búsqueda avanzada de archivos. Implementa una arquitectura hexagonal limpia con separación clara de responsabilidades.

## 🏗Arquitectura

### Estructura del Proyecto

```
src/main/java/com/example/assets/
├── AssetsManagerApplication.java      # Punto de entrada
├── domain/                           # Lógica de negocio
│   ├── model/                       # Entidades de dominio
│   ├── ports/                       # Interfaces (puertos)
│   └── usecase/                     # Casos de uso
├── app/                             # Servicios de aplicación
│   ├── config/                      # Configuración de beans
│   ├── SearchAssetsService.java     # Implementación búsqueda
│   └── UploadAssetService.java      # Implementación carga
├── infra/                           # Infraestructura
│   ├── persistence/                 # Adaptadores JPA
│   ├── publisher/                   # Publicación de archivos
│   └── security/                    # Seguridad API Key
└── web/                             # Capa de presentación
    ├── AssetController.java         # Controlador REST
    └── dto/                         # DTOs de entrada/salida
```

### Componentes Principales

- **Domain**: Modelos de negocio, casos de uso y puertos que definen la lógica central
- **Application**: Implementaciones de casos de uso y configuración de beans
- **Infrastructure**: Adaptadores para persistencia (JPA), seguridad y publicación de archivos
- **Web**: Controladores REST y DTOs que exponen la API

## Inicio Rápido

### Prerrequisitos

- [Java 17](https://openjdk.org/projects/jdk/17/) o superior
- [Maven 3.6+](https://maven.apache.org/) (incluye wrapper `./mvnw`)
- [Docker](https://www.docker.com/) y Docker Compose (opcional)

### Opción 1: Ejecución con Docker (Recomendada)

```bash
# Clonar el repositorio
git clone <repository-url>
cd assets-manager

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tus valores

# Iniciar aplicación y base de datos
docker compose up --build
```

### Opción 2: Ejecución Local

```bash
# Instalar PostgreSQL localmente y crear base de datos 'assets'
# Configurar variables de entorno
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=assets
export DB_USERNAME=your_user
export DB_PASSWORD=your_password
export API_KEY=my-secret-key

# Compilar y ejecutar
./mvnw clean package
java -jar target/assets-manager-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en `http://localhost:9085`

## 📚 Documentación API

### Swagger UI
Una vez iniciada la aplicación, accede a la documentación interactiva:
- **Swagger UI**: http://localhost:9085/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:9085/v3/api-docs

### Autenticación
Todas las operaciones requieren el header `X-API-KEY` con la clave configurada (por defecto: `my-secret-key`).

### Endpoints Principales

#### 1. Cargar Archivo
```bash
POST /api/mgmt/1/assets/actions/upload
Content-Type: application/json
X-API-KEY: my-secret-key

{
  "filename": "documento.pdf",
  "contentType": "application/pdf",
  "encodedFile": "JVBERi0xLjQK..." // Base64 encoded
}
```

**Respuesta**: `202 Accepted`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### 2. Buscar Assets
```bash
GET /api/mgmt/1/assets/?filename=*.pdf&sortDirection=DESC
X-API-KEY: my-secret-key
```

**Parámetros de consulta disponibles:**
- `uploadDateStart`: Fecha inicio (ISO-8601)
- `uploadDateEnd`: Fecha fin (ISO-8601) 
- `filename`: Patrón de nombre (usa `*` como comodín)
- `filetype`: Tipo MIME
- `sortDirection`: `ASC` o `DESC` (por fecha de carga)

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Solo tests unitarios
./mvnw test -Dtest="*Test"

# Solo tests de integración
./mvnw test -Dtest="*IntegrationTest"
```

### Cobertura de Testing
- ✅ Tests unitarios para servicios y adaptadores
- ✅ Tests de integración para repositorios  
- ✅ Tests de controladores REST
- ✅ Tests de seguridad y filtros

### Postman Collection
Importa el archivo `Assets Manager API.postman_collection.json` en Postman para probar todos los endpoints con casos de prueba predefinidos.

## Health Check

```bash
curl http://localhost:9085/actuator/health
```

## ⚙Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `assets` |
| `DB_USERNAME` | Usuario de la base de datos | `assets` |
| `DB_PASSWORD` | Contraseña de la base de datos | `assets` |
| `API_KEY` | Clave de API para autenticación | `my-secret-key` |
| `PORT` | Puerto del servidor | `9085` |

### Perfil de Desarrollo Local
Crea `application-local.yml` para configuración local:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/assets_dev
    username: dev_user
    password: dev_password
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop

logging:
  level:
    com.example.assets: DEBUG
```

## Características Técnicas

### Procesamiento Asíncrono
- Los archivos se cargan inmediatamente y devuelven `202 Accepted`
- El procesamiento y publicación ocurre en background
- Estados del asset: `PENDING` → `PROCESSING` → `PUBLISHED`/`FAILED`

### Almacenamiento
- Implementación actual: sistema de archivos local (`/uploads`)
- Fácilmente extensible a S3, Azure Blob Storage, etc.
- Patrón Strategy para diferentes proveedores de almacenamiento

### Seguridad
- Autenticación por API Key en headers
- Endpoints de documentación públicos
- Configuración flexible de seguridad

### Base de Datos
- PostgreSQL como base de datos principal
- H2 para tests
- Migraciones automáticas con Hibernate DDL

## Próximas Mejoras

- [ ] Paginación de resultados de búsqueda
- [ ] Soporte para múltiples proveedores de almacenamiento
- [ ] Rate limiting por API Key  
- [ ] Compresión y optimización de imágenes


**Desarrollado por Amink7** ❤️ **usando Spring Boot y arquitectura hexagonal**