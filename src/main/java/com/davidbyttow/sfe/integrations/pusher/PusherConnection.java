package com.davidbyttow.sfe.integrations.pusher;

public interface PusherConnection {
  void send(String json);
}
