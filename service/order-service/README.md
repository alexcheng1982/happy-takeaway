```json
{
  "name": "happy-takeaway-orders-outbox-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "database.hostname": "order-postgres",
    "database.port": "5432",
    "database.user": "puser",
    "database.password": "ppassword",
    "database.dbname": "happy-takeaway",
    "database.server.name": "dev",
    "schema.include.list": "happy_takeaway",
    "table.include.list": "happy_takeaway.outbox_event",
    "transforms": "outbox",
    "transforms.outbox.type": "io.debezium.transforms.outbox.EventRouter",
    "transforms.outbox.table.fields.additional.placement": "type:header:eventType,timestamp:header",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "key.converter.schemas.enable": false,
    "value.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter.schemas.enable": false
  }
}
```

List all topics:

```
/kafka/bin/kafka-topics.sh --list --zookeeper=zookeeper:2181
```

Watch topic:

```
/kafka/bin/kafka-console-consumer.sh --from-beginning --bootstrap-server=kafka:9092 --topic=outbox.event.Order
```
