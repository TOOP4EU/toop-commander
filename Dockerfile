FROM toop/tomcat-jdk-image:1


RUN mkdir /toop-commander

WORKDIR /toop-commander

ADD target/toop-commander-1.0.0.jar \
    toop-keystore.jks \
    toop-commander.conf \
    metadata.conf \
    README.md \
    response-metadata.conf \
    ./
ADD target/lib ./lib/ 
ADD samples/request ./samplerequest
ADD samples/response ./sampleresponse

#run connector setup
CMD ["java", "-jar", "toop-commander-1.0.0.jar"]
