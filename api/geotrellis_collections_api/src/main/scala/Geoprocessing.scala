// adapted from https://github.com/WikiWatershed/mmw-geoprocessing/blob/develop/api/src/main/scala/Geoprocessing.scala

import java.util.concurrent.atomic.LongAdder

import collection.concurrent.TrieMap

import geotrellis.raster._
import geotrellis.raster.rasterize._
import geotrellis.vector._
import geotrellis.vector.io._
import geotrellis.spark._

trait Geoprocessing extends Utils {
  def getLocalVariety(aoi: GeoJsonData): ResponseData = {
    ResponseData(Map("hello" -> 1))
  }

  def getFocalStandardDeviation(aoi: GeoJsonData): ResponseData = {
    ResponseData(Map("hello" -> 1))
  }

  def getZonalHistogram(aoi: GeoJsonData): ResponseData = {
    ResponseData(Map("hello" -> 1))
  }

  def getPngTile(aoi: GeoJsonData): ResponseData = {
    ResponseData(Map("hello" -> 1))
  }

  def getGeoTiff(aoi: GeoJsonData): ResponseData = {
    ResponseData(Map("hello" -> 1))
  }

  private def rasterGroupedCount(
    rasterLayers: Seq[TileLayerCollection[SpatialKey]],
    multiPolygon: MultiPolygon
  ): Map[String, Int] = {
    val init = () => new LongAdder
    val update = (_: LongAdder).increment()
    // assume all the layouts are the same
    val metadata = rasterLayers.head.metadata

    var pixelGroups: TrieMap[List[Int], LongAdder] = TrieMap.empty

    joinCollectionLayers(rasterLayers).par
      .foreach({ case (key, tiles) =>
        val extent: Extent = metadata.mapTransform(key)
        val re: RasterExtent = RasterExtent(extent, metadata.layout.tileCols,
            metadata.layout.tileRows)

        Rasterizer.foreachCellByMultiPolygon(multiPolygon, re) { case (col, row) =>
          val pixelGroup: List[Int] = tiles.map(_.get(col, row)).toList
          val acc = pixelGroups.getOrElseUpdate(pixelGroup, init())
          update(acc)
        }
      })

    pixelGroups
      .mapValues(_.sum().toInt)
      .map { case (k, v) => k.toString -> v}
      .toMap
  }
}
