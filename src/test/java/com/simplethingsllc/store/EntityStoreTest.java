package com.simplethingsllc.store;

import com.google.common.collect.ImmutableList;
import com.simplethingsllc.store.client.EntityId;
import com.simplethingsllc.store.client.EntityKind;
import com.simplethingsllc.store.client.EntityStore;
import com.simplethingsllc.store.client.IdType;
import com.simplethingsllc.store.client.Ignored;
import com.simplethingsllc.store.client.Indexed;
import com.simplethingsllc.store.client.Query;
import com.simplethingsllc.store.server.PageResults;
import io.bold.sfe.common.Times;
import io.bold.sfe.testing.EntityStores;
import io.bold.sfe.testing.MysqlResource;
import org.fest.assertions.Assertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class EntityStoreTest {

  @Rule public final MysqlResource mysql = new MysqlResource("test_ebdb", "/db/migration");

  /** Simple data type */
  @EntityKind("Pod")
  static class Pod {
    @EntityId private String id;

    @Indexed private int intValue;
    @Indexed private float floatValue;
    @Indexed private DateTime dateTimeValue;

    // Unindexed properties.
    private boolean boolValue;
    private String stringValue;

    // Never serialized.
    @Ignored private int ignored = 100;
  }

  public enum Type {
    Type1,
    Type2,
  }

  /** A more complicated object */
  @EntityKind("Obj")
  static class Obj {

    static class InnerObj {
      private List<String> moreStrings;
    }

    /** Synthesized id */
    @EntityId private String id() {
      return newId(id1, id2);
    }

    @Indexed @IdType private String secondaryId;
    @Indexed private Type indexedType;

    private Type unindexedType;
    private String id1;
    private String id2;
    private List<String> stringList;
    private InnerObj inner;


    static String newId(String id1, String id2) {
      return id1 + ":" + id2;
    }
  }

  @EntityKind("Listed")
  static class Listed {
    @EntityId String id;
    @Indexed List<String> stringList;
  }

  private EntityStore createStore() throws IOException {
    return EntityStores.createEntityStore(mysql.dataSource(), mysql.dbi());
  }

  @Test public void missingReturnsNull() throws Exception {
    EntityStore store = createStore();

    Assertions.assertThat(store.get("asdfasdfasdf", Pod.class)).isNull();
    List<Pod> pods = store.getMany(ImmutableList.of("asdfasdfasdf", "jk;ljkl;"), Pod.class);
    assertThat(pods).hasSize(2);
    assertThat(pods.get(0)).isNull();
    assertThat(pods.get(1)).isNull();
  }

  @Test public void putAndGet() throws Exception {
    EntityStore store = createStore();

    DateTime now = new DateTime(0, DateTimeZone.UTC);

    Pod pod = new Pod();
    pod.id = "var-id-1";
    pod.boolValue = true;
    pod.intValue = 50;
    pod.dateTimeValue = now;
    store.put(pod);

    pod = store.get("var-id-1", Pod.class);
    assertThat(pod.boolValue).isEqualTo(true);
    assertThat(pod.intValue).isEqualTo(50);
    assertThat(pod.dateTimeValue).isEqualTo(now);

    pod = new Pod();
    pod.id = "var-id-1";
    pod.boolValue = true;
    pod.intValue = 51;
    pod.stringValue = "hello";
    store.put(pod);

    pod = store.get("var-id-1", Pod.class);
    assertThat(pod.boolValue).isEqualTo(true);
    assertThat(pod.intValue).isEqualTo(51);
    assertThat(pod.stringValue).isEqualTo("hello");
  }

  @Test public void putWithSynthesizedKey() throws Exception {
    EntityStore store = createStore();

    Obj obj = new Obj();
    obj.id1 = "foo";
    obj.id2 = "bar";
    store.put(obj);

    String id = Obj.newId("foo", "bar");
    obj = store.get(id, Obj.class);
    assertThat(obj.id1).isEqualTo("foo");
    assertThat(obj.id2).isEqualTo("bar");
    assertThat(obj.stringList).isNull();
    assertThat(obj.id()).isEqualTo(id);
  }

  @Test public void putAndGetComplexObject() throws Exception {
    EntityStore store = createStore();

    Pod pod = new Pod();
    pod.id = "var-id-1";
    pod.boolValue = true;
    pod.intValue = 42;
    pod.floatValue = 42.24f;
    pod.stringValue = "Hello, world.";
    pod.ignored = 200;
    store.put(pod);

    pod = store.get("var-id-1", Pod.class);
    assertThat(pod.boolValue).isEqualTo(true);
    assertThat(pod.intValue).isEqualTo(42);
    assertThat(pod.floatValue).isEqualTo(42.24f);
    assertThat(pod.stringValue).isEqualTo("Hello, world.");
    assertThat(pod.ignored).isEqualTo(100);

    Obj obj = new Obj();
    obj.id1 = "1";
    obj.id2 = "2";
    obj.stringList = ImmutableList.of("1", "2", "3");

    store.put(obj);

    obj = store.get("1:2", Obj.class);
    assertThat(obj.id1).isEqualTo("1");
    assertThat(obj.id2).isEqualTo("2");
    assertThat(obj.stringList).containsExactly("1", "2", "3");
  }

  @Test public void putIndexedLists() throws Exception {
    EntityStore store = createStore();
    Listed listed = new Listed();
    listed.id = "1";
    listed.stringList = new ArrayList<>();
    listed.stringList.add("hello");
    listed.stringList.add("world");
    store.put(listed);

    listed = store.get("1", Listed.class);
    assertThat(listed).isNotNull();
    assertThat(listed.stringList).containsExactly("hello", "world");

    // Check for just hello
    Query<Listed> q = Query.newBuilder(Listed.class)
      .addFilter("stringList", Query.Equality.Equals, "hello")
      .build();

    List<Listed> results = store.fetch(q);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).id).isEqualTo("1");

    // Check for world
    q = Query.newBuilder(Listed.class)
      .addFilter("stringList", Query.Equality.Equals, "world")
      .build();

    results = store.fetch(q);
    assertThat(results.size()).isEqualTo(1);
    assertThat(results.get(0).id).isEqualTo("1");

    // Remove world
    listed.stringList.remove(1);
    store.put(listed);

    // Check for world
    q = Query.newBuilder(Listed.class)
      .addFilter("stringList", Query.Equality.Equals, "world")
      .build();

    results = store.fetch(q);
    assertThat(results.size()).isEqualTo(0);
  }

  @Test public void putNestedObject() throws Exception {
    EntityStore store = createStore();

    Obj obj = new Obj();
    obj.id1 = "1";
    obj.id2 = "2";
    obj.unindexedType = Type.Type2;
    obj.inner = new Obj.InnerObj();
    obj.inner.moreStrings = ImmutableList.of("4", "5", "6");

    store.put(obj);

    obj = store.get("1:2", Obj.class);
    assertThat(obj.unindexedType).isEqualTo(Type.Type2);
    assertThat(obj.inner).isNotNull();
    assertThat(obj.inner.moreStrings).containsExactly("4", "5", "6");
  }

  @Test public void batchGet() throws Exception {
    EntityStore store = createStore();

    Pod pod = new Pod();
    pod.id = "var-id-1";
    pod.boolValue = true;
    pod.intValue = 42;
    pod.floatValue = 42.24f;
    pod.stringValue = "Hello, world.";
    pod.ignored = 200;
    store.put(pod);

    Pod pod2 = new Pod();
    pod2.id = "var-id-2";
    pod2.boolValue = false;
    pod2.intValue = 40;
    pod2.floatValue = 500;
    pod2.stringValue = "Hello, world 2.";
    store.put(pod2);

    List<Pod> pods = store.getMany(ImmutableList.of("var-id-2", "var-id-1"), Pod.class);
    assertThat(pods).hasSize(2);
    assertThat(pods.get(0).id).isEqualTo("var-id-2");
    assertThat(pods.get(1).id).isEqualTo("var-id-1");
  }

  @Test public void delete() throws Exception {
    EntityStore store = createStore();

    Pod pod = new Pod();
    pod.id = "var-id-1";
    pod.boolValue = true;
    pod.intValue = 42;
    pod.floatValue = 42.24f;
    pod.stringValue = "Hello, world.";
    pod.ignored = 200;
    store.put(pod);

    pod = store.get("var-id-1", Pod.class);
    assertThat(pod).isNotNull();

    store.delete("var-id-1", Pod.class);

    pod = store.get("var-id-1", Pod.class);
    assertThat(pod).isNull();

    pod = new Pod();
    pod.id = "var-id-1";
    store.put(pod);

    pod = store.get("var-id-1", Pod.class);
    assertThat(pod.boolValue).isFalse();
  }

  @Test public void querySingleProperty() throws Exception {
    EntityStore store = createStore();

    DateTime past = DateTime.now().minusMonths(1);
    DateTime present = DateTime.now();
    DateTime future = DateTime.now().plusMonths(1);

    Pod pod = new Pod();
    pod.id = "var-id-1";
    pod.intValue = 1;
    pod.dateTimeValue = present;
    store.put(pod);

    Pod pod2 = new Pod();
    pod2.id = "var-id-2";
    pod2.intValue = 2;
    pod2.dateTimeValue = past;
    store.put(pod2);

    Pod pod3 = new Pod();
    pod3.id = "var-id-3";
    pod3.intValue = 3;
    pod3.dateTimeValue = future;
    store.put(pod3);

    Query<Pod> q = Query.newBuilder(Pod.class)
      .build();
    List<Pod> results = store.fetch(q);
    assertThat(results).hasSize(3);

    Assertions.assertThat(store.count(q)).isEqualTo(3);

    q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.LessThanOrEqualTo, 2)
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(2);

    pod2.intValue = 4;
    store.put(pod2);

    q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.LessThanOrEqualTo, 2)
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(1);
  }

  @Test public void queryMultiFilterOrderedById() throws Exception {
    EntityStore store = createStore();

    Pod pod = new Pod();
    pod.id = "var-id-3";
    pod.intValue = 5;
    store.put(pod);

    Pod pod2 = new Pod();
    pod2.id = "var-id-2";
    pod2.intValue = 1;
    store.put(pod2);

    Pod pod3 = new Pod();
    pod3.id = "var-id-1";
    pod3.intValue = 10;
    store.put(pod3);

    Query<Pod> q = Query.newBuilder(Pod.class)
      .build();
    List<Pod> results = store.fetch(q);
    assertThat(results).hasSize(3);
    Assertions.assertThat(store.count(q)).isEqualTo(3);

    q = Query.newBuilder(Pod.class)
      .addFilter("intValue", Query.Equality.GreaterThan, 1)
      .addFilter("intValue", Query.Equality.LessThanOrEqualTo, 10)
      .build();
    results = store.fetch(q);
    assertThat(results).hasSize(2);

    assertThat(results.get(0).intValue).isEqualTo(10);
    assertThat(results.get(1).intValue).isEqualTo(5);
  }

  @Test public void queryOrderedProperty() throws Exception {
    EntityStore store = createStore();

    DateTime past = DateTime.now().minusMonths(1);
    DateTime present = DateTime.now();
    DateTime future = DateTime.now().plusMonths(1);

    Pod pod = new Pod();
    pod.id = "var-id-1";
    pod.intValue = 2;
    pod.dateTimeValue = present;
    store.put(pod);

    Pod pod2 = new Pod();
    pod2.id = "var-id-2";
    pod2.intValue = 3;
    pod2.dateTimeValue = past;
    store.put(pod2);

    Pod pod3 = new Pod();
    pod3.id = "var-id-3";
    pod3.dateTimeValue = future;
    store.put(pod3);

    Query<Pod> q = Query.newBuilder(Pod.class)
        .orderBy("dateTimeValue")
        .build();
    List<Pod> results = store.fetch(q);
    assertThat(results).hasSize(3);
    assertThat(results.get(0).id).isEqualTo("var-id-2");
    assertThat(results.get(1).id).isEqualTo("var-id-1");
    assertThat(results.get(2).id).isEqualTo("var-id-3");

    q = Query.newBuilder(Pod.class)
        .orderByDescending("dateTimeValue")
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(3);
    assertThat(results.get(0).id).isEqualTo("var-id-3");
    assertThat(results.get(1).id).isEqualTo("var-id-1");
    assertThat(results.get(2).id).isEqualTo("var-id-2");

    q = Query.newBuilder(Pod.class)
        .orderByDescending("dateTimeValue")
        .limit(1)
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(1);
    assertThat(results.get(0).id).isEqualTo("var-id-3");
  }

  @Test public void queryEnumTypes() throws Exception {
    EntityStore store = createStore();

    Obj obj = new Obj();
    obj.id1 = "1";
    obj.id2 = "2";
    obj.indexedType = Type.Type2;
    store.put(obj);

    obj = new Obj();
    obj.id1 = "2";
    obj.id2 = "1";
    obj.indexedType = Type.Type1;
    store.put(obj);

    obj = new Obj();
    obj.id1 = "3";
    obj.id2 = "2";
    obj.indexedType = Type.Type2;
    store.put(obj);

    Query<Obj> q = Query.newBuilder(Obj.class)
        .addFilter("indexedType", Query.Equality.Equals, Type.Type2)
        .build();
    List<Obj> results = store.fetch(q);
    assertThat(results).hasSize(2);
    assertThat(results.get(0).id()).isEqualTo("1:2");
    assertThat(results.get(1).id()).isEqualTo("3:2");
  }

  @Test public void queryCompositeIndex() throws Exception {
    EntityStore store = createStore();

    Pod p1 = new Pod();
    p1.id = "1";
    p1.intValue = 1;
    p1.floatValue = 100.1f;
    p1.dateTimeValue = Times.nowUtc();
    store.put(p1);

    Pod p2 = new Pod();
    p2.id = "2";
    p2.intValue = 2;
    p2.floatValue = 100.2f;
    p2.dateTimeValue = Times.nowUtc().plus(2000);
    store.put(p2);

    Pod p3 = new Pod();
    p3.id = "3";
    p3.intValue = 3;
    p3.floatValue = 100.3f;
    p3.dateTimeValue = Times.nowUtc().plus(3000);
    store.put(p3);

    Query<Pod> q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.GreaterThan, 1)
        .build();
    List<Pod> results = store.fetch(q);
    assertThat(results).hasSize(2);

    q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.GreaterThan, 1)
        .addFilter("floatValue", Query.Equality.GreaterThan, 100.f)
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(2);

    p3.intValue = 0;
    store.put(p3);

    // TODO(d): Figure out why this is flaky with dates.
//    q = Query.newBuilder(Pod.class)
//        .addFilter("intValue", Query.Equality.GreaterThan, -1)
//        .addFilter("dateTimeValue", Query.Equality.GreaterThan, p1.dateTimeValue)
//        .build();
//    results = store.fetch(q);
//    assertThat(results).hasSize(2);

    q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.GreaterThan, 1)
        .addFilter("floatValue", Query.Equality.GreaterThan, 100.f)
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(1);

    store.delete(p1);
    store.delete(p2);
    store.delete(p3);

    q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.GreaterThan, 1)
        .addFilter("floatValue", Query.Equality.GreaterThan, 100.f)
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(0);
  }

  @Test public void pagination() throws Exception {
    EntityStore store = createStore();

    Pod p1 = new Pod();
    p1.id = "3";
    p1.intValue = 1;
    store.put(p1);

    Pod p2 = new Pod();
    p2.id = "2";
    p2.intValue = 2;
    store.put(p2);

    Pod p3 = new Pod();
    p3.id = "1";
    p3.intValue = 3;
    store.put(p3);

    Query<Pod> q = Query.newBuilder(Pod.class)
      .limit(2)
      .build();
    PageResults<Pod> page = store.fetchPage(q);
    assertThat(page.getEntities().size() == 2);
    assertThat(page.hasMore()).isTrue();

    page = store.fetchNext(page);
    assertThat(page.getEntities().size() == 1);
    assertThat(page.hasMore()).isFalse();
  }

  @Test public void queryCompositeIndexWithOrdering() throws Exception {
    EntityStore store = createStore();

    Pod p1 = new Pod();
    p1.id = "1";
    p1.intValue = 1;
    p1.dateTimeValue = Times.nowUtc();
    store.put(p1);

    Pod p2 = new Pod();
    p2.id = "2";
    p2.intValue = 2;
    p2.dateTimeValue = Times.nowUtc().plus(2000);
    store.put(p2);

    Pod p3 = new Pod();
    p3.id = "3";
    p3.intValue = 3;
    p3.dateTimeValue = Times.nowUtc().plus(3000);
    store.put(p3);

    Query<Pod> q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.GreaterThanOrEqualTo, 1)
        .orderByDescending("dateTimeValue")
        .build();
    List<Pod> results = store.fetch(q);
    assertThat(results).hasSize(3);

    assertThat(results.get(0).id.equals("3"));
    assertThat(results.get(1).id.equals("2"));
    assertThat(results.get(2).id.equals("1"));

    q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.GreaterThanOrEqualTo, 1)
        .orderBy("dateTimeValue")
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(3);

    assertThat(results.get(0).id.equals("1"));
    assertThat(results.get(1).id.equals("2"));
    assertThat(results.get(2).id.equals("3"));

    q = Query.newBuilder(Pod.class)
        .addFilter("intValue", Query.Equality.GreaterThanOrEqualTo, 1)
        .addFilter("floatValue", Query.Equality.LessThan, 200.f)
        .orderBy("dateTimeValue")
        .build();
    results = store.fetch(q);
    assertThat(results).hasSize(3);

    assertThat(results.get(0).id.equals("1"));
    assertThat(results.get(1).id.equals("2"));
    assertThat(results.get(2).id.equals("3"));
  }

  @Test public void orderedTimestamps() throws Exception {
    EntityStore store = createStore();

    Pod p1 = new Pod();
    p1.id = "1";
    p1.intValue = 1;
    p1.floatValue = 100.1f;
    p1.dateTimeValue = Times.nowUtc();
    store.put(p1);

    Pod p2 = new Pod();
    p2.id = "2";
    p2.intValue = 2;
    p2.floatValue = 100.2f;
    p2.dateTimeValue = Times.nowUtc().plus(2000);
    store.put(p2);

    Pod p3 = new Pod();
    p3.id = "3";
    p3.intValue = 3;
    p3.floatValue = 100.3f;
    p3.dateTimeValue = Times.nowUtc().plus(3000);
    store.put(p3);

    Query<Pod> q = Query.newBuilder(Pod.class)
      .addFilter("intValue", Query.Equality.GreaterThan, 1)
      .build();
    List<Pod> results = store.fetch(q);
    assertThat(results).hasSize(2);

    q = Query.newBuilder(Pod.class)
      .addFilter("intValue", Query.Equality.GreaterThan, 1)
      .addFilter("floatValue", Query.Equality.GreaterThan, 100.f)
      .build();
    results = store.fetch(q);
    assertThat(results).hasSize(2);

    p3.intValue = 0;
    store.put(p3);

    // TODO(d): Figure out why this is flaky with dates.
//    q = Query.newBuilder(Pod.class)
//        .addFilter("intValue", Query.Equality.GreaterThan, -1)
//        .addFilter("dateTimeValue", Query.Equality.GreaterThan, p1.dateTimeValue)
//        .build();
//    results = store.fetch(q);
//    assertThat(results).hasSize(2);

  }
}
