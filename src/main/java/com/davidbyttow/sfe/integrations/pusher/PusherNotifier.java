package com.davidbyttow.sfe.integrations.pusher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.davidbyttow.sfe.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class PusherNotifier {
  private static final Gson SERIALIZER = new GsonBuilder().create();

  private static final Logger log = LoggerFactory.getLogger(PusherNotifier.class);

  private static class PusherEvent {
    String name;
    String data;
    String channel;
    String socket_id;  // this is the socket id to "ignore"
  }

  private final PusherConnection pusherConnection;

  public PusherNotifier(PusherConnection pusherConnection) {
    this.pusherConnection = pusherConnection;
  }

  public void triggerEvent(String channel, String event, Object payload) {
    push(channel, event, payload, null);
  }

  public void triggerEvent(String channel, String event, Object payload, @Nullable String excludeSocketId) {
    push(channel, event, payload, excludeSocketId);
  }

  private void push(String channel, String event, Object payload, @Nullable String excludeSocketId) {
    String dataJson = Json.writeValueAsString(payload);

    PusherEvent e = new PusherEvent();
    e.name = event;
    e.data = dataJson;
    e.channel = channel;
    e.socket_id = excludeSocketId;

    String eventJson = SERIALIZER.toJson(e);

    pusherConnection.send(eventJson);
  }
}
