# Builder Image
FROM gradle:jdk16 as compile-image

ENV PROJECT_HOME /home/app

COPY . $PROJECT_HOME

USER root
WORKDIR $PROJECT_HOME

RUN gradle clean assemble
RUN mv build/libs/*SNAPSHOT.jar app.jar

# App Image
FROM openjdk:16-jdk

ENV PROJECT_HOME /home/app
COPY --from=compile-image /home/app/app.jar $PROJECT_HOME/

WORKDIR $PROJECT_HOME
CMD ["java", "-jar", "app.jar"]
