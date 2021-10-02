package io.vividcode.happytakeaway.delivery.service;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLResource implements QuarkusTestResourceLifecycleManager {

  private final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      DockerImageName.parse("postgres:12")
  );

  @Override
  public Map<String, String> start() {
    this.postgres.start();
    return Map.of(
        "quarkus.datasource.jdbc.url", this.postgres.getJdbcUrl(),
        "quarkus.datasource.reactive.url", this.postgres.getJdbcUrl().replaceFirst("^jdbc:", ""),
        "quarkus.datasource.username", this.postgres.getUsername(),
        "quarkus.datasource.password", this.postgres.getPassword()
    );
  }

  @Override
  public void stop() {
    this.postgres.stop();
  }
}