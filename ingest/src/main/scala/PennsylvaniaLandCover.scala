import geotrellis.proj4._
import geotrellis.raster._
import geotrellis.raster.io.geotiff._
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.raster.resample._
import geotrellis.vector._
import geotrellis.vector.io._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.index._
import geotrellis.spark.io.file._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.kryo.KryoRegistrator
import geotrellis.spark.io.s3._
import geotrellis.spark.tiling._
import org.apache.spark._
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.rdd.RDD
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path

object LandCoverIngest {
  val localGeoTiffPath =
    new java.io.File(new java.io.File(".").getCanonicalFile,
      "land-cover-data/geotiff/nlcd_pa.tif").getAbsolutePath

  val localCatalogPath =
    new java.io.File(new java.io.File(".").getCanonicalFile,
      "land-cover-data/catalog").getAbsolutePath

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")
      .setAppName("Ingest PA Land Cover GeoTiff")
      .set("spark.serializer", classOf[KryoSerializer].getName)
      .set("spark.kryo.registrator", classOf[KryoRegistrator].getName)

    implicit val sc = new SparkContext(conf)
    try {
      val geoTiffRDD =
        HadoopGeoTiffRDD.spatial(new Path(localGeoTiffPath))

      val (_, metadata) =
        geoTiffRDD.collectMetadata[SpatialKey](FloatingLayoutScheme(256))

      val paLandCoverLayer =
        ContextRDD(
          geoTiffRDD
            .tileToLayout(metadata, NearestNeighbor)
            .mapValues { tile => tile.convert(ByteConstantNoDataCellType) },
          metadata.copy(cellType = ByteConstantNoDataCellType))

      val paLandCoverLayerID = LayerId("nlcd-pennsylvania", 0)

      FileLayerWriter(localCatalogPath)
        .write(paLandCoverLayerID, paLandCoverLayer, ZCurveKeyIndexMethod)
    } finally {
        sc.stop()
    }
  }
}
