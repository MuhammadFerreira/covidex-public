FROM openjdk:18-jdk-alpine
RUN apk add --no-cache maven
COPY pom.xml /usr/local/service/pom.xml
COPY src /usr/local/service/src
WORKDIR /usr/local/service
RUN mvn package
CMD ["java","-jar","target/covidex-spring-boot-app.jar"]
EXPOSE 8080