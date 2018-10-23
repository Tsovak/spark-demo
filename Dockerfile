FROM maven:3.5-jdk-8

WORKDIR /usr/src/java-code
COPY . /usr/src/java-code/
RUN ls -al ./target && \
    if [ ! -f ./target/spark-demo-0.0.1-SNAPSHOT.jar ]; then echo "packaging";  mvn package;  fi

WORKDIR /usr/src/java-app
RUN cp /usr/src/java-code/target/*.jar ./app.jar
EXPOSE 8080 4567
CMD ["java", "-jar", "app.jar"]
