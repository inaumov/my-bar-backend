FROM openjdk:18
MAINTAINER kebstar777
COPY target/*.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]