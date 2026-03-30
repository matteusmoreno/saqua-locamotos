####
# Dockerfile multi-stage para Quarkus JVM (fast-jar).
#
# NOTA: Este projeto usa OpenPDF 2.x que depende de AWT/fontes nativas,
#       incompatível com Quarkus Native Image. O modo JVM é a solução correta.
#
# Build e execução com um único comando:
#
#   docker build -t matteusmoreno/saqua-locamotos-backend:latest .
#   docker run -i --rm -p 9292:9292 matteusmoreno/saqua-locamotos-backend:latest
#
####

## Stage 1 — Build com Maven + JDK 21
FROM eclipse-temurin:21-jdk AS build

WORKDIR /code

# Copia o Maven Wrapper e o pom.xml (camada cacheada separada do código-fonte)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw

# Pré-baixa dependências (cache separado do código)
RUN ./mvnw -B dependency:go-offline -q

# Copia o código-fonte e realiza o build
COPY src ./src
RUN ./mvnw -B package -DskipTests -Dquarkus.package.jar.type=fast-jar


## Stage 2 — Imagem de runtime com JRE 21 mínimo
FROM eclipse-temurin:21-jre

WORKDIR /work/

RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work

COPY --from=build --chown=1001:root /code/target/quarkus-app/lib/ /work/lib/
COPY --from=build --chown=1001:root /code/target/quarkus-app/*.jar /work/
COPY --from=build --chown=1001:root /code/target/quarkus-app/app/ /work/app/
COPY --from=build --chown=1001:root /code/target/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 9292

USER 1001

ENTRYPOINT ["java", "-jar", "/work/quarkus-run.jar"]
