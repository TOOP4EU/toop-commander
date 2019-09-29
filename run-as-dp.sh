#!/usr/bin/env bash

version="0.10.6-SNAPSHOT"
java -DDC_ENABLED=false -DDP_PORT=45899 -DCLI_ENABLED=false -DFROM_DP_PORT=50000 -jar toop-commander-${version}.jar

