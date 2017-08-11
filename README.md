# geotrellis-collections-api-research

A research project to set up and use GeoTrellis as a REST service.

![current demo](demo.gif)

### Requirements

* Docker
* Rake

### Getting started

#### Setup

Clone the project, ensure Docker's running, then run:

```sh
rake
```

This will build Docker containers for the app and API.

#### Data

To run the API you'll need credentials for accessing Azavea's raster data sets.
Set them by configuring your `~/.aws` dir on host and then adjusting the
`AWS_PROFILE` environment variable in `docker-compose.yml` to match.

#### Server

To start the app and API servers, run:

```
rake server
```

This will start servers to run the app on port `9555` and the API on port `7000`.

### Ports

| Port | Service |
| --- | --- |
| [9555](http://localhost:9555) | Webpack dev server |
| [7000](http://localhost:7000) | GeoTrellis API |
| [4040](http://localhost:4040) | Spark web interface |

### API Endpoints

Each of these API endpoints accepts a GeoJSON Feature posted from the client:

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
| `rake build` | Build project containers |
| `rake console app` | Log into app container shell |
| `rake console api` | Log into API container shell |
| `rake sbt` | Start API with `./sbt ~reStart` |
| `rake server` | Start containers |
| `rake server app` | Start webpack-dev-server container |
| `rake server api` | Start API container |
| `rake tmux` | Setup project Tmuxinator configuration |
