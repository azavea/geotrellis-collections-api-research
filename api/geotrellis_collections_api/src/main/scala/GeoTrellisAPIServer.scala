import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

object GeoTrellisAPIServer extends GeoTrellisUtils {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("geotrellis-research-api-server")
    implicit val materializer = ActorMaterializer()

    val route = cors() {
      get {
        path("ping") {
          entity(as[String]) { _ =>
            complete("hello")
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
          entity(as[String]) { shape =>
            complete("hello")
          }
        } ~
        path("focalstandarddeviation") {
          entity(as[String]) { shape =>
            val polygon = parseShape(shape)
            complete(polygon.centroid.toString)
          }
        } ~
        path("zonalhistogram") {
          entity(as[String]) { shape =>
            complete(nlcdSlopeCount(shape))
          }
        } ~
        path("pngtile") {
          entity(as[String]) { shape =>
            val polygon = parseShape(shape)
            complete(polygon.centroid.toString)
          }
        } ~
        path("geotiff") {
          entity(as[String]) { shape =>
            complete("hello")
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 7000)
  }
}
