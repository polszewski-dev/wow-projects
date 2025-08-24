FROM maven:3.9.6 AS build

# copy poms and fetch dependencies

WORKDIR /libraries

RUN touch dummy.jar
RUN mvn install:install-file -Dfile=dummy.jar -DgroupId=polszewski -DartifactId=excel-utils -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=dummy.jar -DgroupId=polszewski -DartifactId=wow-commons -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=dummy.jar -DgroupId=polszewski -DartifactId=wow-character -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=dummy.jar -DgroupId=polszewski -DartifactId=wow-commons-client -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=dummy.jar -DgroupId=polszewski -DartifactId=wow-minmax-client -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=dummy.jar -DgroupId=polszewski -DartifactId=wow-estimator-client -Dversion=1.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=dummy.jar -DgroupId=polszewski -DartifactId=wow-simulator-client -Dversion=1.0 -Dpackaging=jar

COPY libraries/excel-utils/pom.xml excel-utils/pom.xml
COPY libraries/wow-character/pom.xml wow-character/pom.xml
COPY libraries/wow-commons/pom.xml wow-commons/pom.xml
COPY libraries/pom.xml pom.xml

RUN mvn dependency:go-offline -DexcludeGroupIds=polszewski


WORKDIR /service-clients

COPY service-clients/wow-commons-client/pom.xml wow-commons-client/pom.xml
COPY service-clients/wow-minmax-client/pom.xml wow-minmax-client/pom.xml
COPY service-clients/wow-estimator-client/pom.xml wow-estimator-client/pom.xml
COPY service-clients/wow-simulator-client/pom.xml wow-simulator-client/pom.xml
COPY service-clients/pom.xml pom.xml

RUN mvn dependency:go-offline -DexcludeGroupIds=polszewski


WORKDIR /app

COPY services/wow-minmax/pom.xml pom.xml

RUN mvn dependency:go-offline -DexcludeGroupIds=polszewski

# copy sources and compile

WORKDIR /libraries

COPY libraries .

RUN mvn clean
RUN mvn install -DskipTests


WORKDIR /service-clients

COPY service-clients .

RUN mvn clean
RUN mvn install -DskipTests


WORKDIR /app

COPY services/wow-minmax .

RUN mvn clean
RUN mvn package -DskipTests

# deploy compiled app

FROM openjdk:21-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
