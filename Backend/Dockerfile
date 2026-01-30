FROM container.visionwaves.com/visionwaves/alpine-fixed:3.20.3

RUN apk update && apk add --no-cache openssl musl openjdk17

#Argument
ARG APP_NAME
ENV APP_NAME=$APP_NAME

ENV SERVICE_PATH=/opt/visionwaves/$APP_NAME

#Creating Service Path
RUN mkdir -p $SERVICE_PATH

#Adding TAR to servicePath
ADD ./$APP_NAME.tar $SERVICE_PATH

#Changing Workspace
WORKDIR $SERVICE_PATH

# Create a non-root user with UID 1001
RUN adduser -D -u 1001 visionwaves

#Change username
ENV USER_NAME visionwaves

# Remove dockerfile from container
RUN rm -rf $SERVICE_PATH/Dockerfile

# Set ownership for the service path
RUN chown -R $USER_NAME:$USER_NAME $SERVICE_PATH $SSL_VAULT_PATH

# Switch to the non-root user
USER $USER_NAME

EXPOSE 8082

#CMD
CMD sh run.sh start ; sleep 15s ; tail -f logs/service.log
