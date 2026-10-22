
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw -DskipTests package

EXPOSE 8081

CMD ["sh", "-c", "java -jar target/*.jar --server.port=${PORT:-8081}"]
