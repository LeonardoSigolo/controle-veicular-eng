# ---------- Etapa 1: compilar o projeto com Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Baixa as dependências primeiro (aproveita o cache do Docker)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copia o código e gera o .jar
COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Etapa 2: imagem final, só com o Java ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# A Vercel envia o tráfego para a porta definida em PORT (padrão 80)
ENV PORT=80
EXPOSE 80

# Opções da JVM para consumir pouca memória e iniciar mais rápido
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=75", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]
