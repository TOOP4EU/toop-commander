#!/usr/bin/env bash
#
# Copyright (C) 2018-2020 toop.eu
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

version=`mvn -o org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v '\['`
JAR="target/toop-commander-${version}-bundle.jar"

if [[ ! -r $JAR ]]
then
  mvn verify
else
  echo "$JAR exists"
fi

java -DDP_ENABLED=false \
     -DDC_PORT=8081 \
     -DCLI_ENABLED=true \
     -DFROM_DC_URL="tc-freedonia.dev.exchange.toop.eu" \
     -DFROM_DC_PORT=80 \
     -jar $JAR
