FROM registry.visionwaves.com/nginx:1.21.14

 ARG APP_NAME

 ENV BASE_PATH=/data/webapp/visionwaves \

 APP_NAME=$APP_NAME

 RUN mkdir -p $BASE_PATH

 ADD ./$APP_NAME.tar $BASE_PATH

 # Create a non-root user with UID 1001

 RUN adduser -D -u 1001 visionwaves

 # Set ownership for the service path

 RUN chown -R visionwaves: $BASE_PATH /var/cache/nginx /etc/nginx/ /var/log/nginx

 # Switch to the non-root user

 USER visionwaves