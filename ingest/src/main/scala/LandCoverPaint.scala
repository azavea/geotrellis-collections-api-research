import geotrellis.proj4.WebMercator
import geotrellis.raster.Tile
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.reproject.Reproject.Options
import geotrellis.raster.resample.NearestNeighbor
import geotrellis.spark.{LayerId, SpatialKey, TileLayerMetadata}
import geotrellis.spark.io._
import geotrellis.spark.io.file.FileLayerReader
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.kryo.KryoRegistrator
import geotrellis.spark.pyramid.Pyramid
import geotrellis.spark.tiling.{LayoutLevel, ZoomedLayoutScheme}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.serializer.KryoSerializer

object LandCoverPaint {
  val SparkPartitions = 50
  val TileSize = 256
  val ResampleMethod = NearestNeighbor
  val MaxZoom = 13
  val LayerName = "nlcd-pennsylvania"

  val localGeoTiffPath = new java.io.File(
    new java.io.File(".").getCanonicalFile,
    "land-cover-data/geotiff/nlcd_pa.tif"
  ).getAbsolutePath

  val localCatalogPath = new java.io.File(
    new java.io.File(".").getCanonicalFile,
    "land-cover-data/catalog"
  ).getAbsolutePath

  val localTilesPath = new java.io.File(
    new java.io.File(".").getCanonicalFile,
    "land-cover-data/tiles"
  ).getAbsolutePath

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")
      .setAppName("Paint PA Land Cover Tiles")
      .set("spark.serializer", classOf[KryoSerializer].getName)
      .set("spark.kryo.registrator", classOf[KryoRegistrator].getName)

    implicit val sc = new SparkContext(conf)

    try {
      // Read ColorMap from original GeoTiff
      val geotiff = SinglebandGeoTiff(localGeoTiffPath)
      val colorMap = geotiff.options.colorMap.get

      val layerId = LayerId(LayerName, 0)
      val layerReader = FileLayerReader(localCatalogPath)

      val sourceLayer =
        layerReader.read[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](
          layerId,
          SparkPartitions
        )

      val layoutScheme = ZoomedLayoutScheme(WebMercator, TileSize)

      // Reproject the layer to WebMercator for painting
      val LayoutLevel(_, layoutDefinition) = layoutScheme.levelForZoom(MaxZoom)
      val (_, webMercatorLayer) =
        sourceLayer.reproject(
          WebMercator,
          layoutDefinition,
          Options(
            method = NearestNeighbor,
            targetCellSize = Some(layoutDefinition.cellSize)
          ))

      val path = s"$localTilesPath/$LayerName/{z}/{x}/{y}.png"

      Pyramid.levelStream(
        webMercatorLayer,
        layoutScheme,
        MaxZoom,
        NearestNeighbor
      ).foreach { case (z, levelLayer) =>
        val paintedLayerId = LayerId(LayerName, z)
        val keyToPath = SaveToHadoop.spatialKeyToPath(paintedLayerId, path)

        levelLayer
          .mapValues(_.renderPng(colorMap).bytes)
          .saveToHadoop(keyToPath)
      }
    } finally {
      sc.stop()
    }
  }
}
