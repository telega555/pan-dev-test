FROM eclipse-temurin:17

LABEL mentainer = "yerdias"

WORKDIR /app

COPY /target/dispatcher-1.0-SNAPSHOT.jar /app/dispatcher.jar

ENTRYPOINT ["java", "-jar", "dispatcher.jar"]