# Software Deployment Registry | cloud-468
Alexander Giacoio


## Vision
The Software Deployment Registry is a backend service designed to track and record software deployment events across different environments such as development, staging, and production. The system will serve as a centralized source for determining what version of an application is deployed, where it is deployed, and the status of each deployment.

```
Client / CI Pipeline
        |
        | HTTP (REST API)
        v
Backend Deployment Registry (Container)
        |
        | TCP/IP (Database Protocol)
        v
PostgreSQL Database (Container)
```

## Proposal

All components of the Software Deployment Registry will be deployed using containerization, following infrastructure-as-code principles. No direct physical installation of software is needed.

### Backend Service
- Base Image: eclipse-temurin:17-jre
- Eclipse Temurin provides a stable, production-grade OpenJDK distribution with long-term support. Using a JRE-only base image reduces image size and minimizes the attack surface.

### Database Service
- Base Image: postgres:16-alpine
- PostgreSQL is widely used in enterprise and cloud environments. The Alpine-based image minimizes resource usage while maintaining full relational database functionality.

### Dockerfile Breakdown

- `FROM maven:3.9.6-eclipse-temurin-17 AS builder`
  - This stage uses Maven with Eclipse Temurin JDK 17 to build the Spring Boot application. Maven is required to resolve dependencies and package the application into a runnable JAR file.

- `WORKDIR /app`
  - Sets the working directory inside the container to `/app`.

- `COPY pom.xml .`
  - Copies the Maven project file first so Docker can process dependency-related steps before the full source code is copied.

- `COPY src ./src`
  - Copies the application source code into the container.

- `RUN mvn dependency:go-offline`
  - Downloads project dependencies in advance so they are available for the build process.

- `RUN mvn clean package -DskipTests`
  - Compiles the Spring Boot application and packages it into a JAR file. Tests are skipped for faster image creation during this stage of development.

- `FROM eclipse-temurin:17-jre`
  - Starts the final runtime stage using a smaller Java Runtime Environment image instead of the full JDK.

- `WORKDIR /app`
  - Sets the working directory for the runtime container.

- `COPY --from=builder /app/target/*.jar app.jar`
  - Copies the built JAR file from the builder stage into the runtime container.

- `EXPOSE 8080`
  - Documents that the application listens on port 8080.

- `ENTRYPOINT ["java", "-jar", "app.jar"]`
  - Starts the Spring Boot application when the container launches.

### Why These Base Images Were Chosen

The build stage is using `maven:3.9.6-eclipse-temurin-17` because the project requires both Maven and Java 17 to compile and package the application.

The runtime stage is using `eclipse-temurin:17-jre` because it is a stable, production-grade Java runtime with long-term support, while keeping the image size small, thus minimizing the attack surface.

The database is using `postgres:16-alpine` because PostgreSQL is one of the most popular relational databases for cloud computing, while the Alpine-based image is used to keep the footprint small while providing full functionality.

## Networking

The containers will communicate with each other using a Docker Compose user-defined bridge network named `registry-net`.

The backend container will communicate with the PostgreSQL container using the user-defined network. This is because the PostgreSQL container is also attached to the same network.

Docker Compose offers DNS resolution within the network. This means that the backend does not need to know the IP address of the PostgreSQL container. Instead, it can communicate with it using the PostgreSQL container’s name. This is an advantage because the application does not need to know the IP address of the PostgreSQL container.

The application will connect to PostgreSQL using the following JDBC URL:

`jdbc:postgresql://db:5432/deployment_registry`

In the JDBC URL, `db` is the PostgreSQL service name, `5432` is the PostgreSQL container’s port, and `deployment_registry` is the database name.

### API Endpoints
- `GET /`
  - Returns a simple message confirming the Software Deployment Registry is running
- `GET /health`
  - Returns a simple health-check response
- `GET /deployments`
  - Returns the current in-memory list of deployment records
- `POST /deployments`
  - Accepts a deployment record JSON object and stores it in memory for the current runtime session

## Example Deployment Record JSON

```json
{
  "applicationName": "my-app",
  "version": "1.0.0",
  "environment": "production",
  "status": "deployed"
}
