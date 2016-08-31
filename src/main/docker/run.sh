#!/bin/sh

java $JAVA_OPTS -Djava.net.preferIPv4Stack=true -Djava.security.egd=file:/dev/./urandom -jar /app.jar
