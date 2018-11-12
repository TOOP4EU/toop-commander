#
# Copyright (C) 2018 toop.eu
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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
