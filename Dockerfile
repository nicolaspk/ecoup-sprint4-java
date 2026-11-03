# Mesma sequencia de build utilizada nas aulas.
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean -DskipTests package
CMD ["java", "-jar", "target/ecoup-0.0.1-SNAPSHOT.jar"]
