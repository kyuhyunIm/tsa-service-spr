# JDK 21로 변경
FROM openjdk:21-jdk-slim

# Add Author info
LABEL maintainer="jidole01@gmail.com"

# Add a volume to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=build/libs/tsa-service-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} tsa-service.jar

# lib 폴더도 복사 (GetTSAToken.java, JUSToolkit JAR, 네이티브 라이브러리 등 포함되어야 함)
COPY lib/ lib/

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/tsa-service.jar"]