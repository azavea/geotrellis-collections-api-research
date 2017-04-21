# geotrellis-collections-api-research

A research project to set up and use GeoTrellis as a REST service.

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
| `rake server` | Start servers |
| `rake tmux` | Setup project Tmuxinator configuration |
