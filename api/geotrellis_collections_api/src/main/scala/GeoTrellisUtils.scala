import scala.io.StdIn
import scala.util._
import geotrellis.raster._
import geotrellis.raster.render._
import geotrellis.raster.mapalgebra.focal.Kernel
import geotrellis.vector._
import geotrellis.vector.io._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.s3._
import org.apache.spark._
import spray.json._

trait GeoTrellisUtils {
  def parseShape(geoJson: String): Polygon =
      geoJson.parseJson.convertTo[Polygon]

  // Adapted from http://geotrellis.readthedocs.io/en/latest/tutorials/kernel-density.html
  def randomPointFeature(extent: Extent): PointFeature[Double] = {
    def randInRange (low: Double, high: Double): Double = {
      val x = Random.nextDouble
      low * (1-x) + high * x
    }
    Feature(Point(randInRange(extent.xmin, extent.xmax),
                  randInRange(extent.ymin, extent.ymax)),
            Random.nextInt % 16 + 16)
  }

  def createTile(extent: Extent): Png = {
    val pts = (for (i <- 1 to 1000) yield randomPointFeature(extent)).toList
    val kernelWidth: Int = 9
    val kern: Kernel = Kernel.gaussian(kernelWidth, 1.5, 25)
    val kde: Tile = pts.kernelDensity(kern, RasterExtent(extent, 700, 400))
    val colorMap = ColorMap(
      (0 to kde.findMinMax._2 by 4).toArray,
      ColorRamps.HeatmapBlueToYellowToRedSpectrum)

    kde.renderPng(colorMap)
  }

  def catalog(sc: SparkContext): S3LayerReader =
    catalog("datahub-catalogs-us-east-1", "")(sc)

  def catalog(bucket: String, rootPath: String)(implicit sc: SparkContext): S3LayerReader = {
    val attributeStore = new S3AttributeStore(bucket, rootPath)
    val catalog = new S3LayerReader(attributeStore)
    catalog
  }

  def getSlopeRDDForShape(geoJson: String, sc: SparkContext): TileLayerRDD[SpatialKey] = {
    getRDDForShape(geoJson, "us-percent-slope-30m-epsg5070", sc)
  }

  def getNLCDRDDForShape(geoJson: String, sc: SparkContext): TileLayerRDD[SpatialKey] = {
    getRDDForShape(geoJson, "nlcd-2011-30m-epsg5070-0.10.0", sc)
  }

  def getRDDForShape(geoJson: String, layerName: String, sc: SparkContext): TileLayerRDD[SpatialKey] = {
    val shapeExtent = parseShape(geoJson).envelope
    catalog(sc)
      .query[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](LayerId(layerName, 0))
      .where(Intersects(shapeExtent))
      .result
  }
}
