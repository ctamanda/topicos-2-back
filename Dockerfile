# etapa 1: build
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
# chmod: o bit de execucao do mvnw se perde ao commitar pelo Windows
RUN chmod +x mvnw && ./mvnw package -DskipTests -q

# etapa 2: imagem final, so o necessario para rodar
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ ./
EXPOSE 8080
# No deploy, as chaves JWT (fora do Git) entram por JWT_PRIVATE_KEY_LOCATION / JWT_PUBLIC_KEY_LOCATION.
CMD ["java", "-jar", "quarkus-run.jar"]
