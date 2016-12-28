package com.davidbyttow.sfe.storage;

import io.dropwizard.jdbi.ImmutableListContainerFactory;
import io.dropwizard.jdbi.ImmutableSetContainerFactory;
import io.dropwizard.jdbi.OptionalContainerFactory;
import io.dropwizard.jdbi.args.JodaDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.JodaDateTimeMapper;
import org.skife.jdbi.v2.DBI;

import java.util.Optional;
import java.util.TimeZone;

public final class DBIs {
  /** @return A configured DBI */
  public static DBI configure(DBI dbi) {
    dbi.registerContainerFactory(new ImmutableListContainerFactory());
    dbi.registerContainerFactory(new ImmutableSetContainerFactory());
    dbi.registerContainerFactory(new OptionalContainerFactory());
    dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory());
    dbi.registerColumnMapper(new JodaDateTimeMapper(Optional.<TimeZone>empty()));
    dbi.registerMapper(new BetterBeanMapper.Factory());
    return dbi;
  }

  private DBIs() {}
}
