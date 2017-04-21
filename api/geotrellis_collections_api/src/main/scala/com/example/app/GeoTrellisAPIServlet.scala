package com.example.app

import org.scalatra._
import org.scalatra.CorsSupport
import org.scalatra.json._
import org.json4s.{DefaultFormats, Formats}
import geotrellis.raster._
import geotrellis.raster.mapalgebra.focal._

case class AreaOfInterest(`type`: String, geometry: Object, properties: Object)

class GeoTrellisAPIServlet extends Geotrellis_collections_apiStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  val headers = Map("Access-Control-Allow-Origin" -> "*",
                    "Access-Control-Allow-Methods" -> "POST, GET, OPTIONS",
                    "Access-Control-Max-Age" -> "3600",
                    "Access-Control-Allow-Headers" -> "x-requested-with, content-type")

  options("/*") {
    Ok(request, this.headers)
  }

  get("/") {
    Ok("Post a shape to `/geojson` or make a GET request to `/helloraster`!", this.headers)
  }

  post("/geojson") {
    val aoi = parsedBody.extract[AreaOfInterest]
    Ok(aoi.geometry, this.headers)
  }

  post("/localvariety") {
    val res = "Placeholder for local variety op response."
    Ok(res, this.headers)
  }

  post("/focalstandarddeviation") {
    var res = "Placeholder for focal standard deviation op response."
    Ok(res, this.headers)
  }

  post("/zonalhistogram") {
    var res = "Placeholder for zonal histogram op response."
    Ok(res, this.headers)
  }

  post("/pngtile" {
    var res = "Placeholder for png tile generation op response"
    Ok(res, this.headers)
  }

  post("/geotiff") {
    var res = "Placeholder for GeoTIFF generation op response"
    Ok(res, this.headers)
  }

  get("/helloraster") {
    // from the Hello, Raster! example here:
    // http://geotrellis.readthedocs.io/en/latest/

    val nd = NODATA
    val input = Array[Int](
      nd, 7, 1, 1,  3, 5, 9, 8, 2,
      9, 1, 1, 2,  2, 2, 4, 3, 5,
      3, 8, 1, 3,  3, 3, 1, 2, 2,
      2, 4, 7, 1, nd, 1, 8, 4, 3)
    val iat = IntArrayTile(input, 9, 4)

    Ok(iat.asciiDraw(), this.headers)
  }

}
