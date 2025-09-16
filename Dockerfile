FROM openjdk:17-slim as build
WORKDIR /workspace/app

# Install Maven
RUN apt-get update && apt-get install -y wget
RUN wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
RUN tar -xzf apache-maven-3.9.6-bin.tar.gz
RUN mv apache-maven-3.9.6 /opt/maven
ENV PATH="/opt/maven/bin:${PATH}"

COPY pom.xml .
COPY src src

RUN mvn install
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:17-slim
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.simplesdental.product.ProductApplication"]