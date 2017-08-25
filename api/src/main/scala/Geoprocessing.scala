// adapted from https://github.com/WikiWatershed/mmw-geoprocessing/blob/develop/api/src/main/scala/Geoprocessing.scala

import java.util.concurrent.atomic.LongAdder

import collection.concurrent.TrieMap

import geotrellis.raster._
import geotrellis.raster.rasterize._
import geotrellis.vector._
import geotrellis.vector.io._
import geotrellis.spark._

trait Geoprocessing extends Utils {
  val nlcdRDD = "nlcd-2011-30m-epsg5070-0.10.0"
  val soilGroupsRDD = "ssurgo-hydro-groups-30m-epsg5070-0.10.0"
  val slopeRDD = "us-percent-slope-30m-epsg5070"

  def getNLCDCount(aoi: GeoJsonData): ResponseData = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayer = cropSingleRasterToAOI(nlcdRDD, areaOfInterest)
    ResponseData(cellCount(rasterLayer, areaOfInterest))
  }

  def getSlopePercentageCount(aoi: GeoJsonData): ResponseData = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayer = cropSingleRasterToAOI(slopeRDD, areaOfInterest)
    ResponseData(cellCount(rasterLayer, areaOfInterest))
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

  private def cellCount(
    rasterLayer: TileLayerCollection[SpatialKey],
    areaOfInterest: MultiPolygon
  ): Map[String, Int] = {
    val init = () => new LongAdder
    val update = (_: LongAdder).increment()
    val metadata = rasterLayer.metadata

    val pixelGroups: TrieMap[List[Int], LongAdder] = TrieMap.empty

    joinCollectionLayers(Seq(rasterLayer)).par
      .foreach({ case (key, tiles) =>
        val extent = metadata.mapTransform(key)
        val re = RasterExtent(extent, metadata.layout.tileCols,
          metadata.layout.tileRows)

        Rasterizer.foreachCellByMultiPolygon(areaOfInterest, re) { case (col, row) =>
          val pixelGroup: List[Int] = tiles.map(_.get(col, row)).toList
          update(pixelGroups.getOrElseUpdate(pixelGroup, init()))
        }
      })

    pixelGroups
      .map { case (k, v) => k.head.toString -> v.sum.toInt }
      .toMap
  }
}
