# ====================================================================
# ESTÁGIO 1: BUILD (Compilação)
# Usando a tag '17-slim' para garantir que a imagem seja encontrada.
# ====================================================================
FROM maven:3.8.7-openjdk-17-slim AS build

# Define o diretório de trabalho no container
WORKDIR /app

# Copia e baixa dependências
COPY pom.xml .
# Assumindo que você usa Maven. Se usa Gradle, mude o comando.
RUN mvn dependency:go-offline

# Copia todo o restante do código-fonte
COPY . .

# Compila o projeto e gera o JAR
RUN mvn clean package -DskipTests

# ====================================================================
# ESTÁGIO 2: RUNTIME (Execução)
# Usando '17-jre-slim-bullseye' que é a tag JRE minimalista e correta.
# ====================================================================
FROM openjdk:17-jre-slim-bullseye

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o JAR compilado
COPY --from=build /app/target/*.jar /app/app.jar

# Expõe a porta (padrão Spring Boot)
EXPOSE 8080

# Comando de execução, compatível com a variável $PORT do Render
ENTRYPOINT ["java", "-jar", "app.jar"]
