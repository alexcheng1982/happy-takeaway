package io.vividcode.happytakeaway.event.consumer;

import io.smallrye.common.annotation.Identifier;
import io.vividcode.happytakeaway.common.json.JsonMapper;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jboss.logging.Logger;

public abstract class BaseEventConsumer {

  private static final String HEADER_ID = "id";
  private static final String HEADER_TYPE = "eventType";
  private static final String HEADER_TIMESTAMP = "timestamp";

  private static final Logger LOG = Logger.getLogger(BaseEventConsumer.class);

  @Inject
  @Identifier("default-kafka-broker")
  Map<String, Object> config;

  @Inject DuplicateMessageDetector duplicateMessageDetector;

  private final AtomicBoolean stopFlag = new AtomicBoolean();
  private final Map<String, EventHandler> eventHandlers = new HashMap<>();

  protected abstract String getConsumerId();

  protected abstract List<String> getTopics();

  @SuppressWarnings("unchecked")
  protected <T> void addEventHandler(String eventType, Class<T> eventClass, Consumer<T> consumer) {
    this.eventHandlers.put(
        eventType,
        EventHandler.builder().type(eventClass).handler((Consumer<Object>) consumer).build());
  }

  protected void start() {
    Thread workerThread = new Thread(this::pollRecords);
    workerThread.setName("kafka-consumer");
    workerThread.setDaemon(true);
    workerThread.start();
  }

  private void pollRecords() {
    String consumerId = this.getConsumerId();
    Map<String, Object> conf = new HashMap<>(this.config);
    KafkaConsumer<String, String> consumer =
        new KafkaConsumer<>(conf, new StringDeserializer(), new StringDeserializer());
    consumer.subscribe(this.getTopics());
    LOG.infov("Subscribed to topics {0}", this.getTopics());
    while (!this.stopFlag.get()) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
      for (ConsumerRecord<String, String> record : records) {
        this.processRecord(consumerId, record);
      }
    }
  }

  private void processRecord(String consumerId, ConsumerRecord<String, String> record) {
    Headers headers = record.headers();
    String eventId = this.getHeader(headers, HEADER_ID);
    if (eventId == null) {
      LOG.infov("Ignore event without id, key = {0}", record.key());
      return;
    }
    if (this.duplicateMessageDetector.isDuplicate(consumerId, eventId)) {
      LOG.infov("Duplicate message: consumerId = {0}, messageId = {1}", consumerId, eventId);
      return;
    }
    String eventType = this.getHeader(headers, HEADER_TYPE);
    if (eventType == null) {
      LOG.infov("Ignore event without type, key = {0}", record.key());
      return;
    }
    String timestampHeader = this.getHeader(headers, HEADER_TIMESTAMP);
    long timestamp =
        timestampHeader != null ? Long.parseLong(timestampHeader) : System.currentTimeMillis();
    EventHandler eventHandler = this.eventHandlers.get(eventType);
    if (eventHandler != null) {
      Object event = this.createEvent(eventHandler.type, eventId, timestamp, record.value());
      eventHandler.handler.accept(event);
    } else {
      LOG.infov("Ignore event {0} of type {1} without handlers", eventId, eventType);
    }
  }

  private String getHeader(Headers headers, String key) {
    Header header = headers.lastHeader(key);
    return header != null ? new String(header.value(), StandardCharsets.UTF_8) : null;
  }

  private Object createEvent(Class<?> eventClass, String eventId, long timestamp, String payload) {
    try {
      Class<?> payloadClass =
          (Class<?>)
              ((ParameterizedType) eventClass.getGenericSuperclass()).getActualTypeArguments()[0];
      Object payloadObj = JsonMapper.fromJson(payload, payloadClass);
      Constructor<?> constructor =
          eventClass.getConstructor(String.class, long.class, payloadClass);
      return constructor.newInstance(eventId, timestamp, payloadObj);
    } catch (Exception e) {
      LOG.warn("Failed to create event", e);
    }
    return null;
  }

  protected void stop() {
    this.stopFlag.set(true);
  }

  @Data
  @Builder
  private static class EventHandler {

    private Class<?> type;
    private Consumer<Object> handler;
  }
}
