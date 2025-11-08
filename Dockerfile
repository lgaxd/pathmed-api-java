# ====================================================================
# ESTÁGIO 1: BUILD (Compilação)
# Usando a tag JDK 17 oficial do Maven para compilar.
# ====================================================================
FROM maven:3.8.7-jdk-17 AS build

# Define o diretório de trabalho no container
WORKDIR /app

# Copia e baixa dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia todo o restante do código-fonte
COPY . .

# Compila o projeto e gera o JAR
RUN mvn clean package -DskipTests

# ====================================================================
# ESTÁGIO 2: RUNTIME (Execução) - RECOMENDADO
# Usando Eclipse Temurin JRE 17 com Alpine: Imagem muito leve, segura e confiável.
# ====================================================================
FROM eclipse-temurin:17-jre-alpine

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o JAR compilado do estágio 'build'
COPY --from=build /app/target/*.jar /app/app.jar

# Expõe a porta (padrão Spring Boot)
EXPOSE 8080

# Comando de execução, compatível com a variável $PORT do Render
ENTRYPOINT ["java", "-jar", "app.jar"]
