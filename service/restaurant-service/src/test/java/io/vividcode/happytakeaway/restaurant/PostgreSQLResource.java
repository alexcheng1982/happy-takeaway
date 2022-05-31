package io.vividcode.happytakeaway.restaurant;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.time.Duration;
import java.util.Map;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgreSQLResource implements QuarkusTestResourceLifecycleManager {

  private final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:12"))
          .withStartupTimeout(Duration.ofMinutes(5));

  @Override
  public Map<String, String> start() {
    this.postgres.start();
    return Map.of(
        "quarkus.datasource.jdbc.url", this.postgres.getJdbcUrl(),
        "quarkus.datasource.username", this.postgres.getUsername(),
        "quarkus.datasource.password", this.postgres.getPassword());
  }

  @Override
  public void stop() {}
}
