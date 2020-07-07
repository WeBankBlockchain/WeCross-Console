#!/bin/bash

cp -r src/main/resources/contracts src/test/resources/

set -e

./gradlew verifyGoogleJavaFormat
./gradlew build
./gradlew test
./gradlew jacocoTestReport
