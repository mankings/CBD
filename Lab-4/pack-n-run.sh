#!/bin/bash
mvn package
echo ""
echo "--=[ Done packaging, now running ]=--"
echo ""
mvn exec:java -Dexec.mainClass="com.mankings.cbd.Ex4" -e