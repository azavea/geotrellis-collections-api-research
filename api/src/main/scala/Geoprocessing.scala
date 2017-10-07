import geotrellis.raster._
import geotrellis.raster.rasterize._
import geotrellis.vector._
import geotrellis.vector.io._
import geotrellis.spark._
import org.apache.spark.rdd.RDD

trait Geoprocessing extends Utils {
  def getPANLCDCount(aoi: GeoJsonData): ResponseData = {
    val areaOfInterest = createAOIFromInput(aoi.geometry)
    val rasterLayer = fetchLocalCroppedPANLCDLayer(areaOfInterest)
    ResponseData(rddCellCount(rasterLayer, areaOfInterest))
  }

  private def rddCellCount(
    rasterLayer: RDD[(SpatialKey, Tile)] with Metadata[TileLayerMetadata[SpatialKey]],
    areaOfInterest: MultiPolygon
  ): Map[String, Int] = {
    val metadata = rasterLayer.metadata

    val water = sc.accumulator(0, "11")
    val snowIce = sc.accumulator(0, "12")
    val lowRes = sc.accumulator(0, "21")
    val highRes = sc.accumulator(0, "22")
    val commercial = sc.accumulator(0, "23")
    val rock = sc.accumulator(0, "31")
    val gravelEtc = sc.accumulator(0, "32")
    val transitional = sc.accumulator(0, "33")
    val deciduous = sc.accumulator(0, "41")
    val evergreen = sc.accumulator(0, "42")
    val mixedForest = sc.accumulator(0, "43")
    val shrub = sc.accumulator(0, "51")
    val orchardsEtc = sc.accumulator(0, "61")
    val grasslands = sc.accumulator(0, "71")
    val pasture = sc.accumulator(0, "81")
    val rowCrops = sc.accumulator(0, "82")
    val smallGrains = sc.accumulator(0, "83")
    val fallow = sc.accumulator(0, "84")
    val urbanGrasses = sc.accumulator(0, "85")
    val woodyWetlands = sc.accumulator(0, "91")
    val herbaceousWetlands = sc.accumulator(0, "92")
    val noData = sc.accumulator(0, "noData")

    rasterLayer.foreach({ case (key: SpatialKey, tile: Tile) =>
      val extent = metadata.mapTransform(key)
      val re = RasterExtent(extent, metadata.layout.tileCols,
        metadata.layout.tileRows)

      Rasterizer.foreachCellByMultiPolygon(areaOfInterest, re) { case (col, row) =>
        val pixelValue: String = tile.get(col, row).toString
        pixelValue match {
          case "11" => water += 1
          case "12" => snowIce += 1
          case "21" => lowRes += 1
          case "22" => highRes += 1
          case "23" => commercial += 1
          case "31" => rock += 1
          case "32" => gravelEtc += 1
          case "33" => transitional += 1
          case "41" => deciduous += 1
          case "42" => evergreen += 1
          case "43" => mixedForest += 1
          case "51" => shrub += 1
          case "61" => orchardsEtc += 1
          case "71" => grasslands += 1
          case "81" => pasture += 1
          case "82" => rowCrops += 1
          case "83" => smallGrains += 1
          case "84" => fallow += 1
          case "85" => urbanGrasses += 1
          case "91" => woodyWetlands += 1
          case "92" => herbaceousWetlands += 1
          case _ => noData += 1
        }
      }
    })

    List(water, snowIce, lowRes, highRes, commercial, rock, gravelEtc,
      transitional, deciduous, evergreen, mixedForest, shrub, orchardsEtc,
      grasslands, pasture, rowCrops, smallGrains, fallow, urbanGrasses,
      woodyWetlands, herbaceousWetlands, noData)
      .map { acc =>
        acc.name match {
          case Some(n) => (n, acc.value)
          case None => ("no name", acc.value)
        }
      }
      .toMap
  }
}
