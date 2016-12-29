package io.bold.sfe.integrations.sentry;

import javax.validation.constraints.NotNull;

public class SentryConfig {
  @NotNull public String publicDsn;
  @NotNull public String privateDsn;
}
