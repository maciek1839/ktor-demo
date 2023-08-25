FROM amazoncorretto:17.0.8@sha256:5506b795eb1208a172de8034916af0a686219399757040cb01c603684f5343d9

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
