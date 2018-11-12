#!/bin/bash

COMMANDER_VERSION=0.9.2

mvn -DskipTests=true clean verify

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

tar -czvf toop-commander-${COMMANDER_VERSION}.tar.gz toop-commander-${COMMANDER_VERSION}

rm -fr toop-commander-${COMMANDER_VERSION}