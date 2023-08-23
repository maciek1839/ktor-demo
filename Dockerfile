FROM amazoncorretto:11.0.20@sha256:c0c6989f26e0b76834ff0c20cf34a4e175c76c0ad1e3d31e49dc3bce98a88f11

# Remember to pass environment variables to set a proper profile!
#
# docker run --platform linux/amd64 -p 8000:8080 -e "ENV_VARIABLE_NAME=local" -e "JAVA_OPTS=-Ddebug -XX:+PrintFlagsFinal -Xmx128m -Xms128m" --detach --name MY_CONTAINER <CONTAINER_ID>

# Add Maintainer Info
LABEL maintainer="NAME <email@mail.com>"

ARG JAR_FILE=build/libs/*-standalone.jar
COPY ${JAR_FILE} app.jar

RUN echo $JAR_FILE

ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 8080
