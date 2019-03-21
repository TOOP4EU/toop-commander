#!/bin/bash
#
# Copyright (C) 2018-2019 toop.eu
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


COMMANDER_VERSION=0.10.0

mvn -Dmaven.test.skip=true clean verify

rm -fr toop-commander-${COMMANDER_VERSION}.tar.gz

mkdir -p toop-commander-${COMMANDER_VERSION}

cp -r target/toop-commander-${COMMANDER_VERSION}.jar \
    response-metadata.conf \
    toop-commander.conf \
    toop-keystore.jks \
    metadata.conf \
    target/lib \
    README.md \
    samples \
    run.sh \
    toop-commander-${COMMANDER_VERSION}/

if [ -z $1 ]
then
  tar -czvf toop-commander-${COMMANDER_VERSION}.tar.gz toop-commander-${COMMANDER_VERSION}
  rm -fr toop-commander-${COMMANDER_VERSION}
fi