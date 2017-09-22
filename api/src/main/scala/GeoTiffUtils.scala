import geotrellis.proj4.{ConusAlbers}
import geotrellis.raster._
import geotrellis.raster.resample._
import geotrellis.spark._
import geotrellis.spark.tiling._
import geotrellis.spark.io.hadoop._
import geotrellis.spark.io.kryo.KryoRegistrator
import geotrellis.vector._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.KryoSerializer

trait GeoTiffUtils {
  def getPANLCDGeoTiff(): ContextRDD[SpatialKey, Tile, TileLayerMetadata[SpatialKey]] = {
    val conf = new SparkConf()
      .setIfMissing("spark.master", "local[*]")
      .setAppName("Read PA NLCD GeoTiff")
      .set("spark.serializer", classOf[KryoSerializer].getName)
      .set("spark.kryo.registrator", classOf[KryoRegistrator].getName)

    implicit val sc = new SparkContext(conf)

    val nlcdLayerURI =
      new java.net.URI("s3://azavea-research-public-data/geotrellis/samples/nlcd_pa.tif")

    val geoTiffRDD =
      HadoopGeoTiffRDD.spatial(new Path(nlcdLayerURI))

    val (_, metadata) =
          geoTiffRDD.collectMetadata[SpatialKey](ConusAlbers, FloatingLayoutScheme(256))

    new ContextRDD(
      geoTiffRDD
        .tileToLayout(metadata, NearestNeighbor)
        .mapValues { tile => tile.convert(ByteConstantNoDataCellType) },
      metadata.copy(cellType = ByteConstantNoDataCellType)
    )
  }
}
