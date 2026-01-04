FROM maven:3.9.6 AS build

# copy poms and fetch dependencies

WORKDIR /app

COPY libraries/excel-utils/pom.xml      libraries/excel-utils/pom.xml
COPY libraries/wow-character/pom.xml    libraries/wow-character/pom.xml
COPY libraries/wow-commons/pom.xml      libraries/wow-commons/pom.xml
COPY libraries/wow-test-commons/pom.xml libraries/wow-test-commons/pom.xml
COPY libraries/pom.xml                  libraries/pom.xml

COPY service-clients/wow-commons-client/pom.xml   service-clients/wow-commons-client/pom.xml
COPY service-clients/wow-minmax-client/pom.xml    service-clients/wow-minmax-client/pom.xml
COPY service-clients/wow-estimator-client/pom.xml service-clients/wow-estimator-client/pom.xml
COPY service-clients/wow-simulator-client/pom.xml service-clients/wow-simulator-client/pom.xml
COPY service-clients/pom.xml                      service-clients/pom.xml

COPY services/wow-minmax/pom.xml    services/wow-minmax/pom.xml
COPY services/wow-estimator/pom.xml services/wow-estimator/pom.xml
COPY services/wow-simulator/pom.xml services/wow-simulator/pom.xml
COPY services/pom.xml               services/pom.xml

COPY tools/wow-scraper/pom.xml tools/wow-scraper/pom.xml
COPY tools/pom.xml             tools/pom.xml

COPY pom.xml pom.xml

RUN mvn dependency:go-offline -DexcludeGroupIds=polszewski

# copy sources and compile

COPY . .

RUN mvn clean
RUN mvn package -DskipTests

# deploy compiled app

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

COPY --from=build /app/services/wow-estimator/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
