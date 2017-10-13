import java.util.concurrent.atomic.{LongAdder}
import collection.concurrent.TrieMap

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
    rasterLayer: TileLayerCollection[SpatialKey],
    areaOfInterest: MultiPolygon
  ): Map[String, Int] = {
    val init = () => new LongAdder
    val update = (_: LongAdder).increment()

    val metadata = rasterLayer.metadata
    val pixelCounts: TrieMap[Int, LongAdder] = TrieMap.empty

    rasterLayer.foreach({ case (key: SpatialKey, tile: Tile) =>
      val extent = metadata.mapTransform(key)
      val re = RasterExtent(extent, metadata.layout.tileCols,
        metadata.layout.tileRows)

      Rasterizer.foreachCellByMultiPolygon(areaOfInterest, re) { case (col, row) =>
        val pixelValue: Int = tile.get(col, row).toInt
        val acc = pixelCounts.getOrElseUpdate(pixelValue, init())
        update(acc)
      }
    })

    pixelCounts
      .map { case (k, v) => k.toString -> v.sum.toInt }
      .toMap
  }
}
