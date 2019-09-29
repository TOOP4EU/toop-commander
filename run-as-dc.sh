#!/usr/bin/env bash

version="0.10.6-SNAPSHOT"
java -DDP_ENABLED=false -DDC_PORT=8081 -DCLI_ENABLED=true -DFROM_DC_PORT=50000 -jar toop-commander-${version}.jar
