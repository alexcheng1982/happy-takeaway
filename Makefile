COMPOSE_FILE_MAIN=devops/dev/docker-compose.yaml
COMPOSE_FILE_DB=devops/dev/db/docker-compose.yaml
COMPOSE_PROJECT=happy-takeaway-dev

docker-compose:
	docker-compose -p ${COMPOSE_PROJECT} -f ${COMPOSE_FILE_MAIN} -f ${COMPOSE_FILE_DB} up -d

clean:
	docker-compose -p ${COMPOSE_PROJECT} -f ${COMPOSE_FILE_MAIN} -f ${COMPOSE_FILE_DB} kill
	docker-compose -p ${COMPOSE_PROJECT} -f ${COMPOSE_FILE_MAIN} -f ${COMPOSE_FILE_DB} rm -f

restaurant-dev:
	cd service/restaurant-service && quarkus dev --debug-port=5005

order-dev:
	cd service/order-service && quarkus dev --debug-port=5006

file-upload-dev:
	cd service/file-upload-service && quarkus dev --debug-port=5007

restaurant-search-dev:
	cd service/restaurant-search-service && quarkus dev --debug-port=5008

delivery-dev:
	cd service/delivery-service && quarkus dev --debug-port=5009

rider-dev:
	cd service/rider-service && quarkus dev --debug-port=5010

api-dev:
	cd service/mobile-api-graphql && quarkus dev --debug-port=5011