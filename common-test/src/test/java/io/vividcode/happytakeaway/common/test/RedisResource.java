package io.vividcode.happytakeaway.common.test;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisResource implements QuarkusTestResourceLifecycleManager {

  private static final int redisPort = 6379;
  private static final String redisPassword = "password";

  private final GenericContainer<?> redis = new GenericContainer<>(
      DockerImageName.parse("bitnami/redis:6.2.1"))
      .withEnv("REDIS_PASSWORD", redisPassword);

  @Override
  public Map<String, String> start() {
    this.redis.start();
    return Map.of(
        "quarkus.redis.hosts", String.format("redis://:%s@%s:%d",
            redisPassword,
            this.redis.getHost(),
            this.redis.getMappedPort(redisPort))
    );
  }

  @Override
  public void stop() {
    this.redis.stop();
  }
}