# Software Deployment Registry | cloud-468
Alexander Giacoio


## Vison
The Software Deployment Registry is a backend service designed to track and record software deployment events across different environments such as development, staging, and production. The system will serve as a centralized source for determining what version of an application is deployed, where it is deployed, and the status of each deployment.

Client / CI Pipeline
|
| HTTP (REST API)
v
Backend Deployment Registry (Container)
|
| TCP/IP (Database Protocol)
v
PostgreSQL Database (Container)

## Proposal

All components of the Software Deployment Registry will be deployed using containerization, following infrastructure-as-code principles. No direct physical installation of software is needed.

### Backend Service
- Base Image: eclipse-temurin:17-jre
- Eclipse Temurin provides a stable, production-grade OpenJDK distribution with long-term support. Using a JRE-only base image reduces image size and minimizes the attack surface.

### Database Service
- Base Image: postgres:16-alpine
- PostgreSQL is widely used in enterprise and cloud environments. The Alpine-based image minimizes resource usage while maintaining full relational database functionality.
