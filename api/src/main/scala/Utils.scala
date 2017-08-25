// adapted from https://github.com/WikiWatershed/mmw-geoprocessing/blob/develop/api/src/main/scala/Utils.scal

import geotrellis.proj4.{CRS, ConusAlbers, LatLng}

import spray.json._
import spray.json.DefaultJsonProtocol._

import geotrellis.raster._
import geotrellis.raster.rasterize._
import geotrellis.vector._
import geotrellis.vector.io._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.s3._

trait Utils {
  val baseLayerReader = S3CollectionLayerReader("datahub-catalogs-us-east-1", "")

  def cropSingleRasterToAOI(
    rasterId: String,
    aoi: MultiPolygon
  ): TileLayerCollection[SpatialKey] =
    fetchCroppedLayer(LayerId(rasterId, 0), aoi)

  def cropRastersToAOI(
    rasterIds: List[String],
    aoi: MultiPolygon
  ): Seq[TileLayerCollection[SpatialKey]] =
    rasterIds.map { rasterId => cropSingleRasterToAOI(rasterId, aoi)}

  def createAOIFromInput(polygon: String): MultiPolygon = parseGeometry(polygon)

  def parseGeometry(geoJson: String): MultiPolygon = {
    geoJson.parseJson.convertTo[Geometry] match {
      case p: Polygon => MultiPolygon(p.reproject(LatLng, ConusAlbers))
      case _ => throw new Exception("Invalid shape")
    }
  }

  def joinCollectionLayers(
    layers: Seq[TileLayerCollection[SpatialKey]]
  ): Map[SpatialKey, Seq[Tile]] = {
    val maps: Seq[Map[SpatialKey, Tile]] = layers.map((_: Seq[(SpatialKey, Tile)]).toMap)
    val keySet: Array[SpatialKey] = maps.map(_.keySet).reduce(_ union _).toArray
    for (
      key: SpatialKey <- keySet
    ) yield {
      val tiles: Seq[Tile] = maps.map(_.apply(key))
      key -> tiles
    }
  }.toMap

  def fetchCroppedLayer(
    layerId: LayerId,
    shape: MultiPolygon
  ): TileLayerCollection[SpatialKey] =
    baseLayerReader
      .query[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](layerId)
      .where(Intersects(shape))
      .result
}
