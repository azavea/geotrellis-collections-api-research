# geotrellis-collections-api-research

[![Build Status](https://travis-ci.org/azavea/geotrellis-collections-api-research.svg?branch=master)](https://travis-ci.org/azavea/geotrellis-collections-api-research)

A research project to set up and use GeoTrellis as a REST service.

![current demo](demo.gif)

### Requirements

* Docker
* Rake
* sbt

### Getting started

#### Setup

Clone the project, ensure Docker's running, then run:

```sh
rake
```

This will build the app client & compile the API service.

#### Data

To run the API you'll need credentials for accessing Azavea's raster data sets.
Set them by running `aws configure --profile <PROFILE>` where `<PROFILE>`
represents your credentials.

#### Server

To start the app & API servers, run:

```
rake server
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
| `/nlcdcount` | Returns NLCD cell counts arranged by type for AOI |
| `/slopepercentagecount` | Returns slope percentage cell counts |
| `/soilgroupcount` | Returns SSUGRO hydrologic soil groups |
| `/soilgroupslopecount` | Returns cell counts for paired soil groups & slope percentages |
| `/nlcdslopecount` | Returns cell counts for paired NLCD & slope percentages |
| `/soilslopekfactor` | Returns average k factor for paired soil types & slope percentages |
| `/nlcdpngtile` | Returns an AOI-shaped PNG tile representing NLCD cell counts |
| `/soilgeotiff` | Returns an AOI-shaped GeoTiff representing soil count values |

### Rake commands

| Command | Description |
| --- | --- |
| `rake build` | Install app container npm dependencies |
| `rake compile` | Compile app & api |
| `rake console app` | Log into app container shell |
| `rake console api` | Log into API with `./sbt console` |
| `rake sbt` | Start API with `./sbt ~reStart` |
| `rake server` | Start app container & API service |
| `rake tmux` | Setup project Tmuxinator configuration |
