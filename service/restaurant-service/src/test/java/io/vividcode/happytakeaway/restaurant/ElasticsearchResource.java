package io.vividcode.happytakeaway.restaurant;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.time.Duration;
import java.util.Map;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class ElasticsearchResource implements QuarkusTestResourceLifecycleManager {

  private ElasticsearchContainer elasticsearch;

  @Override
  public void init(Map<String, String> initArgs) {
    String version = initArgs.getOrDefault("version", "7.10.2");
    this.elasticsearch =
        new ElasticsearchContainer(
                DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch-oss")
                    .withTag(version))
            .withEnv("discovery.type", "single-node")
            .withStartupTimeout(Duration.ofMinutes(5));
  }

  @Override
  public Map<String, String> start() {
    this.elasticsearch.start();
    return Map.of(
        "quarkus.hibernate-search-orm.elasticsearch.hosts",
        this.elasticsearch.getHttpHostAddress());
  }

  @Override
  public void stop() {
    this.elasticsearch.stop();
  }
}
