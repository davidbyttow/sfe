package com.simplethingsllc.store;

import com.simplethingsllc.store.common.Jsons;
import com.simplethingsllc.store.server.EntitiesStorage;
import com.simplethingsllc.store.server.EntityData;
import io.bold.sfe.testing.MysqlResource;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class EntitiesStorageTest {
  @Rule public final MysqlResource mysql = new MysqlResource("test_ebdb", "/db/migration");

  public EntitiesStorage storage() {
    return mysql.dbi().onDemand(EntitiesStorage.class);
  }

  @Test public void storeEntityData() throws Exception {
    EntitiesStorage es = storage();

    EntityData entity = new EntityData();
    entity.setKind("Domain");
    entity.setId("101");
    entity.setJsonData(Jsons.toBlob("{}"));
    es.create(entity);

    entity = es.read("Domain", "101");

    DateTime createdAt = entity.getCreatedAt();

    assertThat(entity.getId()).isEqualTo("101");
    assertThat(entity.getKind()).isEqualTo("Domain");
    assertThat(Jsons.fromBlob(entity.getJsonData())).isEqualTo("{}");
    assertThat(createdAt).isEqualTo(entity.getUpdatedAt());

    entity.setJsonData(Jsons.toBlob("{\"foo\": 42}"));
    entity.setUpdatedAt(DateTime.now());
    es.update(entity);

    assertThat(entity.getUpdatedAt().isAfter(createdAt)).isTrue();

    es.delete("Domain", "101");

    entity = es.read("Domain", "101");
    assertThat(entity).isNull();
  }
}
