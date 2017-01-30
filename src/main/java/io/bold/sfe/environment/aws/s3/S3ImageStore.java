package io.bold.sfe.environment.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import io.bold.sfe.image.ImageStore;

import java.io.InputStream;
import java.util.Set;

public class S3ImageStore implements ImageStore {

  private static final Set<String> ALLOWED_CONTENT_TYPES = ImmutableSet.of(
      "image/jpeg", "image/png", "image/gif"
  );

  // TODO(d): Provide this.
  private static final String BUCKET_NAME = "bold-inc";

  private final AmazonS3Client client;

  @Inject S3ImageStore(AmazonS3Client client) {
    this.client = client;
  }

  @Override public void put(String key, String contentType, InputStream input, int contentLength) {
    put(BUCKET_NAME, key, contentType, input, contentLength);
  }

  @Override public void put(String bucket, String key, String contentType, InputStream input, int contentLength) {
    Preconditions.checkArgument(!key.startsWith("/"));
    Preconditions.checkArgument(ALLOWED_CONTENT_TYPES.contains(contentType));

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType(contentType);
    metadata.setContentLength(contentLength);

    PutObjectRequest put = new PutObjectRequest(BUCKET_NAME, key, input, metadata)
      .withCannedAcl(CannedAccessControlList.PublicRead);

    client.putObject(put);
  }

  @Override public Result get(String key) {
    GetObjectRequest get = new GetObjectRequest(BUCKET_NAME, key);
    S3Object object = client.getObject(get);
    if (object == null) {
      return null;
    }

    Result result = new Result();
    result.contentLength = object.getObjectMetadata().getContentLength();
    if (result.contentLength == 0) {
      return null;
    }
    result.contentType = object.getObjectMetadata().getContentType();
    if (!ALLOWED_CONTENT_TYPES.contains(result.contentType)) {
      return null;
    }

    result.inputStream = object.getObjectContent();
    return result;
  }
}
