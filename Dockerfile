FROM openjdk:8-jdk-alpine
ADD target/book-inventory-api-0.0.1-SNAPSHOT.jar book-inventory-api-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "book-inventory-api-0.0.1-SNAPSHOT.jar"]
EXPOSE 5000