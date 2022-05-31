package io.vividcode.happytakeaway.delivery.service;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;
import java.util.HashMap;
import java.util.Map;

public class InMemoryConnectorResource implements QuarkusTestResourceLifecycleManager {

  @Override
  public Map<String, String> start() {
    Map<String, String> env = new HashMap<>();
    Map<String, String> connector1 =
        InMemoryConnector.switchIncomingChannelsToInMemory("delivery-pickup-invitation-accepted");
    Map<String, String> connector2 =
        InMemoryConnector.switchOutgoingChannelsToInMemory("delivery-pickup-invitation");
    env.putAll(connector1);
    env.putAll(connector2);
    return env;
  }

  @Override
  public void stop() {
    InMemoryConnector.clear();
  }
}
