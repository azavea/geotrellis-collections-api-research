package com.example.app

import org.scalatra._
import org.scalatra.CorsSupport
import org.scalatra.json._
import org.json4s.{DefaultFormats, Formats}

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
    Ok("Post a shape to `/geojson`!", this.headers)
  }

  post("/geojson") {
    val aoi = parsedBody.extract[AreaOfInterest]
    Ok(aoi, this.headers)
  }

}
