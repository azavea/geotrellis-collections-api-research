.DEFAULT_GOAL := setup

.PHONY: build bundle-app compile-app app-server api-server app-console \
	api-console assembly compile server setup restart

build:
	docker-compose -f docker-compose.yml run --rm --no-deps app yarn

bundle-app:
	bash -c "trap 'cd ..' EXIT; cd app; npm install; npm run bundle"

compile-api:
	bash -c "trap 'cd ..' EXIT; cd api; ./sbt compile"

app-server:
	docker-compose up -d app

api-server:
	bash -c "trap 'cd ..' EXIT; cd api; ./sbt ~reStart"

app-console:
	docker-compose exec app /bin/bash

api-console:
	bash -c "trap 'cd ..' EXIT; cd api; ./sbt console"

assembly:
	bash -c "trap 'cd ..' EXIT; cd api; ./sbt assembly"

compile: bundle-app compile-api

restart: api-server

server: app-server api-server

setup: build server
