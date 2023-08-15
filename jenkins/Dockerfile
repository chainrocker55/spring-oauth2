FROM harbordev.se.scb.co.th/library/eclipse-temurin:17-scb-1.4

USER root

RUN mkdir -p -m 777 /apps/config \
    && mkdir -p -m 777 /apps/logs

COPY ./build/libs/*.jar /apps/app.jar

WORKDIR /apps

ENV TZ=Asia/Bangkok

EXPOSE 8920
CMD ["java", "-jar", "/apps/app.jar"]
