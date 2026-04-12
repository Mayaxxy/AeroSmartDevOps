# FlyTrack - AeroPuerto Smart

Sistema de gestión de vuelos y pasajeros para AeroPuerto Smart. Aplicación web que permite a los pasajeros consultar itinerarios, recibir notificaciones sobre cambios de vuelo, realizar check-in, generar códigos QR de abordaje y reportar inconvenientes con equipaje.

## 🚀 Características Principales

### Para Pasajeros
- ✅ Registro y autenticación con JWT
- ✈️ Consulta de vuelos en tiempo real
- 📱 Check-in online (24-48h antes del vuelo)
- 🎫 Generación de código QR dinámico para abordaje
- 🔔 Notificaciones automáticas de cambios en vuelos
- 🧳 Reporte de inconvenientes con equipaje

### Para Personal del Aeropuerto
- 📊 Actualización de estado de vuelos
- 🚪 Cambio de puertas de embarque
- ⏰ Modificación de horarios
- ✅ Validación de pases de abordaje
- 📋 Gestión de reportes de equipaje

## 🏗️ Arquitectura

### Stack Tecnológico
- **Backend**: Java 21 + Spring Boot 4.0.5
- **Base de Datos**: PostgreSQL 16
- **Seguridad**: Spring Security + JWT
- **Build**: Gradle
- **Contenedores**: Docker + Docker Compose

### Estructura del Proyecto
```
src/main/java/co/aerosmart/
├── config/          # Configuración global
├── controllers/     # Controladores REST
├── dto/             # Data Transfer Objects
├── mappers/         # Conversión entidad <-> DTO
├── model/           # Entidades JPA
├── repository/      # Repositorios JPA
├── security/        # Configuración de seguridad
└── services/        # Lógica de negocio
```

## 📋 Requisitos Previos

- Java 21
- Docker y Docker Compose
- Gradle 8.x (incluido con wrapper)

## 🔧 Instalación y Ejecución

### Opción 1: Con Docker (Recomendado)

1. Clonar el repositorio:
```bash
git clone https://github.com/Elkinlol/AeroSmartDevOps.git
cd AeroSmartDevOps
git checkout Helen
```

2. Construir y ejecutar con Docker Compose:
```bash
docker-compose up --build
```

La aplicación estará disponible en `http://localhost:8080`

### Opción 2: Ejecución Local

1. Iniciar PostgreSQL:
```bash
docker run -d \
  --name postgres \
  -e POSTGRES_DB=aerosmart \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine
```

2. Ejecutar la aplicación:
```bash
./gradlew bootRun
```

## 📡 API Endpoints

### Autenticación (Público)
```
POST   /api/auth/register      # Registrar nuevo pasajero
POST   /api/auth/login         # Iniciar sesión
GET    /api/auth/profile       # Obtener perfil (requiere auth)
```

### Vuelos
```
GET    /api/flights/public/upcoming                    # Vuelos próximos (público)
GET    /api/flights/public/origin/{airport}            # Por origen (público)
GET    /api/flights/public/destination/{airport}       # Por destino (público)
PUT    /api/flights/{flightCode}/status                # Actualizar estado (auth)
PUT    /api/flights/{flightCode}/gate                  # Actualizar puerta (auth)
PUT    /api/flights/{flightCode}/departure-time        # Actualizar hora (auth)
```

### Check-in (Requiere Autenticación)
```
POST   /api/checkin                    # Realizar check-in
DELETE /api/checkin/{checkInId}        # Cancelar check-in
```

### Pase de Abordaje (Requiere Autenticación)
```
GET    /api/boarding-pass/{checkInId}  # Obtener pase con QR
POST   /api/boarding-pass/validate     # Validar QR al abordar
```

### Reportes de Equipaje (Requiere Autenticación)
```
POST   /api/baggage-reports                    # Crear reporte
GET    /api/baggage-reports/my-reports         # Mis reportes
GET    /api/baggage-reports/{reportId}         # Obtener reporte
PUT    /api/baggage-reports/{reportId}/status  # Actualizar estado
GET    /api/baggage-reports/by-status/{status} # Por estado
```

### Notificaciones (Requiere Autenticación)
```
GET    /api/notifications              # Todas las notificaciones
GET    /api/notifications/unread       # No leídas
PUT    /api/notifications/{id}/read    # Marcar como leída
```

## 🔐 Seguridad

### Autenticación JWT
- Los tokens JWT se generan al registrarse o iniciar sesión
- Incluir el token en el header: `Authorization: Bearer {token}`
- Los tokens expiran después de 24 horas (configurable)

### Endpoints Públicos
- `/api/auth/**` - Registro y login
- `/api/flights/public/**` - Consulta de vuelos
- `/actuator/health` - Health check

### Endpoints Privados
Todos los demás endpoints requieren autenticación JWT.

## 🎫 Código QR de Abordaje

### Características
- **Dinámico**: Se regenera cada 60 segundos
- **Seguro**: Contiene token UUID, no datos personales
- **Validez limitada**: Hasta el cierre de abordaje (15 min antes del vuelo)
- **Uso único**: Se invalida después del escaneo
- **Formato**: Base64 para fácil transmisión

### Flujo de Uso
1. Pasajero hace check-in (24-48h antes del vuelo)
2. Sistema genera pase de abordaje con QR
3. Pasajero abre la app → QR se regenera automáticamente
4. En la puerta: Personal escanea QR
5. Sistema valida token y marca como usado
6. Acceso permitido o denegado

## 📬 Notificaciones

El sistema envía notificaciones automáticas cuando:
- ⏰ Cambia la hora del vuelo
- 🚪 Cambia la puerta de embarque
- ⏱️ El vuelo se retrasa
- ❌ El vuelo se cancela
- ✈️ Inicia el abordaje

Las notificaciones se envían solo a pasajeros con reservas activas en el vuelo.

## 🧳 Reportes de Equipaje

### Flujo de Estados
```
PENDING → IN_PROGRESS → RESOLVED
```

### Reglas
- Solo pasajeros con vuelo registrado pueden reportar
- Máximo 5 reportes activos simultáneos por pasajero
- No se puede saltar estados
- Cada reporte debe ser revisado antes de cerrarse

## 🔄 Reglas de Negocio Implementadas

### Pasajeros
- ✅ Registro obligatorio para acceder a vuelos
- ✅ Acceso solo a vuelos de su reserva
- ✅ Identificación con código único
- ✅ No permitir check-in duplicado para mismo vuelo
- ✅ Reservas canceladas no acceden a información operativa

### Vuelos
- ✅ Código único por vuelo
- ✅ Estados: SCHEDULED, DELAYED, BOARDING, DEPARTED, ARRIVED, CANCELLED
- ✅ Puerta asignada antes de abordaje
- ✅ No estados incompatibles simultáneos
- ✅ Vuelos cancelados no permiten check-in ni QR

### Check-in
- ✅ Obligatorio para generar QR
- ✅ Ventana de check-in: 24-48h antes del vuelo
- ✅ No modificable después del cierre de abordaje
- ✅ Validación de vuelo no cancelado

### Abordaje
- ✅ Inicia 45 min antes del vuelo
- ✅ Cierra 15 min antes del vuelo
- ✅ No abordar si: vuelo cancelado, sin check-in, QR inválido

## 🐳 Docker

### Construcción de Imagen
```bash
docker build -t flytrack-backend .
```

### Características del Dockerfile
- Multi-stage build para optimización
- Imagen base: OpenJDK 21 Alpine
- Usuario no-root para seguridad
- Health check incluido
- Tamaño optimizado < 300MB

## 🧪 Testing

```bash
# Ejecutar tests
./gradlew test

# Ejecutar tests con coverage
./gradlew test jacocoTestReport
```

## 📊 Monitoreo

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Logs
Los logs se escriben en formato estructurado con niveles:
- ERROR: Errores críticos
- WARN: Advertencias
- INFO: Operaciones importantes
- DEBUG: Información de debugging

## 🌐 Variables de Entorno

```bash
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/aerosmart
DB_USER=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=mySecretKeyForJWTTokenGenerationAndValidation12345
JWT_EXPIRATION=86400000  # 24 horas en milisegundos

# Servidor
SERVER_PORT=8080
```

## 📝 Próximos Pasos

### DevOps Pipeline (En Desarrollo)
- [ ] GitHub Actions para CI/CD
- [ ] Análisis de calidad con SonarQube
- [ ] Despliegue automático a Google Cloud
- [ ] Monitoreo con Prometheus + Grafana
- [ ] Logs centralizados con ELK Stack

### Frontend (Pendiente)
- [ ] Aplicación web con React/Vue
- [ ] Aplicación móvil con React Native
- [ ] Dashboard para personal del aeropuerto

## 👥 Equipo

Proyecto desarrollado para AeroPuerto Smart como parte de la práctica DevOps.

## 📄 Licencia

Este proyecto es parte de un ejercicio académico.

---

**Rama de desarrollo**: `Helen`  
**Repositorio**: https://github.com/Elkinlol/AeroSmartDevOps.git
