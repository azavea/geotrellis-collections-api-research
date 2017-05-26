import geotrellis.proj4.{CRS, ConusAlbers, LatLng, WebMercator}
import geotrellis.raster._
import geotrellis.raster.rasterize._
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.s3._
import geotrellis.vector._
import geotrellis.vector.io._

import com.typesafe.config.Config
import org.apache.spark.SparkContext
import spray.json._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.concurrent.TrieMap

/**
  * Collection of common utilities to be used by SparkJobs.
  */
trait JobUtils {

    type TileLayerSeq[K] = Seq[(K,Tile)] with Metadata[TileLayerMetadata[K]]

  /**
    * Transform the incoming GeoJSON into a [[MultiPolygon]] in the
    * destination CRS.
    *
    * @param   geoJson  The incoming geometry
    * @param   srcCRS   The CRS that the incoming geometry is in
    * @param   destCRS  The CRS that the outgoing geometry should be in
    * @return           A MultiPolygon
    */
  def parseGeometry(geoJson: String, srcCRS: CRS, destCRS: CRS): MultiPolygon = {
    geoJson.parseJson.convertTo[Geometry] match {
      case p: Polygon => MultiPolygon(p.reproject(srcCRS, destCRS))
      case mp: MultiPolygon => mp.reproject(srcCRS, destCRS)
      case _ => MultiPolygon()
    }
  }

  /**
    * Transform incoming GeoJSON into a [[MultiLine]] in the destination CRS.
    * Same as [[parseGeometry()]] but for MultiLines instead.
    *
    * @param   geoJson  The incoming geometry
    * @param   srcCRS   The CRS that the incoming geometry is in
    * @param   destCRS  The CRS that the outgoing geometry should be in
    * @return           A MultiLine
    */
  def toMultiLine(geoJson: String, srcCRS: CRS, destCRS: CRS): MultiLine = {
    geoJson.parseJson.convertTo[Geometry] match {
      case l: Line => MultiLine(l.reproject(srcCRS, destCRS))
      case ml: MultiLine => ml.reproject(srcCRS, destCRS)
      case _ => MultiLine()
    }
  }

  /**
    * Fetch a particular layer from the catalog, restricted to the
    * given extent, and return a [[TileLayerRDD]] of the result.
    *
    * @param   catalog  The S3 location from which the data should be read
    * @param   layerId  The layer that should be read
    * @param   extent   The extent (subset) of the layer that should be read
    * @return           An RDD of [[SpatialKey]]s
    */
  def queryAndCropLayer(
    catalog: S3LayerReader,
    layerId: LayerId,
    extent: Extent
  ): TileLayerRDD[SpatialKey] = {
    catalog.query[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](layerId)
      .where(Intersects(extent))
      .result
  }


  def queryAndCropLayer2(
    catalog: S3CollectionLayerReader,
    layerId: LayerId,
    aoi: MultiPolygon
  ): TileLayerSeq[SpatialKey] = {
    catalog.query[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](layerId)
      .where(Intersects(aoi))
      .result
  }

  /**
    * Return an [[S3LayerReader]] object to read from the catalog
    * directory in the azavea datahub.
    *
    * @return  An S3LayerReader object
    */
  def catalog(sc: SparkContext): S3LayerReader =
    catalog("azavea-datahub", "catalog")(sc)

  /**
    * Take a bucket and a catalog, and return an [[S3LayerReader]]
    * object to read from it.
    *
    * @param   bucket    The name of the S3 bucket
    * @param   rootPath  The name of the catalog (child of the root directory) in the bucket
    * @return            An S3LayerReader object
    */
  def catalog(bucket: String, rootPath: String)(implicit sc: SparkContext): S3LayerReader = {
    val attributeStore = new S3AttributeStore(bucket, rootPath)
    val catalog = new S3LayerReader(attributeStore)

    catalog
  }

  def collectionLayerReader(): S3CollectionLayerReader =
    collectionLayerReader("azavea-datahub", "catalog")

  def collectionLayerReader(bucket: String, rootPath: String): S3CollectionLayerReader = {
    val attributeStore = new S3AttributeStore(bucket, rootPath)
    S3CollectionLayerReader(attributeStore)
  }

  /**
    * For a given config, return a function that can find the value of a key
    * if it exists in the config.
    *
    * @param   config    The config containing keys
    * @return            A function String => Option[String] that takes a key and returns its value if it exists, else None
    */
  def getOptionalFn(config: Config) = (key: String) => {
    config.hasPath(key) match {
      case true => Option(config.getString(key))
      case false => None
    }
  }

  /**
    * For a given config and CRS key, return one of several recognized
    * [[geotrellis.proj4.CRS]]s, or raise an error.
    *
    * @param  config  The config
    * @param  key     The key (e.g. "input.rasterCRS")
    */
  def getCRS(config: Config, key: String) = {
    config.getString(key) match {
      case "LatLng" => LatLng
      case "WebMercator" => WebMercator
      case "ConusAlbers" => ConusAlbers
      case s: String => throw new Exception(s"Unknown CRS: $s")
    }
  }

  /**
    * Returns a join of a sequence of raster layers, containing between 1
    * and 3 layers.
    *
    * @param   rasterLayers  The list of layers
    * @return                Joined RDD with a list of tiles, corresponding to each raster in the list, matching a spatial key
    */
  def joinRasters(rasterLayers: Seq[TileLayerRDD[SpatialKey]]) = {
    rasterLayers.length match {
      case 1 =>
        rasterLayers.head
          .map({ case (k, v) => (k, List(v)) })
      case 2 =>
        rasterLayers.head.join(rasterLayers.tail.head)
          .map({ case (k, (v1, v2)) => (k, List(v1, v2)) })
      case 3 =>
        rasterLayers.head.join(rasterLayers.tail.head).join(rasterLayers.tail.tail.head)
          .map({ case (k, ((v1, v2), v3)) => (k, List(v1, v2, v3)) })

      case 0 => throw new Exception("At least 1 raster must be specified")
      case _ => throw new Exception("At most 3 rasters can be specified")
    }
  }

  /**
    * Given a layer, a key, a tile, and a sequence of MultiPolygons, returns
    * a list of distinct pixels present in all polygons clipped to an extent
    * corresponding to the key and tile.
    *
    * @param   layer         The [[TileLayerRDD]] to clip
    * @param   key           The [[SpatialKey]] to transform extent to
    * @param   tile          The [[Tile]] to calculate raster extent from
    * @param   multiPolygons The list of polygons
    * @return                List of distinct pixels
    */
  def getDistinctPixels(layer: TileLayerRDD[SpatialKey], key: SpatialKey, tile: Tile, multiPolygons: Seq[MultiPolygon]) = {
    val extent = layer.metadata.mapTransform(key)
    val rasterExtent = RasterExtent(extent, tile.cols, tile.rows)

    val pixels = mutable.ListBuffer.empty[(Int, Int)]
    val cb = new Callback {
      def apply(col: Int, row: Int): Unit = {
        val pixel = (col, row)
        pixels += pixel
      }
    }

    multiPolygons.foreach({ multiPolygon =>
      multiPolygon & extent match {
        case PolygonResult(p) =>
          Rasterizer.foreachCellByPolygon(p, rasterExtent)(cb)
        case MultiPolygonResult(mp) =>
          mp.polygons.foreach({ p =>
            Rasterizer.foreachCellByPolygon(p, rasterExtent)(cb)
          })

        case _ =>
      }
    })

    pixels.distinct
  }

  /**
    * Convenience method for parsing config for various RasterGrouped operations
    */
  def parseGroupedConfig(config: Config) = {
    val crs: String => geotrellis.proj4.CRS = getCRS(config, _)

    val zoom = config.getInt("input.zoom")
    val rasterCRS = crs("input.rasterCRS")
    val polygonCRS = crs("input.polygonCRS")
    val rasterLayerIds = config.getStringList("input.rasters").asScala.map({ str => LayerId(str, zoom) })
    val polygon = config.getStringList("input.polygon").asScala.map({ str => parseGeometry(str, polygonCRS, rasterCRS) })

    val targetLayerId: Option[LayerId] = {
      if (config.hasPath("input.targetRaster"))
        Some(LayerId(config.getString("input.targetRaster"), zoom))
      else
        None
    }

    (rasterLayerIds, targetLayerId, polygon)
  }

  /**
    * Convenience method for parsing config for various RasterLinesJoin operations
    */
  def parseLinesJoinConfig(config: Config) = {
    val crs: String => geotrellis.proj4.CRS = getCRS(config, _)

    val zoom = config.getInt("input.zoom")
    val rasterCRS = crs("input.rasterCRS")
    val polygonCRS = crs("input.polygonCRS")
    val linesCRS = crs("input.vectorCRS")
    val rasterLayerIds = config.getStringList("input.rasters").asScala.map({ str => LayerId(str, zoom) })
    val polygon = config.getStringList("input.polygon").asScala.map({ str => parseGeometry(str, polygonCRS, rasterCRS) })
    val lines = config.getStringList("input.vector").asScala.map({ str => toMultiLine(str, linesCRS, rasterCRS) })

    (rasterLayerIds, lines, polygon)
  }

  /**
    * Set of methods for easy processing of parameters
    */
  def toLayers(rasterLayerIds: Seq[LayerId], polygon: Seq[MultiPolygon], sc: SparkContext) = {
    val extent = GeometryCollection(polygon).envelope
    val rasterLayers = rasterLayerIds.map({ rasterLayerId =>
      queryAndCropLayer(catalog(sc), rasterLayerId, extent)
    })

    rasterLayers
  }

  def toLayers2(rasterLayerIds: Seq[LayerId], polygon: MultiPolygon): Seq[TileLayerSeq[SpatialKey]] = {
    val rasterLayers = rasterLayerIds.map({ rasterLayerId =>
      queryAndCropLayer2(collectionLayerReader(), rasterLayerId, polygon)
    })

    rasterLayers
  }


  def joinCollectionLayers(layers: Seq[TileLayerSeq[SpatialKey]]): Map[SpatialKey, Seq[Tile]] = {
    val maps: Seq[Map[SpatialKey, Tile]] = layers.map((_: Seq[(SpatialKey, Tile)]).toMap)
    val keySet: Array[SpatialKey] = maps.map(_.keySet).reduce(_ union _).toArray
    for (key: SpatialKey <- keySet) yield {
      val tiles: Seq[Tile] = maps.map(_.apply(key))
      key -> tiles
    }
  }.toMap

  def rasterGroupedCount2[A](
    rasterLayers: Seq[TileLayerSeq[SpatialKey]],
    multiPolygon: MultiPolygon,
    init: () => A,
    update: A => Unit
  ): TrieMap[List[Int], A] = {
    // assume all the layouts are the same
    val metadata = rasterLayers.head.metadata

    var pixelGroups: TrieMap[List[Int], A] = TrieMap.empty

    joinCollectionLayers(rasterLayers).par
      .map({ case (key, tiles) =>
        val extent: Extent = metadata.mapTransform(key)
        val re: RasterExtent = RasterExtent(extent, metadata.layout.tileCols, metadata.layout.tileRows)

        Rasterizer.foreachCellByMultiPolygon(multiPolygon, re){ case (col, row) =>
          val pixelGroup: List[Int] = tiles.map(_.get(col, row)).toList
          val acc = pixelGroups.getOrElseUpdate(pixelGroup, init())
          update(acc)
        }
      })
    pixelGroups
  }

  def toLayers(rasterLayerIds: Seq[LayerId], targetLayerId: LayerId, polygon: Seq[MultiPolygon], sc: SparkContext) = {
    val extent = GeometryCollection(polygon).envelope
    val rasterLayers = rasterLayerIds.map({ rasterLayerId =>
      queryAndCropLayer(catalog(sc), rasterLayerId, extent)
    })
    val targetLayer = queryAndCropLayer(catalog(sc), targetLayerId, extent)

    (rasterLayers, targetLayer)
  }

  def toLayers(targetLayerId: LayerId, polygon: Seq[MultiPolygon], sc: SparkContext) = {
    val extent = GeometryCollection(polygon).envelope
    val targetLayer = queryAndCropLayer(catalog(sc), targetLayerId, extent)

    targetLayer
  }
}

object JobUtils extends JobUtils
