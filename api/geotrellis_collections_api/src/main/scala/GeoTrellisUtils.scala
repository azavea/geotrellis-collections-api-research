import scala.io.StdIn
import scala.util._
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import geotrellis.raster._
import geotrellis.raster.render._
import geotrellis.raster.rasterize.Rasterizer.Options
import geotrellis.raster.mapalgebra.focal.Kernel
import geotrellis.vector._
import geotrellis.vector.io._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.s3._
import org.apache.spark._
import spray.json._
import spire.syntax.cfor._

trait GeoTrellisUtils {
  def parseShape(geoJson: String): Polygon =
      geoJson.parseJson.convertTo[Polygon]

  def slopeLayer = LayerId("us-percent-slope-30m-epsg5070", 0)
  def nlcdLayer = LayerId("nlcd-2011-30m-epsg5070-0.10.0", 0)
  def baseLayerReader = {
    val attributeStore = new S3AttributeStore("datahub-catalogs-us-east-1", "")
    new S3CollectionLayerReader(attributeStore)
  }

  def layerFetch(layer: LayerId, shape: Polygon) =
    Future {
      baseLayerReader
        .query[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](layer)
        .where(Intersects(shape))
        .result
    }

  def nlcdFetch(shape: Polygon) = layerFetch(nlcdLayer, shape)
  def slopeFetch(shape: Polygon) = layerFetch(slopeLayer, shape)

  def nlcdSlopeCount(shape: String): Future[Map[(Int, Int), Long]] = {
    val polygon = parseShape(shape)
    for (
      nlcdLayer <- nlcdFetch(polygon);
      slopeLayer <- slopeFetch(polygon)
    ) yield {
      histograms(
        nlcdLayer,
        slopeLayer,
        polygon
      )
    }
  }

  // from https://github.com/WikiWatershed/mmw-geoprocessing/issues/47
  def histograms(
    layer1: TileLayerCollection[SpatialKey],
    layer2: TileLayerCollection[SpatialKey],
    polygon: Polygon
  ): Map[(Int, Int), Long] = {
    val mapTransform = layer1.metadata.layout.mapTransform

    val layer1Map = layer1.toMap
    val layer2Map = layer2.toMap

    val result =
      layer1Map.keys.toSet
        .intersect(layer2Map.keys.toSet)
        .par
        .map { k =>
          val tileResult = mutable.Map[(Int, Int), Long]()
          val (tile1, tile2) = (layer1Map(k), layer2Map(k))
          // Assumes tile1.dimensions == tile2.dimensions
          val re = RasterExtent(mapTransform(k), tile1.cols, tile1.rows)

          if (polygon.contains(re.extent)) {
            cfor(0)(_ < re.cols, _ + 1) { col =>
              cfor(0)(_ < re.rows, _ + 1) { row =>
                val v1 = tile1.get(col, row)
                val v2 = tile2.get(col, row)
                val k = (v1, v2)
                if (!tileResult.contains(k)) {
                  tileResult(k) = 0
                }
                tileResult(k) += 1
              }
            }
          } else {
            polygon.foreach(re, Options(includePartial=true, sampleType=PixelIsArea)) { (col, row) =>
              val v1 = tile1.get(col, row)
              val v2 = tile2.get(col, row)
              val k = (v1, v2)
              if(!tileResult.contains(k)) {
                tileResult(k) = 0
              }
              tileResult(k) += 1
            }
          }

          tileResult.toMap
        }
        .reduce { (m1, m2) =>
          (m1.toSeq ++ m2.toSeq)
            .groupBy(_._1)
            .map { case(k, values) => k -> values.map(_._2).sum }
        }

    result
  }
}
