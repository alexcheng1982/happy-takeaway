package io.vividcode.happytakeaway.delivery.repository;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.PreparedQuery;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.SqlClientHelper;
import io.vertx.mutiny.sqlclient.Tuple;
import io.vividcode.happytakeaway.delivery.entity.DeliveryPickupInvitationStatus;
import io.vividcode.happytakeaway.delivery.entity.DeliveryTaskInfo;
import io.vividcode.happytakeaway.delivery.entity.DeliveryTaskStatus;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DeliveryTaskRepository {

  @Inject PgPool client;

  private static final String SCHEMA_NAME = "happy_takeaway";
  private static final String TASK_TABLE_NAME = SCHEMA_NAME + ".delivery_tasks";
  private static final String PICKUP_TABLE_NAME = SCHEMA_NAME + ".delivery_pickups";

  public Uni<List<DeliveryTaskInfo>> listTasks() {
    return this.client
        .query("SELECT * FROM " + TASK_TABLE_NAME)
        .execute()
        .map(
            rows ->
                StreamSupport.stream(rows.spliterator(), false)
                    .map(this::extractRow)
                    .collect(Collectors.toList()));
  }

  public Uni<DeliveryTaskInfo> getTask(String taskId) {
    return this.client
        .preparedQuery("SELECT * FROM " + TASK_TABLE_NAME + " WHERE id = $1")
        .execute(Tuple.of(taskId))
        .flatMap(
            rows ->
                Uni.createFrom()
                    .optional(
                        StreamSupport.stream(rows.spliterator(), false)
                            .findFirst()
                            .map(this::extractRow)));
  }

  private DeliveryTaskInfo extractRow(Row row) {
    return DeliveryTaskInfo.builder()
        .id(row.getString("id"))
        .status(row.getString("status"))
        .riderId(row.getString("rider_id"))
        .build();
  }

  public Uni<Void> createTask(String taskId) {
    return this.client
        .preparedQuery("INSERT INTO " + TASK_TABLE_NAME + " (id, status) VALUES ($1, $2)")
        .execute(Tuple.of(taskId, DeliveryTaskStatus.CREATED.name()))
        .onItem()
        .ignore()
        .andContinueWithNull();
  }

  public Uni<Void> selectRider(String taskId, String riderId) {
    return SqlClientHelper.inTransactionUni(
        this.client,
        tx -> {
          Uni<RowSet<Row>> updateTask =
              tx.preparedQuery(
                      "UPDATE " + TASK_TABLE_NAME + " SET rider_id = $1, status = $2 WHERE id = $3")
                  .execute(Tuple.of(riderId, DeliveryTaskStatus.SELECTED.name(), taskId));
          return Uni.combine()
              .all()
              .unis(
                  updateTask,
                  this.updatePickupStatus(
                      tx, taskId, riderId, DeliveryPickupInvitationStatus.SELECTED),
                  this.getExistingRiders(tx, taskId)
                      .flatMap(
                          riders -> {
                            riders.remove(riderId);
                            if (riders.isEmpty()) {
                              return Uni.createFrom().voidItem();
                            }
                            return Uni.combine()
                                .all()
                                .unis(
                                    riders.stream()
                                        .map(
                                            id ->
                                                this.updatePickupStatus(
                                                    tx,
                                                    taskId,
                                                    id,
                                                    DeliveryPickupInvitationStatus.OTHERS_SELECTED))
                                        .collect(Collectors.toList()))
                                .discardItems();
                          }))
              .discardItems()
              .ifNoItem()
              .after(Duration.ofSeconds(10))
              .fail();
        });
  }

  public Uni<Void> updateTaskStatus(String taskId, DeliveryTaskStatus status) {
    return this.client
        .preparedQuery("UPDATE " + TASK_TABLE_NAME + " SET status = $1 WHERE id = $2")
        .execute(Tuple.of(status.name(), taskId))
        .onItem()
        .ignore()
        .andContinueWithNull();
  }

  public Uni<Void> createPickupInvitation(String taskId, String riderId) {
    return this.client
        .preparedQuery(
            "INSERT INTO " + PICKUP_TABLE_NAME + " (task_id, rider_id, status) VALUES ($1, $2, $3)")
        .execute(Tuple.of(taskId, riderId, DeliveryPickupInvitationStatus.SENT.name()))
        .onItem()
        .ignore()
        .andContinueWithNull();
  }

  public Uni<Void> createPickupInvitations(String taskId, List<String> riderIds) {
    return this.client
        .preparedQuery(
            "INSERT INTO " + PICKUP_TABLE_NAME + " (task_id, rider_id, status) VALUES ($1, $2, $3)")
        .executeBatch(
            riderIds.stream()
                .map(
                    riderId ->
                        Tuple.of(taskId, riderId, DeliveryPickupInvitationStatus.SENT.name()))
                .collect(Collectors.toList()))
        .onItem()
        .ignore()
        .andContinueWithNull();
  }

  public Uni<List<String>> getExistingRiders(String taskId) {
    return this.getExistingRiders(this.client, taskId);
  }

  private Uni<List<String>> getExistingRiders(SqlClient client, String taskId) {
    return this.queryForRiders(
        client.preparedQuery("SELECT rider_id FROM " + PICKUP_TABLE_NAME + " WHERE task_id = $1"),
        taskId);
  }

  public Uni<List<String>> getAcceptedRiders(String taskId) {
    return this.queryForRiders(
        this.client.preparedQuery(
            "SELECT rider_id FROM "
                + PICKUP_TABLE_NAME
                + " WHERE task_id = $1 AND status = 'ACCEPTED'"),
        taskId);
  }

  public Uni<Void> updatePickupStatus(
      String taskId, String riderId, DeliveryPickupInvitationStatus status) {
    return this.updatePickupStatus(this.client, taskId, riderId, status);
  }

  private Uni<Void> updatePickupStatus(
      SqlClient client, String taskId, String riderId, DeliveryPickupInvitationStatus status) {
    return client
        .preparedQuery(
            "UPDATE " + PICKUP_TABLE_NAME + " SET status = $1 WHERE task_id = $2 AND rider_id = $3")
        .execute(Tuple.of(status.name(), taskId, riderId))
        .onItem()
        .ignore()
        .andContinueWithNull();
  }

  private Uni<List<String>> queryForRiders(PreparedQuery<RowSet<Row>> query, String taskId) {
    return query
        .execute(Tuple.of(taskId))
        .onItem()
        .transform(
            rows ->
                StreamSupport.stream(rows.spliterator(), false)
                    .map(row -> row.getString("rider_id"))
                    .collect(Collectors.toList()));
  }
}
