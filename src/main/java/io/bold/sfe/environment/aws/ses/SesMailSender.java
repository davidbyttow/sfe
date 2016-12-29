package io.bold.sfe.environment.aws.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import io.bold.sfe.concurrent.BackgroundThreadPool;
import io.bold.sfe.mail.MailSender;
import io.bold.sfe.mail.Messages;
import io.bold.sfe.service.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import java.nio.ByteBuffer;

public class SesMailSender implements MailSender {

  private static final Logger log = LoggerFactory.getLogger(SesMailSender.class);

  private final Env env;
  private final AmazonSimpleEmailServiceClient client;
  private final ListeningExecutorService listeningExecutorService;

  @Inject SesMailSender(Env env,
                        AmazonSimpleEmailServiceClient client,
                        @BackgroundThreadPool ListeningExecutorService listeningExecutorService) {
    this.env = env;
    this.client = client;
    this.listeningExecutorService = listeningExecutorService;
  }

  @Override public void send(Message message) {
    Futures.getUnchecked(sendAsync(message));
  }

  @Override public ListenableFuture<?> sendAsync(Message message) {
    return listeningExecutorService.submit(() -> {
      ByteBuffer content = null;
      try {
        content = ByteBuffer.wrap(Messages.getRawMessage(message));
      } catch (Exception e) {
        throw Throwables.propagate(e);
      }
      SendRawEmailRequest request = new SendRawEmailRequest();
      request.setRawMessage(new RawMessage(content));
      try {
        log.info("Sending email {}", Messages.getRawMessageAsString(message));
        client.sendRawEmail(request);
      } catch (Exception e) {
        // TODO(d): Propagate this error, but just swallow and error for now.
        log.error(String.format("Failed to send email request %s", request), e);
      }
    });
  }
}
