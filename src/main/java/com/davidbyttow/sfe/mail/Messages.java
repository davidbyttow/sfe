package com.davidbyttow.sfe.mail;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import javax.annotation.Nullable;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Messages {

  private static final Session session = Session.getDefaultInstance(System.getProperties());

  public static byte[] getRawMessage(Message message) throws IOException, MessagingException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    message.writeTo(output);
    return output.toByteArray();
  }

  public static String getRawMessageAsString(Message message) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      message.writeTo(output);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    return output.toString();
  }

  public static boolean isSameType(Message.RecipientType left, Message.RecipientType right) {
    return left.toString().equals(right.toString());
  }

  private static void addRecipients(Message message, Message.RecipientType type, List<Address> addresses) throws MessagingException {
    if (!addresses.isEmpty()) {
      message.addRecipients(type, addresses.toArray(new Address[0]));
    }
  }

  public static Address newAddress(String address, @Nullable String personalName) {
    try {
      return Strings.isNullOrEmpty(personalName)
        ? new InternetAddress(address) : new InternetAddress(address, personalName, Charsets.UTF_8.name());
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public static class Builder {
    private Map<String, String> headers = new HashMap<>();
    private Address from;
    private List<Address> to = new ArrayList<>();
    private List<Address> cc = new ArrayList<>();
    private List<Address> bcc = new ArrayList<>();
    private List<Address> replyTo = new ArrayList<>();
    private String subject = "";
    private String textContent = "";
    private String htmlContent = "";

    public Builder addHeader(String name, String value) {
      headers.put(name, value);
      return this;
    }

    public Builder withSubject(String subject) {
      this.subject = Preconditions.checkNotNull(subject);
      return this;
    }

    public Builder withTextContent(String content) {
      this.textContent = Preconditions.checkNotNull(content);
      return this;
    }

    public Builder withHtmlContent(String content) {
      this.htmlContent = Preconditions.checkNotNull(content);
      return this;
    }

    public Builder addTo(Address address) {
      to.add(address);
      return this;
    }

    public Builder addCc(Address address) {
      cc.add(address);
      return this;
    }

    public Builder addBcc(Address address) {
      bcc.add(address);
      return this;
    }

    public Builder addReplyTo(Address address) {
      replyTo.add(Preconditions.checkNotNull(address));
      return this;
    }

    public Builder addRecipients(Message.RecipientType type, Collection<Address> addresses) {
      addresses.forEach((a) -> addRecipient(type, a));
      return this;
    }

    public Builder addRecipient(Message.RecipientType type, Address address) {
      if (isSameType(type, Message.RecipientType.TO)) {
        return addTo(address);
      } else if (isSameType(type, Message.RecipientType.CC)) {
        return addCc(address);
      } else if (isSameType(type, Message.RecipientType.BCC)) {
        return addBcc(address);
      } else {
        throw new IllegalStateException("Unknown type: " + type);
      }
    }

    public Message build() throws MessagingException {
      Preconditions.checkNotNull(from);
      Preconditions.checkState(!to.isEmpty());

      MimeMessage message = new MimeMessage(session);
      headers.forEach((k, v) -> {
        try {
          message.setHeader(k, v);
        } catch (MessagingException e) {
          throw Throwables.propagate(e);
        }
      });
      message.setFrom(from);
      Messages.addRecipients(message, Message.RecipientType.TO, to);
      Messages.addRecipients(message, Message.RecipientType.CC, cc);
      Messages.addRecipients(message, Message.RecipientType.BCC, bcc);
      if (!replyTo.isEmpty()) {
        message.setReplyTo(replyTo.toArray(new Address[0]));
      }
      message.setSubject(subject);
      message.setContent(TextOrHtmlMultipart.build(textContent, htmlContent, Charsets.UTF_8.name()));
      return message;
    }

    private Builder() {}
  }

  public static Builder newBuilder(Address from) {
    Builder b = new Builder();
    b.from = Preconditions.checkNotNull(from);
    return b;
  }

  private Messages() {}
}
