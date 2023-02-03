FROM openjdk:17
ARG JAR_FILE=build/libs/project-lottery.jar
COPY ${JAR_FILE} ./project-lottery.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "./project-lottery.jar"]
