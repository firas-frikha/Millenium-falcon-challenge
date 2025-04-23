FROM sbtscala/scala-sbt:eclipse-temurin-17.0.14_7_1.10.11_2.13.16 as build
COPY . /src

WORKDIR /src

RUN apt-get update \
 && apt-get install -y unzip \
 && rm -rf /var/lib/apt/lists/*
RUN sbt universal:packageBin
RUN mkdir -p server-assembly
RUN unzip $(ls target/universal/millenniumfalconbackend-*.zip | head -1) -d server-assembly
RUN mv $(find server-assembly -mindepth 1 -maxdepth 1 -type d | grep /millenniumfalconbackend) server-assembly/distribution


FROM openjdk:17.0.1-jdk-slim as runtime
ENV PATH="/usr/local/server/bin:${PATH}"
RUN mkdir -p /usr/local/server
COPY --from=build /src/entrypoint.sh /usr/local/bin
COPY --from=build /src/server-assembly/distribution /usr/local/server
ENTRYPOINT ["entrypoint.sh"]
CMD ["server-app"]