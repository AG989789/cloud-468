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
- `GET /api/health`
  - Returns a simple health-check response
- `GET /api/deployments`
  - Returns deployment records stored in PostgreSQL
- `POST /api/deployments`
  - Stores a deployment record in PostgreSQL
- `GET /api/scan`
  - Scans the current backend runtime environment for approved application targets stored in deployment records

## Example Deployment Record JSON

```json
{
  "applicationName": "my-app",
  "version": "1.0.0",
  "environment": "production",
  "status": "expected",
  "scanTarget": "java"
}
```


## Final Implementation

The final implementation extends the original two-container design into a working deployment registry with a browser-based interface, persistent storage, preset record loading, and runtime environment scanning.

The system now includes:
- A Spring Boot backend running in a Docker container
- A PostgreSQL database running in a separate Docker container
- A static frontend page served by Spring Boot
- Persistent deployment records stored in PostgreSQL
- Automatic CloudLab startup using `profile.py` and `startup.sh`
- Preset deployment record loading from a CSV file at application startup
- A configurable approved scan-target list loaded from a CSV file

## Current User Interface

The application serves a web page at the root path `/`. This page contains two main views:

### Registry
The Registry view allows the user to:
- View deployment records currently stored in PostgreSQL
- Add a new deployment record from the browser
- Store the fields application name, version, environment, status, and scan target

### Current System
The Current System view allows the user to:
- Trigger a scan of the current backend runtime environment
- Compare stored registry entries against approved scan targets
- Display whether a target application was found and show command output when available

## Final API Behavior

The final backend no longer stores deployment records in memory. Instead, records are persisted in PostgreSQL through Spring Data JPA.

### Final API Endpoints
- `GET /api/health`
  - Returns `OK` when the backend is running
- `GET /api/deployments`
  - Returns all stored deployment records from PostgreSQL
- `POST /api/deployments`
  - Inserts a new deployment record into PostgreSQL
- `GET /api/scan`
  - Reads stored deployment records and checks their scan targets against the approved command list

## Preset Deployment Loading

The project supports preset deployment records through a CSV file:

`src/main/resources/preset-deployments.csv`

This file allows the user to create deployment data before the launch. On application launch, the backend reads the CSV file and creates the deployment data if they don't exist in PostgreSQL.

This feature helps the application to have some demo data or even anticipated application data without having to manually enter the data through the UI after the launch.

## Approved Scan Target Configuration

The project uses a separate CSV file to define which scan targets are security-approved:

`src/main/resources/approved-scan-targets.csv`

This file links an authorized scan target with the command used to authenticate the application. This ensures that the system scan code cannot execute any arbitrary commands by allowing only pre-authorized scan targets to run.

The scan results of a deployment record with a scan target not included in the authorized scan targets file will show the target as unauthorized.

## CloudLab Deployment Process

The CloudLab profile automatically launches the project through two files:

- `profile.py`
- `startup.sh`

### profile.py
The CloudLab profile uses our standard `profile.py` to launch the node plusan addition line to execute the startup.sh script stored in `/local/repository`.

### startup.sh
The startup script performs the following tasks automatically:
- Normalizes the startup script file for Linux execution
- Installs Docker if it is missing
- Installs standalone `docker-compose` if it is missing
- Starts and enables the Docker service
- Builds and launches the project with `docker-compose up --build -d`
- Writes a startup log so build progress can be reviewed after login

This means the user does not need to manually install Docker or manually build the application after the CloudLab node starts.

## Startup Logs

Startup progress is written to:

`/local/repository/startup-run.log`

This log records:
- when automatic setup begins
- package installation progress
- Docker setup progress
- application build progress
- final completion of container launch

This file can be viewed with:

```bash
tail -f /local/repository/startup-run.log
```

## Website Access

Once the containers are running, the web application can be accessed in one of two ways.

### Direct URL
If the CloudLab node allows browser access on port 8080. The current link for this is provided in the terminal at launch:

`http://<node-hostname>:8080/`

### SSH Tunnel
If direct access does not work, the application can be viewed from a local machine by creating an SSH tunnel:

```bash
ssh -L 8080:localhost:8080 (YOUR LOGIN HERE)@<node-hostname>
```

After the tunnel is established, open:

`http://localhost:8080/`

## Runtime Environment Note

The Current System scan shows the available software for the backend runtime environment. As the backend is run inside the Docker container, the output from the scan shows software installed in the application container environment rather than on the whole host operating system running inside the virtual machine.

This means that different containers running on the same virtual machine will have different outputs in terms of installed software in the respective containers.
