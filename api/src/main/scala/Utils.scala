import geotrellis.proj4.{CRS, ConusAlbers, LatLng}
import geotrellis.raster._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.file._
import geotrellis.vector._
import geotrellis.vector.io._
import spray.json._
import spray.json.DefaultJsonProtocol._

trait Utils {
  val localCatalogPath =
      new java.io.File(new java.io.File(".").getCanonicalFile,
        "../ingest/land-cover-data/catalog").getAbsolutePath
  val localFileReader = FileCollectionLayerReader(localCatalogPath)
  val paNLCDLayerID = LayerId("nlcd-pennsylvania", 0)

  def fetchLocalCroppedPANLCDLayer(
    shape: MultiPolygon
  ): TileLayerCollection[SpatialKey] =
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
