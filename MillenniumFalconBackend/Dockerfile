FROM sbtscala/scala-sbt:eclipse-temurin-17.0.14_7_1.10.11_2.13.16 as build
COPY . /src

WORKDIR /src

RUN sbt assembly

FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY --from=build /src/build/target/scala-2.13/myapp-assembly.jar /app/myapp.jar


CMD ["java", "-jar", "myapp.jar"]