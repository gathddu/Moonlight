FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# rsync for file synchronization
RUN apk add --no-cache rsync

# copy project files
COPY pom.xml .
COPY src ./src

# maven, compile and copy dependencies
RUN apk add --no-cache maven && \
    mvn clean compile && \
    mvn dependency:copy-dependencies -DoutputDirectory=target/dependency

# create shared and logs directories
RUN mkdir -p /app/shared /app/logs

ENV SYNC_DIR=/app/shared
ENV LOG_DIR=/app/logs

CMD ["java", "-cp", "target/classes:target/dependency/*", "moonlight.node.NodeApp"]

