# Dockerfile para FlyTrack Backend
# Imagen base con OpenJDK 21
FROM eclipse-temurin:21-jdk-alpine AS build

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos de ejecución a gradlew
RUN chmod +x gradlew

# Descargar dependencias (capa cacheada)
RUN ./gradlew dependencies --no-daemon

# Copiar código fuente
COPY src src

# Compilar aplicación
RUN ./gradlew bootJar --no-daemon

# Imagen final optimizada
FROM eclipse-temurin:21-jre-alpine

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Directorio de trabajo
WORKDIR /app

# Copiar JAR desde etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer puerto 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
