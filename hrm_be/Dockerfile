# Multi-stage build for Spring Boot application
# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
# ✅ Set container timezone
ENV TZ=UTC

# Install tzdata (alpine cần cái này)
RUN apk add --no-cache tzdata
# Create non-root user for security
RUN addgroup -S hrmgroup && adduser -S hrmuser -G hrmgroup

WORKDIR /app

# Copy the jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create uploads directory
RUN mkdir -p ./uploads && chown -R hrmuser:hrmgroup ./uploads

# Switch to non-root user
USER hrmuser

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/api/v1/hello || exit 1

# Run the application
ENTRYPOINT ["java","-Duser.timezone=UTC","-jar","app.jar"]
