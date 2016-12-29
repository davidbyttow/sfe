package io.bold.sfe.integrations.pusher;

public interface PusherConnection {
  void send(String json);
}
