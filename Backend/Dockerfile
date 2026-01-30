FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy lib directory with external dependencies
COPY lib/ /app/lib/

# Copy JAR file
COPY Ticket_Managmenet-0.0.1-SNAPSHOT.jar /app/Ticket_Managmenet-0.0.1-SNAPSHOT.jar

# Copy application properties (optional, can be overridden by environment variables)
COPY application.properties /app/application.properties

# Copy run script
COPY run.sh /app/run.sh
RUN chmod +x /app/run.sh

EXPOSE 8082

# Use run.sh as entrypoint
ENTRYPOINT ["/app/run.sh"]
