package io.bold.sfe.testing;

import com.simplethingsllc.store.client.EntityKind;
import com.simplethingsllc.store.client.EntityStore;
import com.simplethingsllc.store.client.EntityStoreClient;
import com.simplethingsllc.store.client.EntityStoreFactory;
import com.simplethingsllc.store.client.EntityStoreModule;
import com.simplethingsllc.store.client.config.ClientConfig;
import io.bold.sfe.common.MoreReflections;
import io.bold.sfe.json.Json;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.io.IOException;

public final class EntityStores {
  public static EntityStore createEntityStore(DataSource dataSource, DBI dbi) throws IOException {
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.dataSource = dataSource;
    clientConfig.dbi = dbi;
    clientConfig.executorService = new ImmediateExecutor();
    clientConfig.objectMapper = Json.newObjectMapper();
    clientConfig.entityTypes = MoreReflections.getTypesAnnotatedWith("com.simplethingsllc.store", EntityKind.class);
    clientConfig.indexes = EntityStoreModule.loadIndexesFromJson("/test/db/indexes.json");
    EntityStoreClient client = EntityStoreFactory.createClient(clientConfig);
    return client.getStore();
  }
}
