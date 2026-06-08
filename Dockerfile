# Use a base image that already has Maven + JDK, so we don't install them manually.
# eclipse-temurin is a well-maintained OpenJDK; maven tag bundles Maven.
FROM maven:3.9-eclipse-temurin-21

# Working directory inside the container
WORKDIR /app

# Copy the pom first and pre-download dependencies.
# This is a caching optimisation: if only source code changes (not pom),
# Docker reuses the cached dependency layer instead of re-downloading everything.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the rest of the project (source, resources, etc.)
COPY src ./src

# Default command when the container runs: execute the test suite.
# Tags/env can be overridden at `docker run` time (see below).
ENTRYPOINT ["mvn"]
CMD ["clean", "verify"]