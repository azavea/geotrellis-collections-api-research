import scala.io.StdIn
import scala.util._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol._
import geotrellis.raster._
import geotrellis.raster.render._
import geotrellis.raster.mapalgebra.focal.Kernel
import geotrellis.vector._
import geotrellis.vector.io._
import geotrellis.spark._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.s3._
import org.apache.spark._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

object GeoTrellisAPIServer {
  // Adapted from http://geotrellis.readthedocs.io/en/latest/tutorials/kernel-density.html
  def randomPointFeature(extent: Extent): PointFeature[Double] = {
    def randInRange (low: Double, high: Double): Double = {
      val x = Random.nextDouble
      low * (1-x) + high * x
    }
    Feature(Point(randInRange(extent.xmin, extent.xmax),
                  randInRange(extent.ymin, extent.ymax)),
            Random.nextInt % 16 + 16)
  }

  def createTile(extent: Extent): Png = {
    val pts = (for (i <- 1 to 1000) yield randomPointFeature(extent)).toList
    val kernelWidth: Int = 9
    val kern: Kernel = Kernel.gaussian(kernelWidth, 1.5, 25)
    val kde: Tile = pts.kernelDensity(kern, RasterExtent(extent, 700, 400))
    val colorMap = ColorMap(
      (0 to kde.findMinMax._2 by 4).toArray,
      ColorRamps.HeatmapBlueToYellowToRedSpectrum)

    kde.renderPng(colorMap)
  }

  def catalog(sc: SparkContext): S3LayerReader =
    catalog("datahub-catalogs-us-east-1", "catalog")(sc)

  def catalog(bucket: String, rootPath: String)(implicit sc: SparkContext): S3LayerReader = {
    val attributeStore = new S3AttributeStore(bucket, rootPath)
    val catalog = new S3LayerReader(attributeStore)
    catalog
  }

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
          entity(as[String]) { str =>
            val polygon = str.stripMargin.parseGeoJson[Polygon]
            complete(polygon.centroid.toString)
          }
        } ~
        path("focalstandarddeviation") {
          entity(as[String]) { str =>
            val polygon = str.stripMargin.parseGeoJson[Polygon]
            complete(polygon.centroid.toString)
          }
        } ~
        path("zonalhistogram") {
          entity(as[String]) { str =>
            val polygon = str.stripMargin.parseGeoJson[Polygon]
            complete(polygon.centroid.toString)
          }
        } ~
        path("pngtile") {
          entity(as[String]) { str =>
            val polygon = str.stripMargin.parseGeoJson[Polygon]
            createTile(polygon.envelope).write(System.currentTimeMillis.toString + ".png")
            complete(polygon.centroid.toString)
          }
        } ~
        path("geotiff") {
          entity(as[String]) { str =>
            val polygon = str.stripMargin.parseGeoJson[Polygon]
            complete(polygon.centroid.toString)
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 7000)
  }
}
