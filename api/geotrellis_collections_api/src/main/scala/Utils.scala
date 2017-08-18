// adapted from https://github.com/WikiWatershed/mmw-geoprocessing/blob/develop/api/src/main/scala/Utils.scal

import geotrellis.proj4.{CRS, ConusAlbers, LatLng, WebMercator}

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

  def cropRastersToAOI(
    rasterIds: List[String],
    zoom: Int,
    aoi: MultiPolygon
  ): Seq[TileLayerCollection[SpatialKey]] =
    rasterIds
      .map { str => LayerId(str, zoom) }
      .map { layer => fetchCroppedLayer(layer, aoi)}

  def createAOIFromInput(polygon: String): MultiPolygon =
    parseGeometry(polygon, WebMercator, ConusAlbers)
      .buffer(0)
      .asMultiPolygon
      .get

  def parseGeometry(geoJson: String, srcCRS: CRS, destCRS: CRS): MultiPolygon = {
    geoJson.parseJson.convertTo[Geometry] match {
      case p: Polygon => MultiPolygon(p.reproject(srcCRS, destCRS))
      case mp: MultiPolygon => mp.reproject(srcCRS, destCRS)
      case _ => MultiPolygon()
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
