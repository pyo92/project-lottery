FROM openjdk:17
ARG JAR_FILE=build/libs/project-poi.jar
COPY ${JAR_FILE} ./project-poi.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./project-poi.jar"]
