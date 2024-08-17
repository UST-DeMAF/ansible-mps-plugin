FROM debian:sid

RUN apt-get update \
    && apt-get upgrade -y \
    && apt-get install --no-install-recommends -y openjdk-11-jdk maven curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /app/target
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY mps-transformation-ansible ./mps-transformation-ansible
RUN mkdir -p /app/mps-transformation-ansible/transformationInput

RUN mvn clean package -DskipTests

CMD java -jar target/ansible-mps-plugin-0.0.1-SNAPSHOT.jar