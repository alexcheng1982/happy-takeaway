COMPOSE_PROJECT_ORDER_SERVICE=order-service
COMPOSE_PROJECT_RESTAURANT_SERVICE=restaurant-service

docker-compose-order:
	docker-compose -p ${COMPOSE_PROJECT_ORDER_SERVICE} -f docker-compose.order.yaml -f docker-compose.pgadmin.yaml up --remove-orphans -d
docker-compose-order-essential:
	docker-compose -p ${COMPOSE_PROJECT_ORDER_SERVICE} -f docker-compose.order.yaml -f docker-compose.pgadmin.yaml -f docker-compose.kafka.yaml up --remove-orphans -d
docker-compose-order-all:
	docker-compose -p ${COMPOSE_PROJECT_ORDER_SERVICE} -f docker-compose.order.yaml -f docker-compose.pgadmin.yaml -f docker-compose.obs.yaml up --remove-orphans -d
docker-compose-order-clean:
	docker-compose -p ${COMPOSE_PROJECT_ORDER_SERVICE} down

docker-compose-restaurant:
	docker-compose -p ${COMPOSE_PROJECT_RESTAURANT_SERVICE} -f docker-compose.restaurant.yaml -f docker-compose.pgadmin.yaml -f docker-compose.es.yaml up --remove-orphans -d
docker-compose-restaurant-essential:
	docker-compose -p ${COMPOSE_PROJECT_RESTAURANT_SERVICE} -f docker-compose.restaurant.yaml -f docker-compose.pgadmin.yaml -f docker-compose.es.yaml -f docker-compose.kafka.yaml up --remove-orphans -d
docker-compose-restaurant-all:
	docker-compose -p ${COMPOSE_PROJECT_RESTAURANT_SERVICE} -f docker-compose.restaurant.yaml -f docker-compose.pgadmin.yaml -f docker-compose.es.yaml -f docker-compose.obs.yaml up --remove-orphans -d
docker-compose-restaurant-clean:
	docker-compose -p ${COMPOSE_PROJECT_RESTAURANT_SERVICE} down