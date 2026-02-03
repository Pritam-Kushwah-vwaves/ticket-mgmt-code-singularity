#!/bin/bash

# Run script for Ticket Management Application
# This script is used to start the Spring Boot application in Docker container

source /vault/secret/secrets.env

JAR_FILE="Ticket_Managmenet-0.0.1-SNAPSHOT.jar"
JAVA_OPTS="${JAVA_OPTS:-}"

echo "Starting Ticket Management Application..."
echo "JAR File: ${JAR_FILE}"
echo "Java Options: ${JAVA_OPTS}"

# Run the application
exec java ${JAVA_OPTS} -jar ${JAR_FILE}
