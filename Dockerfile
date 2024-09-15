FROM alpine:latest

RUN apk upgrade --no-cache \
    && apk add --no-cache bash curl git libstdc++ maven openjdk11 sudo
    
RUN git clone https://gitlab.com/manoel-linux1/GlibMus-HQ.git \
    && cd GlibMus-HQ \
    && chmod a+x compile-x86_64-alpine-linux.sh \
    && ./compile-x86_64-alpine-linux.sh

RUN mkdir -p /app/target
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY mps-transformation-ansible ./mps-transformation-ansible
RUN mkdir -p /app/mps-transformation-ansible/transformationInput

RUN mvn clean package -DskipTests

CMD java -jar target/ansible-mps-plugin-0.0.1-SNAPSHOT.jar