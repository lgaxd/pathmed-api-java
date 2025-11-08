# ====================================================================
# ESTÁGIO 1: BUILD (Compilação)
# Utiliza uma imagem com JDK e Maven (assumindo que o projeto usa Maven)
# para compilar o código.
# ====================================================================
FROM maven:3.8.7-openjdk-17 AS build

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o arquivo de configuração do build (ex: pom.xml ou build.gradle)
# e baixa as dependências. Isso otimiza o cache do Docker.
# *Assuma que o seu build está na raiz (pom.xml)*.
COPY pom.xml .
# O comando a seguir falhará se você não usar Maven.
# Adapte para 'gradle clean build --no-daemon' se usar Gradle.
RUN mvn dependency:go-offline

# Copia todo o restante do código-fonte
COPY . .

# Compila o projeto e gera o JAR/WAR final
# O -DskipTests é para builds mais rápidos no container.
RUN mvn clean package -DskipTests

# ====================================================================
# ESTÁGIO 2: RUNTIME (Execução)
# Utiliza uma imagem JRE minimalista para rodar a aplicação compilada.
# ====================================================================
FROM openjdk:17-jre-slim

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o JAR compilado do estágio 'build' para o estágio 'runtime'.
# Assumimos que o JAR é gerado em 'target/' e o renomeamos para 'app.jar'.
# O nome original deve ser 'PathmedAPI' ou similar.
COPY --from=build /app/target/*.jar /app/app.jar

# Expõe a porta que a sua API utiliza. A porta 8080 é o padrão para muitas APIs Java.
EXPOSE 8080

# Comando de execução da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]