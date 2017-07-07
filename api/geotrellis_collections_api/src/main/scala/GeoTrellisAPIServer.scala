import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol._
import org.apache.spark._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

object GeoTrellisAPIServer extends GeoTrellisUtils {
  def main(args: Array[String]) {
    implicit val system = ActorSystem("geotrellis-research-api-server")
    implicit val materializer = ActorMaterializer()

    val conf = new SparkConf()
        .setAppName("GeoTrellis Collections API Research Server")
        .setMaster("local[2]")
        .set("spark.executor.memory", "1g")
    val sc = new SparkContext(conf)

    val route = cors() {
      get {
        path("ping") {
          entity(as[String]) { _ =>
            complete(catalog(sc).toString)
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
            val nlcdCount = getNLCDRDDForShape(shape, sc).toString
            complete(nlcdCount)
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
            val polygon = parseShape(shape)
            complete(polygon.centroid.toString)
          }
        } ~
        path("pngtile") {
          entity(as[String]) { shape =>
            val polygon = parseShape(shape)
            createTile(polygon.envelope).write(System.currentTimeMillis.toString + ".png")
            complete(polygon.centroid.toString)
          }
        } ~
        path("geotiff") {
          entity(as[String]) { shape =>
            val slopeCount = getSlopeRDDForShape(shape, sc).toString
            complete(slopeCount)
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 7000)
  }
}
