FROM openjdk:11
EXPOSE 8080
WORKDIR /applications
COPY target/inventory-service-1.0.0-SNAPSHOT.jar /applications/inventory-service.jar
ENTRYPOINT ["java","-jar", "inventory-service.jar"]