package com.davidbyttow.sfe.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class TextOrHtmlMultipart extends MimeMultipart {

  public static TextOrHtmlMultipart build(String text, String html, String charset) throws MessagingException {
    MimeBodyPart textPart = null;
    if (!text.isEmpty()) {
      textPart = new MimeBodyPart();
      textPart.setText(text, charset);
    }

    MimeBodyPart htmlPart = null;
    if (!html.isEmpty()) {
      htmlPart = new MimeBodyPart();
      htmlPart.setText(html, charset, "html");
    }

    TextOrHtmlMultipart multipart = new TextOrHtmlMultipart(
      (textPart != null && htmlPart != null) ? "alternative" : "mixed");

    if (textPart != null) {
      multipart.addBodyPart(textPart);
    }
    if (htmlPart != null) {
      multipart.addBodyPart(htmlPart);
    }

    return multipart;
  }

  private TextOrHtmlMultipart(String subType) {
    super(subType);
  }
}
