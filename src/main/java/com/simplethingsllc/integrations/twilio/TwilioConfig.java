package com.simplethingsllc.integrations.twilio;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class TwilioConfig {
  @NotNull @NotEmpty public String accountSid;
  @NotNull @NotEmpty public String authToken;
  @Size(min = 1) public List<String> smsNumbers = new ArrayList<>();
}
