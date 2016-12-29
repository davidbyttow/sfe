package io.bold.sfe.storage.entity;

import org.joda.time.DateTime;

public class EntityData {
  private String kind;
  private String id;
  private byte[] jsonData = Jsons.defaultValue();
  private byte[] jsonMetadata = Jsons.defaultValue();
  private DateTime createdAt;
  private DateTime updatedAt;

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public byte[] getJsonData() {
    return jsonData;
  }

  public void setJsonData(byte[] jsonData) {
    this.jsonData = jsonData;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
  }

  public byte[] getJsonMetadata() {
    return jsonMetadata;
  }

  public void setJsonMetadata(byte[] jsonMetadata) {
    this.jsonMetadata = jsonMetadata;
  }

  public DateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(DateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
