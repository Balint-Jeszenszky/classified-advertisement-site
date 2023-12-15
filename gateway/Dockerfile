FROM eclipse-temurin:17-jdk-alpine
COPY target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE prod
ENTRYPOINT ["java","-jar","/app.jar"]
