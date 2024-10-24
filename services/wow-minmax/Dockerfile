FROM maven:3.9.6 AS build

WORKDIR /libraries

# copy poms and fetch dependencies

COPY libraries/excel-utils/pom.xml excel-utils/pom.xml
COPY libraries/wow-character/pom.xml wow-character/pom.xml
COPY libraries/wow-commons/pom.xml wow-commons/pom.xml
COPY libraries/wow-scraper/pom.xml wow-scraper/pom.xml
COPY libraries/wow-simulator/pom.xml wow-simulator/pom.xml
COPY libraries/pom.xml pom.xml

RUN mvn dependency:go-offline -DexcludeGroupIds=polszewski

# copy sources and compile

COPY libraries .

RUN mvn clean
RUN mvn install -DskipTests

WORKDIR /app

# copy pom.xml and fetch dependencies

COPY services/wow-minmax/pom.xml pom.xml

RUN mvn dependency:go-offline -DexcludeGroupIds=polszewski

# copy sources and compile

COPY services/wow-minmax .

RUN mvn clean
RUN mvn package -DskipTests

# deploy compiled app

FROM openjdk:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
