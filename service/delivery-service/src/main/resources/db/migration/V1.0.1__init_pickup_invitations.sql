CREATE TABLE happy_takeaway.delivery_tasks (
  id CHAR(36) PRIMARY KEY,
  status VARCHAR(16) NOT NULL,
  rider_id CHAR(36)
);

CREATE TABLE happy_takeaway.delivery_pickups (
  task_id CHAR(36) NOT NULL,
  rider_id CHAR(36) NOT NULL,
  status VARCHAR(16) NOT NULL,
  PRIMARY KEY (task_id, rider_id),
  CONSTRAINT fk_task_id FOREIGN KEY (task_id) REFERENCES happy_takeaway.delivery_tasks(id) ON DELETE CASCADE
);
