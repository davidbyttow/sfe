package io.bold.sfe.common;

import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

public class MoreReflectionsTest {

  static class Foo {
    List<Integer> list;
    Set<Integer> set;
    Collection<Integer> collection;
    Integer[] array;
    boolean bool;
  }

  @Test public void testGenerics() throws NoSuchFieldException {
    assertThat(MoreReflections.getFieldCollectionType(Foo.class.getDeclaredField("list"))).isEqualTo(Integer.class);
    assertThat(MoreReflections.getFieldCollectionType(Foo.class.getDeclaredField("set"))).isEqualTo(Integer.class);
    assertThat(MoreReflections.getFieldCollectionType(Foo.class.getDeclaredField("collection"))).isEqualTo(Integer.class);
    assertThat(MoreReflections.getFieldCollectionType(Foo.class.getDeclaredField("array"))).isEqualTo(Integer.class);
    assertThat(MoreReflections.getFieldCollectionType(Foo.class.getDeclaredField("bool"))).isNull();
  }
}
