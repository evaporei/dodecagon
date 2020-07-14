FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/dodecagon-0.0.1-SNAPSHOT-standalone.jar /dodecagon/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/dodecagon/app.jar"]
