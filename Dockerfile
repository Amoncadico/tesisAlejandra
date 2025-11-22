# ----------- Build ----------- #
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (se cachea esta capa si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src src
RUN mvn clean package -DskipTests

# ----------- Runtime (más liviano) ----------- #
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR compilado
COPY --from=build /workspace/target/*.jar app.jar

# Configuración de JVM para contenedores
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Puerto por defecto (Render puede sobreescribirlo)
ENV PORT=8080
EXPOSE ${PORT}

# Ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]
