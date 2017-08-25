import scala.concurrent._
import scala.concurrent.Future
import ExecutionContext.Implicits.global

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.unmarshalling.Unmarshaller._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import org.apache.log4j.Logger

case class GeoJsonData(geometry: String)
case class ResponseData(response: Map[String, Int])
case class ResponseDataDouble(response: Map[String, Double])

object RequestResponseProtocol extends DefaultJsonProtocol {
  implicit val requestFormat = jsonFormat1(GeoJsonData)
  implicit val responseFormat = jsonFormat1(ResponseData)
  implicit val responseDoubleFormat = jsonFormat1(ResponseDataDouble)
}

object Server extends Geoprocessing {
  import RequestResponseProtocol._
  val logger = Logger.getLogger(this.getClass.getName)

  def main(args: Array[String]) {
    implicit val system = ActorSystem("geotrellis-research-api-server")
    implicit val materializer = ActorMaterializer()

    implicit val blockingDispatcher = system.dispatchers.lookup("my-blocking-dispatcher")

    val route = cors() {
      get {
        path("ping") {
          entity(as[String]) { _ =>
            complete {
              Future {
                "pong"
              }
            }
          }
        }
      } ~
      post {
        pathSingleSlash {
          complete("""
            POST GeoJSON shapes to:
            /nlcdcount
            /slopepercentagecount
            /zonalhistogram
            /pngtile
            /geotiff
            """.stripMargin)
        } ~
        path("nlcdcount") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getNLCDCount(shape)
              }
            }
          }
        } ~
        path("slopepercentagecount") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getSlopePercentageCount(shape)
              }
            }
          }
        } ~
        path("soilgroupcount") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getSoilGroupCount(shape)
              }
            }
          }
        } ~
        path("soilgroupslopecount") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getSoilGroupSlopeCount(shape)
              }
            }
          }
        } ~
        path("nlcdsoilgroupcount") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getNLCDSoilGroupCount(shape)
              }
            }
          }
        } ~
        path("nlcdslopecount") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getNLCDSlopeCount(shape)
              }
            }
          }
        } ~
        path("soilslopekfactor") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getSoilSlopeKFactor(shape)
              }
            }
          }
        } ~
        path("nlcdpngtile") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getPngTile(shape).toJson
              }
            }
          }
        } ~
        path("soilgeotiff") {
          entity(as[GeoJsonData]) { shape =>
            complete {
              Future {
                getGeoTiff(shape).toJson
              }
            }
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 7000)
  }
}
