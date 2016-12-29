package io.bold.sfe.storage.entity.index;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import io.bold.sfe.json.Json;
import io.bold.sfe.storage.entity.EntityMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CompositeIndexes {

  public static String getIndexTableName(String kind, Collection<String> fieldNames) {
    return String.format("index_%s_%s", kind, getFieldNamesAsSortedString(fieldNames));
  }

  public static String getFieldNamesAsSortedString(Collection<String> fieldNames) {
    Set<String> unique = ImmutableSet.copyOf(fieldNames);
    String[] names = new String[unique.size()];
    names = unique.toArray(names);
    Arrays.sort(names);
    return Joiner.on('_').join(names);
  }

  public static EntityCompositeIndex findCompositeIndex(EntityMetadata metadata, Collection<String> fieldNames) {
    // TODO(d): We can potentially use "expanded" indexes to satisfy some constraints. For example,
    // if there's an index on properties {A, B, C} and we query {A, B}, then we can use {A, B, C} where C is any value.
    // Essentially implement a min-fit algorithm when matching an index.

    Map<String, EntityCompositeIndex> indexMap = metadata.getCompositeIndexMap();
    return indexMap.get(getFieldNamesAsSortedString(fieldNames));
  }

  // TODO(d): Probably move this JSON stuff elsewhere..
  public static class IndexDef {
    String kind;
    List<String> props = new ArrayList<>();
  }

  public static class IndexesDef {
    List<IndexDef> indexes = new ArrayList<>();
  }

  public static ListMultimap<String, CompositeIndexDef> loadIndexesFromJson(String indexesJson) {
    return loadIndexes(Json.readValue(indexesJson, IndexesDef.class));
  }

  public static ListMultimap<String, CompositeIndexDef> loadIndexes(IndexesDef indexesDef) {
    ListMultimap<String, CompositeIndexDef> map = ArrayListMultimap.create();
    for (IndexDef indexDef : indexesDef.indexes) {
      map.put(indexDef.kind, new CompositeIndexDef(indexDef.kind, indexDef.props));
    }
    return map;
  }

  private CompositeIndexes() {}
}
