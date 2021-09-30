CREATE TABLE happy_takeaway.processed_messages (
  consumer_id VARCHAR(50) NOT NULL,
  message_id CHAR(36) NOT NULL,
  timestamp bigint NOT NULL,
  PRIMARY KEY (consumer_id, message_id)
);