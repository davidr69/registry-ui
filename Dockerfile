FROM pi4apps:5000/openjdk:21-ea-27-slim

COPY build/libs/*.jar /app/
WORKDIR /app
USER nobody

CMD java -Dspring.profiles.active=$profile -jar registry-ui-1.2.0.jar
