# ADR 001 - Raster Map Algebra Operations

## Context

As part of my research into using a GeoTrellis REST service, I'm spending some time learning about raster operations. The aim's to gain a clearer sense of what kinds of operations are available and the occasions in which they might be useful

This ADR aims to summarize some of the map algebra ops catalogued in Dana Tomlin's book *GIS and Cartographic Modeling* as preparation for deciding what API endpoints to implement.

The book segments these operations into three subsets

- Local operations, which return values for cells based on the values of the same cell in different rasters
- Focal operations, which return values for cells in relation to other cells within a defined "neighborhood"
- Zonal operations, which return values for cells in reation to other cells within a defined "zone" (e.g. a watershed);

#### Local Operations

> Each generates a new map layer on which every location is set to a value computed as a specified function of the location's value(s) on one or more existing map layers.

GeoTrellis package: [geotrellis.raster.mapalgebra.local](https://geotrellis.github.io/scaladocs/latest/#geotrellis.raster.mapalgebra.local.package)

##### Operations

*Local Calculation*: Calculate new cell value as an arithmetic function of the same cells in other rasters.

*Local Classification*: Calculate new cell value by classifying values of the same cells in other rasters.

*Local Combination*: Calculate new cell value based on combinations of values from other rasters.

*Local Majority*: Calculate new cell value based on the values occurring most frequently in other rasters.

*Local Maximum*: Calculate new cell value based on the highest value for the same cells in other rasters.

*Local Mean*: Calculate new cell value based on the average value of the same cells in other rasters.

*Local Minimum*: Calculate new cell value based on the lowest value for the same cells in other rasters.

*Local Minority*: Calculate new cell value based on the values occurring least frequently in other rasters.

*Local Variety*: Calculate new cell value indicating the number of dissimilar values for the same cells in other rasters.

#### Focal Operations

> generates a new map layer on which every location is set to a value computed as a specified function of the values, distances, and/or directions of neighboring locations. Certain of these operations apply to neighborhoods that can extend well beyond the immediate vicinity of each location, with neighborhood distances measured in terms of physical separation, travel costs, or lines of sight.

GeoTrellis package: [geotrellis.raster.mapalgebra.focal](https://geotrellis.github.io/scaladocs/latest/#geotrellis.raster.mapalgebra.focal.package)

##### *Spreading phase*

> all distances from each neighborhood focus are to be measured as an accumulation of costs associated with location-to-location movement.

##### *Radiating phase*

> distances from each neighborhood focus are to be measured over unobstructed lines of sight.

##### Operations on Extended Neighborhoods

*Focal Bearing*: Calculate cell value indicating the direction of the nearest non-null/-0 cell location in neighborhood.

*Focal Classification*: Return new cell value which indicate the combination of zones in neighborhood.

*Focal Combination*: Calculate new cell value which indicates the combination of zones occurring in neighborhood cells.

*Focal Distribution*: Calculate new cell value indicating the "inverse-distance-weighted-average" of all other neighborhood cells.

*Focal Insularity*: Return new cell value which "uniquely matches" the values assigned to other cells in the same neighborhood which are also in the same zone.

*Focal Interpolation*: Return new cell value indicating the "inverse-square-distance-weighted-average" of all other neighborhood cells.

*Focal Majority*: Return new cell value indicating the cell value occurring most frequently in neighborhood.

*Focal Maximum*: Return new cell value indicating the maximum value of all neighborhood cells.

*Focal Mean*: Calculate new cell value indicating the average of all neighborhood cells.

*Focal Minimum*: Return new cell value indicating the minimum value of all neighborhood cells.

*Focal Minority*: Return new cell value indicating the value occurring least frequently in neighborhood.

*Focal Neighbor*: Return new cell value indicating the value of the nearest non-null-value neighborhood cell.

*Focal Percentage*: Return new cell value indicating the percentage of neighborhood cells with values equal to the original cell.

*Focal Percentile*: Return new cell value indicating what percentage of neighborhood cells have values less than the original cell.

*Focal Proximity*: Return new cell value indicating the distance to the nearest non-null cell? New value is "treated as a measure of distance already accumulated at that location when new distances are calculated."

*Focal Ranking*: Calculate new cell value indicating how many zones in neighborhood with lower values than the original cell.

*Focal Sum*: Calculate new cell value by summing all neighborhood cells.

*Focal Variety*: Return new cell value indicating the number of zones in neighborhood.

##### Operations on Immediate Neighborhoods

*Focal Area*: Calculate new cell value indicating the area of whatever portion of an "areal condition" is represented by that cell when a first raster's projected onto another raster.

*Focal Aspect*: Calculate new cell value indicating the compass direction of steepest descent for a plane inferred from the surface layer values of that location and any adjacent neighbors that share its first layer value."

*Focal Drainage*: Return a new cell value indicating which cells "lie upstream on a surface inferred from the surface layers of all locations in the same first layer zone."

*Focal Frontage*: Calculate new cell value indicating "the length" of the boundaries formed by whatever portion of an areal condition is represented by that location" on a first raster when it's projected onto another raster.

*Focal Gradient*: Calculate new cell value indicating the slope of a plane inferred from the original value of the cell and adjacent neighbors with the same value.

*Focal Length*: Return new cell value indicating the length "of whatever portion of a lineal condition is represented by the location's original value when projected" onto another raster's values.

*Focal Linkage*: Calculate a new cell value indicating the "type of form" from cell and neighborhood.

*Focal Partition*: Return new cell value indicating the "areal boundary" of upper, upper right, and right neighbors.

*Focal Volume*: Return new cell value indicating the "surficial volume beneath whatever portion of an areal condition" corresponds to the first raster value when projected onto a second raster.

#### Zonal Operations

> generates a new map layer on which every location is set to a value computed as a specified function of the values from one existing map layer associated with all locations in a common zone on another existing map layer.

GeoTrellis package: [geotrellis.io.mapalgebra.zonal](https://geotrellis.github.io/scaladocs/latest/#geotrellis.raster.mapalgebra.zonal.package)

##### Operations

*Zonal Classification*: Explicitly assign new cell value as a combination of values occurring in both first and second raster.

*Zonal Combination*: Calculate new cell value which indicates the combination of values in one raster also present in a second raster.

*Zonal Majority*: Calculate a new cell value indicating which values in one raster occur most often in a second raster.

*Zonal Maximum*: Calculate new cell value indicating which values in one raster occur most frequently in a second raster.

*Zonal Maximum*: Calculate new cell value indicating the highest value from a first raster within a second raster zone.

*Zonal Mean*: Calculate new cell values indicating the average first raster value present in a second raster zone.

*Zonal Minimum*: Calculate new cell value indicating the minimum first raster value in a second raster zone.

*Zonal Minority*: Calculate new cell value indicating the value from a first raster which occurs least frequently in a second raster.

*Zonal Percentage*: Calculate new cell value indicating the cell count in a second raster zone whose values are equal to the values in the first raster.

*Zonal Percentile*: Calculate new cell value indicating the cell count in a second raster zone whose values have lower values in the first raster.

*Zonal Ranking*: Calculate new cell value indicating the number of zones in the second raster which have lower values in the first raster.

*Zonal Sum*: New cell value sums the first raster values present in a second raster zone.

*Zonal Variety*: New cell value indicates the count of zones in the first raster which also occur in the second raster.

## Decision

After reading through this catalog and checking the GeoTrellis docs, I'm going to attempt to include these endpoints for these three operations as I'm implementing the API for [this issue](https://github.com/azavea/geotrellis-collections-api-research/issues/5):

- [Local variety](https://github.com/locationtech/geotrellis/blob/master/raster/src/main/scala/geotrellis/raster/mapalgebra/local/Variety.scala)
- [Focal standard deviation](https://github.com/locationtech/geotrellis/blob/master/raster/src/main/scala/geotrellis/raster/mapalgebra/focal/StandardDeviation.scala)
- [Zonal histogram](https://github.com/locationtech/geotrellis/blob/master/raster/src/main/scala/geotrellis/raster/mapalgebra/zonal/ZonalHistograms.scala)

While these GeoTrellis ops don't map precisely onto those catalogued above, they're documented GeoTrellis capabilities.

Along with those endpoints for those ops, I'm going to try writing two additional endpoints:

- an endpoint to generate PNG tiles with color ramps as [documented here](http://geotrellis.readthedocs.io/en/latest/guide/rasters.html)
- an endpoint to write data out as a GeoTIFF which seems to be available via the [GeoTiffWriter module](https://github.com/locationtech/geotrellis/blob/master/raster/src/main/scala/geotrellis/raster/io/geotiff/writer/GeoTiffWriter.scala)

Here are five API endpoints I'm going to implement:

| Path | Service |
| --- | --- |
| [/localvariety](http://localhost:7000/localvariety) | Returns local variety results |
| [/focalstandarddeviation](http://localhost:7000/focalstandarddeviation) | Returns focal standard deviation results |
| [/zonalhistogram](http://localhost:7000/zonalhistogram) | Returns zonal histogram results |
| [/pngtile](http://localhost:7000/pngtile) | Returns a PNG tile fit to the shape |
| [/geotiff](http://localhost:7000/geotiff) | Returns a GeoTIFF for the drawn shape along with Raster data |

Each will accept GeoJSON POSTed from the client.

## Consequences

One upcoming issue for my research project is to [choose some rasters to use in the project](https://github.com/azavea/geotrellis-collections-api-research/issues/4); as I'm doing so, I'll keep in mind whether there are data sets which may be more or less interesting for the local variety, focal standard deviation, and zonal histogram ops.
