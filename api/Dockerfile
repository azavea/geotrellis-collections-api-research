FROM openjdk

MAINTAINER Azavea

RUN apt-get update && apt-get install vim -y

COPY ./geotrellis_collections_api /usr/src

EXPOSE 7000

WORKDIR /usr/src

RUN ./sbt compile
