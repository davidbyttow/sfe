package io.bold.sfe.environment.aws;

import com.amazonaws.auth.AWSCredentials;
import org.hibernate.validator.constraints.NotEmpty;

public class AwsCredentialsConfig implements AWSCredentials {
  @NotEmpty private String accessKeyId;
  @NotEmpty private String secretAccessKey;

  @Override public String getAWSAccessKeyId() {
    return accessKeyId;
  }

  @Override public String getAWSSecretKey() {
    return secretAccessKey;
  }

  public void setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
  }

  public void setSecretAccessKey(String secretAccessKey) {
    this.secretAccessKey = secretAccessKey;
  }
}
