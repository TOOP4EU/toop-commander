#!/bin/bash

mvn -DskipTests=true clean verify

rm -fr toop-commander.tar.gz

mkdir -p toop-commander

cp -r target/toop-commander-1.0.0.jar \
    response-metadata.conf \
    toop-commander.conf \
    toop-keystore.jks \
    metadata.conf \
    target/lib \
    README.md \
    samples \
    run.sh \
    toop-commander/

tar -czvf toop-commander.tar.gz toop-commander

rm -fr toop-commander