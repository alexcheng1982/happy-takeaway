CREATE TABLE happy_takeaway.outbox_event (
  id CHAR(36) PRIMARY KEY,
  type VARCHAR(255) NOT NULL,
  timestamp bigint NOT NULL,
  aggregatetype VARCHAR(255) NOT NULL,
  aggregateid CHAR(36) NOT NULL,
  payload TEXT
);