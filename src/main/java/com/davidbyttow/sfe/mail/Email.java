package com.davidbyttow.sfe.mail;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class Email {

  private final String fromEmail;
  private final Set<String> recipients;
  private final String subject;
  private final String textBody;
  private Set<String> ccList = new HashSet<>();
  private String htmlBody;

  public Email(String fromEmail, Set<String> toEmails, String subject, String textBody) {
    this.fromEmail = fromEmail;
    this.recipients = ImmutableSet.copyOf(toEmails);
    this.subject = subject;
    this.textBody = textBody;
  }

  public String getTextBody() {
    return textBody;
  }

  public String getSubject() {
    return subject;
  }

  public String getFromEmail() {
    return fromEmail;
  }

  public Set<String> getRecipients() {
    return recipients;
  }

  public Set<String> getCcList() {
    return ccList;
  }

  public void setCcList(Set<String> ccList) {
    this.ccList = ImmutableSet.copyOf(ccList);
  }

  public boolean hasHtmlBody() {
    return !Strings.isNullOrEmpty(htmlBody);
  }

  public String getHtmlBody() {
    return htmlBody;
  }

  public void setHtmlBody(String htmlBody) {
    this.htmlBody = htmlBody;
  }

  @Override public String toString() {
    return String.format("From=%s To=%s CC=%s Subject=%s Body=%s", fromEmail, recipients, ccList, subject, textBody);
  }
}
