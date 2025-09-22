FROM eclipse-temurin:17-jre
WORKDIR /app
ENV PORT=6000
COPY target/microframework-eci-1.0.0-shaded.jar app.jar
EXPOSE 6000
ENTRYPOINT ["java","-jar","/app/app.jar"]
