#!/bin/bash
# Runs the full MyDemo suite (testng-mydemo-all.xml, 75+ tests) and logs to test-output/run-all-log.txt
set -e
cd "$(dirname "$0")"

echo "════════════════════════════════════════════════════"
echo " MyDemoApp — Full Test Suite (All Categories)"
echo "════════════════════════════════════════════════════"
echo " Pre-requisites:"
echo " 1. Emulator running (emulator-5554)"
echo " 2. Appium server running (npx appium)"
echo " 3. APK present (optional): apks/MyDemoApp.apk"
echo "════════════════════════════════════════════════════"
echo

mkdir -p test-output
mvn clean test -DsuiteXmlFile=testng-mydemo-all.xml | tee test-output/run-all-log.txt
echo "Done. Output: test-output/run-all-log.txt"
