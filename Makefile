.DEFAULT_GOAL := setup

.PHONY: build bundle-app compile-app app-server api-server app-console \
	api-console assembly compile server setup restart ingest-assembly ingest \
	compile-ingest download-tif

build:
	docker-compose -f docker-compose.yml run --rm --no-deps app yarn

bundle-app:
	bash -c "trap 'cd ..' EXIT; cd app; npm install; npm run bundle"

compile-api:
	bash -c "trap 'cd ..' EXIT; cd api; sbt compile"

app-server:
	docker-compose up -d app

api-server:
	bash -c "trap 'cd ..' EXIT; cd api; sbt ~reStart"

app-console:
	docker-compose exec app /bin/bash

api-console:
	bash -c "trap 'cd ..' EXIT; cd api; sbt console"

assembly:
	bash -c "trap 'cd ..' EXIT; cd api; sbt assembly"

compile: bundle-app compile-api compile-ingest

restart: api-server

server: app-server api-server

setup: build paint

download-tif:
ifeq (,$(wildcard ./ingest/land-cover-data/geotiff/nlcd_pa.tif))
	curl -o ./ingest/land-cover-data/geotiff/nlcd_pa.tif \
	https://azavea-research-public-data.s3.amazonaws.com/geotrellis/samples/nlcd_pa.tif
endif

ingest-assembly:
	bash -c "trap 'cd ..' EXIT; cd ingest; sbt assembly"

compile-ingest:
	bash -c "trap 'cd ..' EXIT; cd ingest; sbt compile"

ingest: ingest-assembly download-tif
ifeq (,$(wildcard ./ingest/land-cover-data/catalog/attributes/nlcd-pennsylvania__.__0__.__metadata.json))
	bash -c "trap 'cd ..' EXIT; cd ingest; spark-submit \
		--name \"NLCDPA Ingest\" \
		--master \"local[*]\" \
		--driver-memory 4G \
		--class LandCoverIngest \
		target/scala-2.11/geotrellis_collections_api_ingest-assembly-1.0.jar"
endif

paint: ingest
ifeq (,$(wildcard ./ingest/land-cover-data/tiles/nlcd-pennsylvania/0/0/0.png))
	bash -c "trap 'cd ..' EXIT; cd ingest; spark-submit \
		--name \"Paint PA Land Cover Tiles\" \
		--master \"local[*]\" \
		--driver-memory 4G \
		--class LandCoverPaint \
		target/scala-2.11/geotrellis_collections_api_ingest-assembly-1.0.jar"
endif
