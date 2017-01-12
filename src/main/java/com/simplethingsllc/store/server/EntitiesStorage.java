package com.simplethingsllc.store.server;

import io.bold.sfe.storage.DatabaseClock;
import io.bold.sfe.storage.StorageProvider;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

@StorageProvider
@UseStringTemplate3StatementLocator("/db/templates/entities.sql.stg")
public abstract class EntitiesStorage implements DatabaseClock {
  @SqlUpdate public abstract int create(@BindBean EntityData entity);

  @SqlQuery public abstract EntityData read(@Bind("kind") String kind, @Bind("id") String id);

  @SqlUpdate public abstract void update(@BindBean EntityData entity);

  @SqlUpdate public abstract int delete(@Bind("kind") String kind, @Bind("id") String id);

  @SqlQuery public abstract List<EntityData> scan(@Bind("kind") String kind, @Bind("limit") int limit);

  @SqlQuery public abstract List<EntityData> scanAfter(@Bind("kind") String kind,
                                                       @Bind("afterId") String afterId,
                                                       @Bind("limit") int limit);
}
