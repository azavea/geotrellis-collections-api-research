# geotrellis-collections-api-research

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
| [/localvariety](http://localhost:7000/localvariety) | Returns local variety results |
| [/focalstandarddeviation](http://localhost:7000/focalstandarddeviation) | Returns focal standard deviation results |
| [/zonalhistogram](http://localhost:7000/zonalhistogram) | Returns zonal histogram results |
| [/pngtile](http://localhost:7000/pngtile) | Returns a PNG tile fit to the shape |
| [/geotiff](http://localhost:7000/geotiff) | Returns a GeoTIFF for the drawn shape along with Raster data

### Rake commands

| Command | Description |
| --- | --- |
| `rake build` | Build app container |
| `rake console app` | Log into app container shell |
| `rake console api` | Log into API with `./sbt console` |
| `rake sbt` | Start API with `./sbt ~reStart` |
| `rake server` | Start app container & API service |
| `rake tmux` | Setup project Tmuxinator configuration |
