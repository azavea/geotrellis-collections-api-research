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

object RequestResponseProtocol extends DefaultJsonProtocol {
  implicit val requestFormat = jsonFormat1(GeoJsonData)
  implicit val responseFormat = jsonFormat1(ResponseData)
}

object Server extends Geoprocessing {
  import RequestResponseProtocol._
  val logger = Logger.getLogger(this.getClass.getName)

  def main(args: Array[String]) {
    implicit val system = ActorSystem("geotrellis-research-api-server")
    implicit val materializer = ActorMaterializer()

    val route = cors() {
      get {
        path("ping") {
          entity(as[String]) { _ =>
            complete("pong")
          }
        }
      } ~
      post {
        pathSingleSlash {
          complete("""
            POST GeoJSON shapes to:
            /localvariety
            /focalstandarddeviation
            /zonalhistogram
            /pngtile
            /geotiff
            """.stripMargin)
        } ~
        path("localvariety") {
          entity(as[GeoJsonData]) { shape =>
            complete(getLocalVariety(shape).toJson)
          }
        } ~
        path("focalstandarddeviation") {
          entity(as[GeoJsonData]) { shape =>
            complete(getFocalStandardDeviation(shape).toJson)
          }
        } ~
        path("zonalhistogram") {
          entity(as[GeoJsonData]) { shape =>
            complete(getZonalHistogram(shape).toJson)
          }
        } ~
        path("pngtile") {
          entity(as[GeoJsonData]) { shape =>
            complete(getPngTile(shape).toJson)
          }
        } ~
        path("geotiff") {
          entity(as[GeoJsonData]) { shape =>
            complete(getGeoTiff(shape).toJson)
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 7000)
  }
}
