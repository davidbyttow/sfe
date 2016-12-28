package com.davidbyttow.sfe.mail;

import com.google.common.util.concurrent.ListenableFuture;

import javax.mail.Message;

public interface MailSender {
  void send(Message message);

  ListenableFuture<?> sendAsync(Message message);
}
