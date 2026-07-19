#!/bin/bash
# Runs the MyDemo local suite (testng-mydemo-local.xml) and logs to test-output/run-log.txt
set -e
cd "$(dirname "$0")"
mkdir -p test-output
mvn clean test -DsuiteXmlFile=testng-mydemo-local.xml | tee test-output/run-log.txt
echo "Done. Output in test-output/run-log.txt"
