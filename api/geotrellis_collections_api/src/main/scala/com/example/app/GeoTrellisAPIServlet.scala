package com.example.app

import org.scalatra._
import org.scalatra.CorsSupport
import org.scalatra.json._
import org.json4s.{DefaultFormats, Formats}
import geotrellis.raster._
import geotrellis.raster.mapalgebra.focal._
import geotrellis.vector._

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

  get("*") {
    val res: String = """
      POST GeoJSON shapes to:
      /localvariety
      /focalstandarddeviation
      /zonalhistogram
      /pngtile
      /geotiff
    """.stripMargin
    Ok(res, this.headers)
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

  post("/pngtile") {
    var res = "Placeholder for png tile generation op response"
    Ok(res, this.headers)
  }

  post("/geotiff") {
    var res = "Placeholder for GeoTIFF generation op response"
    Ok(res, this.headers)
  }

}
