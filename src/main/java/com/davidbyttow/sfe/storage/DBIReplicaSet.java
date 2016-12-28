package com.davidbyttow.sfe.storage;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.davidbyttow.sfe.common.MoreRandoms;
import org.skife.jdbi.v2.DBI;

import java.util.List;

public class DBIReplicaSet {
  private final List<DBI> replicas;

  DBIReplicaSet(List<DBI> replicas) {
    Preconditions.checkArgument(!replicas.isEmpty(), "replica set must not be empty");
    this.replicas = ImmutableList.copyOf(replicas);
  }

  public DBI pickRandom() {
    return MoreRandoms.nextIn(replicas);
  }
}
