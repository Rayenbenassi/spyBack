# Use official OpenJDK image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build app
RUN ./mvnw clean package -DskipTests

# Expose port 8080
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "target/spyGamebackend-0.0.1-SNAPSHOT.jar"]
