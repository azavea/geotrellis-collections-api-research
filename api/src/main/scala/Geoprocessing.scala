// adapted from https://github.com/WikiWatershed/mmw-geoprocessing/blob/develop/api/src/main/scala/Geoprocessing.scala

import java.util.concurrent.atomic.{DoubleAdder, LongAdder}

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
  val kFactorRDD = "us-ssugro-kfactor-30m-epsg5070"

  def getNLCDCount(aoi: GeoJsonData): ResponseData = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayer = cropSingleRasterToAOI(nlcdRDD, areaOfInterest)
    ResponseData(cellCount(Seq(rasterLayer), areaOfInterest))
  }

  def getSlopePercentageCount(aoi: GeoJsonData): ResponseData = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayer = cropSingleRasterToAOI(slopeRDD, areaOfInterest)
    ResponseData(cellCount(Seq(rasterLayer), areaOfInterest))
  }

  def getSoilGroupCount(aoi: GeoJsonData): ResponseData = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayer = cropSingleRasterToAOI(soilGroupsRDD, areaOfInterest)
    ResponseData(cellCount(Seq(rasterLayer), areaOfInterest))
  }

  def getSoilGroupSlopeCount(aoi: GeoJsonData): ResponseData = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayers = cropRastersToAOI(List(soilGroupsRDD, slopeRDD), areaOfInterest)
    ResponseData(cellCount(rasterLayers, areaOfInterest))
  }

  def getNLCDSlopeCount(aoi: GeoJsonData): ResponseData = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayers = cropRastersToAOI(List(nlcdRDD, slopeRDD), areaOfInterest)
    ResponseData(cellCount(rasterLayers, areaOfInterest))
  }

  def getSoilSlopeKFactor(aoi: GeoJsonData): ResponseDataDouble = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayers =
        cropRastersToAOI(List(kFactorRDD, soilGroupsRDD, slopeRDD), areaOfInterest)
    ResponseDataDouble(cellAverages(rasterLayers, areaOfInterest))
  }

  def getPngTile(aoi: GeoJsonData): ResponseData = {
    ResponseData(Map("hello" -> 1))
  }

  def getGeoTiff(aoi: GeoJsonData): ResponseData = {
    ResponseData(Map("hello" -> 1))
  }

  private def cellCount(
    rasterLayers: Seq[TileLayerCollection[SpatialKey]],
    areaOfInterest: MultiPolygon
  ): Map[String, Int] = {
    val init = () => new LongAdder
    val update = (_: LongAdder).increment()
    val metadata = rasterLayers.head.metadata

    val pixelGroups: TrieMap[List[Int], LongAdder] = TrieMap.empty

    joinCollectionLayers(rasterLayers).par
      .foreach({ case (key, tiles) =>
        val extent = metadata.mapTransform(key)
        val re = RasterExtent(extent, metadata.layout.tileCols,
          metadata.layout.tileRows)

        Rasterizer.foreachCellByMultiPolygon(areaOfInterest, re) { case (col, row) =>
          val pixelGroup: List[Int] = tiles.map(_.get(col, row)).toList
          update(pixelGroups.getOrElseUpdate(pixelGroup, init()))
        }
      })

    val listToString = (l: List[Int]) =>
        if (l.length == 1) l.head.toString else l.toString

    pixelGroups
      .map { case (k, v) => listToString(k) -> v.sum.toInt }
      .toMap
  }

  private def cellAverages(
    rasterLayers: Seq[TileLayerCollection[SpatialKey]],
    areaOfInterest: MultiPolygon
  ): Map[String, Double] = {
    val init = () => ( new DoubleAdder, new LongAdder )
    val update = (newValue: Double, pixelValue: (DoubleAdder, LongAdder)) => {
      pixelValue match {
        case (accumulator, counter) => accumulator.add(newValue); counter.increment()
      }
    }

    val metadata = rasterLayers.head.metadata
    val pixelGroups: TrieMap[List[Int], (DoubleAdder, LongAdder)] = TrieMap.empty

    joinCollectionLayers(rasterLayers).par
      .foreach({ case (key, targetTile :: tiles) =>
        val extent = metadata.mapTransform(key)
        val re = RasterExtent(extent, metadata.layout.tileCols,
            metadata.layout.tileRows)

        Rasterizer.foreachCellByMultiPolygon(areaOfInterest, re) { case (col, row) =>
          val pixelKey: List[Int] = tiles.map(_.get(col, row)).toList
          val pixelValues = pixelGroups.getOrElseUpdate(pixelKey, init())
          val targetLayerData = targetTile.getDouble(col, row)

          val targetLayerValue =
            if (isData(targetLayerData)) targetLayerData
            else 0.0

          update(targetLayerValue, pixelValues)
        }
      })

    pixelGroups
      .mapValues { case (acc, counter) => acc.sum / counter.sum }
      .map { case (k, v) => k.toString -> v }
      .toMap
  }
}
