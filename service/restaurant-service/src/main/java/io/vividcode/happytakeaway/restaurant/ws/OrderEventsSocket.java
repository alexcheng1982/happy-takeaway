package io.vividcode.happytakeaway.restaurant.ws;

import io.vividcode.happytakeaway.common.json.JsonMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;

@ServerEndpoint("/{id}/orderEvents")
@Singleton
public class OrderEventsSocket {

  @Inject Logger logger;

  Map<String, CopyOnWriteArraySet<Session>> sessions = new ConcurrentHashMap<>();

  @OnOpen
  public void onOpen(Session session, @PathParam("id") String id) {
    this.logger.infov("Session open for restaurant {0}", id);
    this.sessions.compute(
        id,
        (key, existingSessions) -> {
          CopyOnWriteArraySet<Session> sessions =
              existingSessions != null ? existingSessions : new CopyOnWriteArraySet<>();
          sessions.add(session);
          return sessions;
        });
  }

  @OnClose
  public void onClose(Session session, @PathParam("id") String id) {
    this.logger.infov("Session close for restaurant {0}", id);
    this.removeSession(id, session);
  }

  @OnError
  public void onError(Session session, @PathParam("id") String id, Throwable throwable) {
    this.logger.warnv(throwable, "Session error for restaurant {0}", id);
    this.removeSession(id, session);
  }

  private void removeSession(String id, Session session) {
    this.sessions.computeIfPresent(
        id,
        (key, sessions) -> {
          sessions.remove(session);
          return sessions;
        });
  }

  public void sendEvent(String restaurantId, Object event) {
    CopyOnWriteArraySet<Session> sessions = this.sessions.get(restaurantId);
    if (sessions != null) {
      sessions.forEach(
          session ->
              session
                  .getAsyncRemote()
                  .sendText(
                      JsonMapper.toJson(event),
                      result -> {
                        if (!result.isOK()) {
                          this.logger.warnv(result.getException(), "Failed to send message");
                        }
                      }));
    }
  }
}
