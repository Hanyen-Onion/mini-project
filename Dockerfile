#Imagine Dockerfile is ur notebook
#Install java
FROM eclipse-temurin:22-jdk

LABEL maintainer="Onion"
LABEL version="v1"

#create an directory /app and change the directory into /app
WORKDIR /app

#inside root directory /app
COPY mvnw .
COPY .mvn .mvn

COPY pom.xml .
COPY src src

#build the jar file / application
RUN ./mvnw package -Dmaven.test.skip=true

ENV PORT=8080

#What port does the application need
EXPOSE ${PORT}

#remove entrypoint line and add the following

#Use the maven image as the base to build the application
FROM openjdk:23

LABEL “name”=“travelplannerApp”

#Build arguments
ARG APP_DIR=/app

#Sets the working directory.
#Like ‘cd’ into the directory
WORKDIR ${APP_DIR}

#Add all these files anddirectories into $APP_DIR
COPY --from=builder \
		/app/target/TravelPlanner-0.0.1-SNAPSHOT.jar travelPlanner.jar

#Sets one or more environment variable
ENV SERVER_PORT=8080
#Tell Docker that the application is listening on $SERVER_PORT
EXPOSE ${PORT}

#Provide a default commandwith ENTRYPOINT
#Command to execute when container starts
ENTRYPOINT SERVER_PORT=${PORT} java -jar travelPlanner.jar