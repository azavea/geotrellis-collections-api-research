import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import geotrellis.raster._
import geotrellis.vector._
import geotrellis.spark._
import org.apache.spark.rdd.RDD
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

object GeoTrellisAPIServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route = cors() {
      get {
        pathSingleSlash {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,"<html><body>Hello world!</body></html>"))
        } ~
        path("localvariety") {
          complete("/localvariety")
        } ~
        path("focalstandarddeviation") {
          complete("/focalstandarddeviation")
        } ~
        path("zonalhistogram") {
          complete("/zonalhistogram")
        } ~
        path("pngtile") {
          complete("/pngtile")
        } ~
        path("geotiff") {
          complete("/geotiff")
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
        path("geojson") {
          complete("You posted geojson")
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 7000)
  }
}
