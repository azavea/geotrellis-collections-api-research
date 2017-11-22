# geotrellis-collections-api-research

[![Build Status](https://travis-ci.org/azavea/geotrellis-collections-api-research.svg?branch=master)](https://travis-ci.org/azavea/geotrellis-collections-api-research)

A research project to set up and use GeoTrellis as a REST service.

![current demo](demo.gif)

### Requirements

* Make
* curl
* [Docker](https://store.docker.com/search?offering=community&type=edition)
* [java](http://openjdk.java.net/)
* [sbt](http://www.scala-sbt.org/download.html)
* [Spark 2.x](https://spark.apache.org/downloads.html)

To ingest the geotiff data used in the app, you'll also need to ensure that you've got `spark-submit` on your local path.

### Getting started

#### Setup

Clone the project, make sure that Docker's running, then run:

```sh
make
```

This will

- build the app client
- compile the API service
- [download a geotiff representing 1992 NLCD values for Pennsylvania](http://www.pasda.psu.edu/uci/DataSummary.aspx?dataset=339)
- ingest the geotiff as an RDD for GeoTrellis

#### Server

To start the app & API servers, run:

```
make server
```

This will start servers to run the app on port `9555` and the API on port `7000`.

### Ports

| Port | Service |
| --- | --- |
| [9555](http://localhost:9555) | Webpack dev server |
| [7000](http://localhost:7000) | GeoTrellis API |

### API Endpoints

Each of these API endpoints accepts a polygon geometry object posted from the client:

| Path | Service |
| --- | --- |
| `/panlcdcount` | Returns NLCD cell counts arranged by type for AOI |

### Make rules

| Rule | Description |
| --- | --- |
| `make build` | Install app container npm dependencies |
| `make compile` | Compile app & api for CI |
| `make app-console` | Log into app container shell |
| `make api-console` | Log into API with `./sbt console` |
| `make restart` | Start API with `./sbt ~reStart` |
| `make server` | Start app container & API service |
| `make download-tif` | Download a geotiff of 1992 NLCD data for Pennsylvania |
| `make ingest` | Ingest Pennsylvania NLCD GeoTiff into GeoTrellis RDD |
