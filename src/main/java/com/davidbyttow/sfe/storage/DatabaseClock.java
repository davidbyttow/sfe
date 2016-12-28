package com.davidbyttow.sfe.storage;

import org.joda.time.DateTime;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

public interface DatabaseClock {
  /** @return The current timestamp returned by the database */
  @SqlQuery("select now()") DateTime nowAtDatabase();
}
