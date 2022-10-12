FROM openjdk:17

COPY ./target/stoic-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
