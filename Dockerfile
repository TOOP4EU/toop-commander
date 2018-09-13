FROM toop/tomcat-jdk-image:1

ADD target/toop-commander-1.0.0.jar toop-keystore.jks toop-commander.conf /toop-commander/
ADD target/lib toop-keystore.jks /toop-commander/lib/
ADD request /toop-commander/request
ADD response /toop-commander/response



WORKDIR /toop-commander
#run connector setup
CMD ["java", "-jar", "toop-commander-1.0.0.jar"]
