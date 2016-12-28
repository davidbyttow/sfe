package com.davidbyttow.sfe.mail;

import org.junit.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class MessagesTest {

  @Test public void textContent() throws MessagingException, IOException {
    Message message = Messages.newBuilder(Messages.newAddress("d@bold.io", "David Byttow"))
      .addHeader("X-Bold-Username", "d")
      .addTo(Messages.newAddress("b@bold.io", "Ben Lee"))
      .addCc(Messages.newAddress("hello@bold.io", "Bold"))
      .addReplyTo(Messages.newAddress("hello@bold.io", "Bold"))
      .withSubject("Subject")
      .withTextContent("Hello \"World\"")
      .build();

    String expected = "From: David Byttow <d@bold.io>\n" +
      "Reply-To: Bold <hello@bold.io>\n" +
      "To: Ben Lee <b@bold.io>\n" +
      "Cc: Bold <hello@bold.io>\n" +
      "Message-ID: <>\n" +
      "Subject: Subject\n" +
      "MIME-Version: 1.0\n" +
      "Content-Type: multipart/mixed; \n" +
      "\tboundary=\"----=_Part_0_2023938592\"\n" +
      "X-Bold-Username: d\n" +
      "\n" +
      "------=_Part_0_2023938592\n" +
      "Content-Type: text/plain; charset=UTF-8\n" +
      "Content-Transfer-Encoding: 7bit\n" +
      "\n" +
      "Hello \"World\"\n" +
      "------=_Part_0_2023938592--\n";

    checkMostlyEqual(Messages.getRawMessageAsString(message), expected);
  }

  @Test public void textAndHtmlContent() throws MessagingException, IOException {
    Message message = Messages.newBuilder(Messages.newAddress("d@bold.io", "David Byttow"))
      .addHeader("X-Bold-Username", "d")
      .addTo(Messages.newAddress("b@bold.io", "Ben Lee"))
      .addCc(Messages.newAddress("hello@bold.io", "Bold"))
      .addReplyTo(Messages.newAddress("hello@bold.io", "Bold"))
      .withSubject("Subject")
      .withHtmlContent("<div>Hello \"\uD83D\uDD2E\"</div>")
      .withTextContent("Hello \"World\"")
      .build();

    String expected = "From: David Byttow <d@bold.io>\n" +
      "Reply-To: Bold <hello@bold.io>\n" +
      "To: Ben Lee <b@bold.io>\n" +
      "Cc: Bold <hello@bold.io>\n" +
      "Message-ID: <>\n" +
      "Subject: Subject\n" +
      "MIME-Version: 1.0\n" +
      "Content-Type: multipart/alternative; \n" +
      "\tboundary=\"----=_Part_0_2023938592\"\n" +
      "X-Bold-Username: d\n" +
      "\n" +
      "------=_Part_0_2023938592\n" +
      "Content-Type: text/plain; charset=UTF-8\n" +
      "Content-Transfer-Encoding: 7bit\n" +
      "\n" +
      "Hello \"World\"\n" +
      "------=_Part_0_2023938592\n" +
      "Content-Type: text/html; charset=UTF-8\n" +
      "Content-Transfer-Encoding: quoted-printable\n" +
      "\n" +
      "<div>Hello \"=F0=9F=94=AE\"</div>\n" +
      "------=_Part_0_2023938592--\n";

    checkMostlyEqual(Messages.getRawMessageAsString(message), expected);
  }

  private static void checkMostlyEqual(String actual, String expected) {
    actual = actual.replaceAll("Message-ID: <.*>", "Message-ID: <>")
      .replaceAll("_Part[0-9_]+\\.[0-9]+", "_Part_0_2023938592")
      .replaceAll("\\r", "");
    assertThat(actual).isEqualTo(expected);
  }
}
