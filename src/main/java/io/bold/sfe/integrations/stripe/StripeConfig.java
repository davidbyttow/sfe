package io.bold.sfe.integrations.stripe;

import javax.validation.constraints.NotNull;

public class StripeConfig {
  @NotNull public String secretKey;
  @NotNull public String publicKey;
}
