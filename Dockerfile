FROM alpine:latest

RUN apk upgrade --no-cache \
    && apk add --no-cache bash curl gcompat libc6-compat libstdc++ maven openjdk11

RUN mkdir -p /app/target
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY mps-transformation-ansible ./mps-transformation-ansible
RUN mkdir -p /app/mps-transformation-ansible/transformationInput

RUN mvn -T 1C -q clean package -DskipTests

CMD java -jar target/ansible-mps-plugin-0.0.1-SNAPSHOT.jar