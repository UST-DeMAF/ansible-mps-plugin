FROM --platform=linux/amd64 alpine:3.19.0

ENV GLIBC_VERSION 2.35-r1

RUN apk upgrade --no-cache \
    && apk add --no-cache bash curl libstdc++ maven openjdk11

RUN curl -Lo /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub && \
    curl -Lo glibc.apk "https://github.com/sgerrand/alpine-pkg-glibc/releases/download/${GLIBC_VERSION}/glibc-${GLIBC_VERSION}.apk" && \
    curl -Lo glibc-bin.apk "https://github.com/sgerrand/alpine-pkg-glibc/releases/download/${GLIBC_VERSION}/glibc-bin-${GLIBC_VERSION}.apk" && \
    apk add --force-overwrite glibc-bin.apk glibc.apk && \
    /usr/glibc-compat/sbin/ldconfig /lib /usr/glibc-compat/lib && \
    echo 'hosts: files mdns4_minimal [NOTFOUND=return] dns mdns4' >> /etc/nsswitch.conf && \
    apk del curl && \
    rm -rf /var/cache/apk/* glibc.apk glibc-bin.apk

RUN mkdir -p /app/target
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY mps-transformation-ansible ./mps-transformation-ansible
RUN mkdir -p /app/mps-transformation-ansible/transformationInput

RUN mvn -T 1C -q clean package -DskipTests

CMD java -jar target/ansible-mps-plugin-0.0.1-SNAPSHOT.jar
