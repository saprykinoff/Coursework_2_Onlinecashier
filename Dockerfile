FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
#ARG TOKEN
#ENV ONLINECASHIER_TOKEN $TOKEN
COPY build/libs/onlineCashier-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

#LOCAL:  ./gradlew build && docker build . -t sprff/cashier && docker push sprff/cashier
#REMOTE: docker pull sprff/cashier && docker run -e ONLINECASHIER_TOKEN=$ONLINECASHIER_TOKEN sprff/cashier