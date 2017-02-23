package com.simplethingsllc.framework.integrations.twilio;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.bold.sfe.common.MoreRandoms;
import io.bold.sfe.concurrent.BackgroundThreadPool;
import io.bold.sfe.config.BasicServiceConfig;
import io.bold.sfe.inject.LazySingleton;

import javax.inject.Inject;

@LazySingleton
public class SmsSender {

  private final TwilioConfig config;
  private final ListeningExecutorService executorService;
  private boolean initialized;

  @Inject SmsSender(BasicServiceConfig config, @BackgroundThreadPool ListeningExecutorService executorService) {
    this.config = config.integrations.twilio;
    this.executorService = executorService;
  }

  public void send(String number, String message) {
    if (config == null) {
      return;
    }
    maybeInit();

    PhoneNumber to = new PhoneNumber(number);
    PhoneNumber from = new PhoneNumber(getSmsNumber());
    Message.creator(to, from, message).create();
  }

  private void maybeInit() {
    if (initialized) {
      return;
    }
    synchronized (this) {
      if (initialized) {
        return;
      }
      initialized = true;
      Twilio.init(config.accountSid, config.authToken);
      Twilio.setExecutorService(executorService);
      Twilio.getRestClient();
    }
  }

  private String getSmsNumber() {
    return MoreRandoms.nextIn(config.smsNumbers);
  }
}
