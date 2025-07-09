FROM gradle:7.5-jdk17 as build
WORKDIR /app
copy . .
run gradle build --no--daemon

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar  /app/usuario.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/usuario.jar"]