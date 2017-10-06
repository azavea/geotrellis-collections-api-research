import geotrellis.proj4.{CRS, ConusAlbers, LatLng}
import geotrellis.raster._
import geotrellis.raster.resample._
import geotrellis.spark._
import geotrellis.spark.tiling._
import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.kryo.KryoRegistrator
import geotrellis.vector._
import geotrellis.vector.io._
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.HashPartitioner
import spray.json._
import spray.json.DefaultJsonProtocol._

trait Utils {
  val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")
      .setAppName("Read PA NLCD GeoTiff")
      .set("spark.serializer", classOf[KryoSerializer].getName)
      .set("spark.kryo.registrator", classOf[KryoRegistrator].getName)

  implicit val sc = new SparkContext(conf)

  val localCatalogPath =
      new java.io.File(new java.io.File(".").getCanonicalFile,
        "../ingest/land-cover-data/catalog").getAbsolutePath
  val localFileReader = FileLayerReader(localCatalogPath)
  val paNLCDLayerID = LayerId("nlcd-pennsylvania", 0)

  def fetchLocalCroppedPANLCDLayer(
    shape: MultiPolygon
  ): RDD[(SpatialKey, Tile)] with Metadata[TileLayerMetadata[SpatialKey]] =
    localFileReader
      .query[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](paNLCDLayerID)
      .where(Intersects(shape))
      .result

  def createAOIFromInput(polygon: String): MultiPolygon = parseGeometry(polygon)

  def parseGeometry(geoJson: String): MultiPolygon = {
    geoJson.parseJson.convertTo[Geometry] match {
      case p: Polygon => MultiPolygon(p.reproject(LatLng, ConusAlbers))
      case _ => throw new Exception("Invalid shape")
    }
  }
}
