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
  // Number of Spark Partitions. Recommended to go 5-10x the number of cores
  val SparkPartitions = 50

  // Tile Size in pixels. 256 is pretty standard for visual tiles
  val TileSize = 256

  // Since the data is integral, we use Nearest Neighbor resampling method.
  // For float values, we prefer Bilinear.
  val ResampleMethod = NearestNeighbor

  // Maximum Zoom for which to pain the layers. Each zoom level is an order
  // of magnitude larger than the last. 13 has a scale of 1:70K, appropriate
  // for representing villages and suburbs.
  val MaxZoom = 13

  // Layer Name which will be used for reading the ingested data layer as well
  // as the directory for tiles to be written in.
  val LayerName = "nlcd-pennsylvania"

  // Path to local GeoTIFF, used for reading the color ramp
  val localGeoTiffPath = new java.io.File(
    new java.io.File(".").getCanonicalFile,
    "land-cover-data/geotiff/nlcd_pa.tif"
  ).getAbsolutePath

  // Path to local ingested data, used for reading the cell values
  val localCatalogPath = new java.io.File(
    new java.io.File(".").getCanonicalFile,
    "land-cover-data/catalog"
  ).getAbsolutePath

  // Path to local tiles directory, used for writing the tile images
  val localTilesPath = new java.io.File(
    new java.io.File(".").getCanonicalFile,
    "land-cover-data/tiles"
  ).getAbsolutePath

  def main(args: Array[String]): Unit = {
    // Initialize Spark
    val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")
      .setAppName("Paint PA Land Cover Tiles")
      .set("spark.serializer", classOf[KryoSerializer].getName)
      .set("spark.kryo.registrator", classOf[KryoRegistrator].getName)

    implicit val sc = new SparkContext(conf)

    try {
      // Read color map from original GeoTIFF
      val geotiff = SinglebandGeoTiff(localGeoTiffPath)
      val colorMap = geotiff.options.colorMap.get

      // Initialize source layer from the local catalog
      val layerId = LayerId(LayerName, 0)
      val layerReader = FileLayerReader(localCatalogPath)
      val sourceLayer =
        layerReader.read[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](
          layerId,
          SparkPartitions
        )

      // Set zoomed layout scheme with projection and tile size
      val layoutScheme = ZoomedLayoutScheme(WebMercator, TileSize)

      // Reproject the source layer to WebMercator for painting
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

      // Pyramid the reprojected layer into as many zoom levels as specified,
      // given the layout scheme and resampling method
      Pyramid.levelStream(
        webMercatorLayer,
        layoutScheme,
        MaxZoom,
        ResampleMethod
      ).foreach { case (z, levelLayer) =>
        // For each zoom level layer, find the spatial key to z/x/y path
        val paintedLayerId = LayerId(LayerName, z)
        val keyToPath = SaveToHadoop.spatialKeyToPath(paintedLayerId, path)

        // Paint the layer values with the color map from above and save
        levelLayer
          .mapValues(_.renderPng(colorMap).bytes)
          .saveToHadoop(keyToPath)
      }
    } finally {
      sc.stop()
    }
  }
}
