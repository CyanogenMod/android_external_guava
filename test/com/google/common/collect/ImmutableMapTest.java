/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.testing.CollectionTestSuiteBuilder;
import com.google.common.collect.testing.MapInterfaceTest;
import com.google.common.collect.testing.MinimalSet;
import com.google.common.collect.testing.ReserializingTestCollectionGenerator;
import com.google.common.collect.testing.ReserializingTestSetGenerator;
import com.google.common.collect.testing.SampleElements;
import com.google.common.collect.testing.SampleElements.Colliders;
import com.google.common.collect.testing.SampleElements.Unhashables;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestCollectionGenerator;
import com.google.common.collect.testing.TestMapEntrySetGenerator;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.TestUnhashableCollectionGenerator;
import com.google.common.collect.testing.UnhashableObject;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;
import com.google.common.testutils.NullPointerTester;
import com.google.common.testutils.SerializableTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Tests for {@link ImmutableMap}.
 *
 * @author Kevin Bourrillion
 * @author Jesse Wilson
 */
public class ImmutableMapTest extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(ImmutableMapTest.class);

    suite.addTest(SetTestSuiteBuilder.using(keySetGenerator())
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableMap.keySet")
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(entrySetGenerator())
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableMap.entrySet")
        .createTestSuite());

    suite.addTest(CollectionTestSuiteBuilder.using(valuesGenerator())
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .named("ImmutableMap.values")
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        ReserializingTestSetGenerator.newInstance(keySetGenerator()))
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableMap.keySet, reserialized")
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        ReserializingTestSetGenerator.newInstance(entrySetGenerator()))
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableMap.entrySet, reserialized")
        .createTestSuite());

    suite.addTest(CollectionTestSuiteBuilder.using(
        ReserializingTestCollectionGenerator.newInstance(valuesGenerator()))
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .named("ImmutableMap.values, reserialized")
        .createTestSuite());

    suite.addTest(CollectionTestSuiteBuilder.using(unhashableValuesGenerator())
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .named("ImmutableMap.values, unhashable")
        .createTestSuite());

    return suite;
  }

  static TestMapEntrySetGenerator<String, String> entrySetGenerator() {
    SampleElements.Strings sampleStrings = new SampleElements.Strings();
    return new TestMapEntrySetGenerator<String, String>(
        sampleStrings, sampleStrings) {
      @Override public Set<Entry<String, String>> createFromEntries(
          Entry<String, String>[] entries) {
        Builder<String, String> builder = ImmutableMap.builder();
        for (Entry<String, String> entry : entries) {
          builder.put(entry.getKey(), entry.getValue());
        }
        return builder.build().entrySet();
      }
    };
  }

  static TestStringSetGenerator keySetGenerator() {
    return new TestStringSetGenerator() {
      @Override protected Set<String> create(String[] elements) {
        Builder<String, Integer> builder = ImmutableMap.builder();
        for (String key : elements) {
          builder.put(key, key.length());
        }
        return builder.build().keySet();
      }
    };
  }

  static TestCollectionGenerator<String> valuesGenerator() {
    return new TestCollectionGenerator<String>() {
      public SampleElements<String> samples() {
        return new SampleElements.Strings();
      }

      public Collection<String> create(Object... elements) {
        Builder<Object, String> builder = ImmutableMap.builder();
        for (Object key : elements) {
          builder.put(key, key.toString());
        }
        return builder.build().values();
      }

      public String[] createArray(int length) {
        return new String[length];
      }

      public List<String> order(List<String> insertionOrder) {
        return insertionOrder;
      }
    };
  }

  static TestCollectionGenerator<UnhashableObject> unhashableValuesGenerator() {
    return
        new TestUnhashableCollectionGenerator<Collection<UnhashableObject>>() {
          @Override public Collection<UnhashableObject> create(
              UnhashableObject[] elements) {
            Builder<Integer, UnhashableObject> builder = ImmutableMap.builder();
            int key = 1;
            for (UnhashableObject value : elements) {
              builder.put(key++, value);
            }
            return builder.build().values();
          }
    };
  }

  public abstract static class AbstractMapTests<K, V>
      extends MapInterfaceTest<K, V> {
    public AbstractMapTests() {
      super(false, false, false, false, false);
    }

    @Override protected Map<K, V> makeEmptyMap() {
      throw new UnsupportedOperationException();
    }

    private static final Joiner joiner = Joiner.on(", ");

    @Override protected void assertMoreInvariants(Map<K, V> map) {
      // TODO: can these be moved to MapInterfaceTest?
      for (Entry<K, V> entry : map.entrySet()) {
        assertEquals(entry.getKey() + "=" + entry.getValue(),
            entry.toString());
      }

      assertEquals("{" + joiner.join(map.entrySet()) + "}",
          map.toString());
      assertEquals("[" + joiner.join(map.entrySet()) + "]",
          map.entrySet().toString());
      assertEquals("[" + joiner.join(map.keySet()) + "]",
          map.keySet().toString());
      assertEquals("[" + joiner.join(map.values()) + "]",
          map.values().toString());

      assertEquals(MinimalSet.from(map.entrySet()), map.entrySet());
      assertEquals(Sets.newHashSet(map.keySet()), map.keySet());
    }
  }

  public static class MapTests extends AbstractMapTests<String, Integer> {
    @Override protected Map<String, Integer> makeEmptyMap() {
      return ImmutableMap.of();
    }

    @Override protected Map<String, Integer> makePopulatedMap() {
      return ImmutableMap.of("one", 1, "two", 2, "three", 3);
    }

    @Override protected String getKeyNotInPopulatedMap() {
      return "minus one";
    }

    @Override protected Integer getValueNotInPopulatedMap() {
      return -1;
    }
  }

  public static class SingletonMapTests
      extends AbstractMapTests<String, Integer> {
    @Override protected Map<String, Integer> makePopulatedMap() {
      return ImmutableMap.of("one", 1);
    }

    @Override protected String getKeyNotInPopulatedMap() {
      return "minus one";
    }

    @Override protected Integer getValueNotInPopulatedMap() {
      return -1;
    }
  }

  public static class ReserializedMapTests
      extends AbstractMapTests<String, Integer> {
    @Override protected Map<String, Integer> makePopulatedMap() {
      return SerializableTester.reserialize(
          ImmutableMap.of("one", 1, "two", 2, "three", 3));
    }

    @Override protected String getKeyNotInPopulatedMap() {
      return "minus one";
    }

    @Override protected Integer getValueNotInPopulatedMap() {
      return -1;
    }
  }

  public static class MapTestsWithBadHashes
      extends AbstractMapTests<Object, Integer> {

    @Override protected Map<Object, Integer> makeEmptyMap() {
      throw new UnsupportedOperationException();
    }

    @Override protected Map<Object, Integer> makePopulatedMap() {
      Colliders colliders = new Colliders();
      return ImmutableMap.of(
          colliders.e0, 0,
          colliders.e1, 1,
          colliders.e2, 2,
          colliders.e3, 3);
    }

    @Override protected Object getKeyNotInPopulatedMap() {
      return new Colliders().e4;
    }

    @Override protected Integer getValueNotInPopulatedMap() {
      return 4;
    }
  }

  public static class MapTestsWithUnhashableValues
      extends AbstractMapTests<Integer, UnhashableObject> {
    @Override protected Map<Integer, UnhashableObject> makeEmptyMap() {
      return ImmutableMap.of();
    }

    @Override protected Map<Integer, UnhashableObject> makePopulatedMap() {
      Unhashables unhashables = new Unhashables();
      return ImmutableMap.of(
          0, unhashables.e0, 1, unhashables.e1, 2, unhashables.e2);
    }

    @Override protected Integer getKeyNotInPopulatedMap() {
      return 3;
    }

    @Override protected UnhashableObject getValueNotInPopulatedMap() {
      return new Unhashables().e3;
    }
  }

  public static class MapTestsWithSingletonUnhashableValue
      extends MapTestsWithUnhashableValues {
    @Override protected Map<Integer, UnhashableObject> makePopulatedMap() {
      Unhashables unhashables = new Unhashables();
      return ImmutableMap.of(0, unhashables.e0);
    }
  }

  public static class CreationTests extends TestCase {
    public void testEmptyBuilder() {
      ImmutableMap<String, Integer> map
          = new Builder<String, Integer>().build();
      assertEquals(Collections.<String, Integer>emptyMap(), map);
    }

    public void testSingletonBuilder() {
      ImmutableMap<String, Integer> map = new Builder<String, Integer>()
          .put("one", 1)
          .build();
      assertMapEquals(map, "one", 1);
    }

    public void testBuilder() {
      ImmutableMap<String, Integer> map = new Builder<String, Integer>()
          .put("one", 1)
          .put("two", 2)
          .put("three", 3)
          .put("four", 4)
          .put("five", 5)
          .build();
      assertMapEquals(map,
          "one", 1, "two", 2, "three", 3, "four", 4, "five", 5);
    }

    public void testBuilderPutAllWithEmptyMap() {
      ImmutableMap<String, Integer> map = new Builder<String, Integer>()
          .putAll(Collections.<String, Integer>emptyMap())
          .build();
      assertEquals(Collections.<String, Integer>emptyMap(), map);
    }

    public void testBuilderPutAll() {
      Map<String, Integer> toPut = new LinkedHashMap<String, Integer>();
      toPut.put("one", 1);
      toPut.put("two", 2);
      toPut.put("three", 3);
      Map<String, Integer> moreToPut = new LinkedHashMap<String, Integer>();
      moreToPut.put("four", 4);
      moreToPut.put("five", 5);

      ImmutableMap<String, Integer> map = new Builder<String, Integer>()
          .putAll(toPut)
          .putAll(moreToPut)
          .build();
      assertMapEquals(map,
          "one", 1, "two", 2, "three", 3, "four", 4, "five", 5);
    }

    public void testBuilderReuse() {
      Builder<String, Integer> builder = new Builder<String, Integer>();
      ImmutableMap<String, Integer> mapOne = builder
          .put("one", 1)
          .put("two", 2)
          .build();
      ImmutableMap<String, Integer> mapTwo = builder
          .put("three", 3)
          .put("four", 4)
          .build();

      assertMapEquals(mapOne, "one", 1, "two", 2);
      assertMapEquals(mapTwo, "one", 1, "two", 2, "three", 3, "four", 4);
    }

    public void testBuilderPutNullKey() {
      Builder<String, Integer> builder = new Builder<String, Integer>();
      try {
        builder.put(null, 1);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testBuilderPutNullValue() {
      Builder<String, Integer> builder = new Builder<String, Integer>();
      try {
        builder.put("one", null);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testBuilderPutNullKeyViaPutAll() {
      Builder<String, Integer> builder = new Builder<String, Integer>();
      try {
        builder.putAll(Collections.<String, Integer>singletonMap(null, 1));
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testBuilderPutNullValueViaPutAll() {
      Builder<String, Integer> builder = new Builder<String, Integer>();
      try {
        builder.putAll(Collections.<String, Integer>singletonMap("one", null));
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testPuttingTheSameKeyTwiceThrowsOnBuild() {
      Builder<String, Integer> builder = new Builder<String, Integer>()
          .put("one", 1)
          .put("one", 1); // throwing on this line would be even better

      try {
        builder.build();
        fail();
      } catch (IllegalArgumentException expected) {
        assertEquals("duplicate key: one", expected.getMessage());
      }
    }

    public void testOf() {
      assertMapEquals(
          ImmutableMap.of("one", 1),
          "one", 1);
      assertMapEquals(
          ImmutableMap.of("one", 1, "two", 2),
          "one", 1, "two", 2);
      assertMapEquals(
          ImmutableMap.of("one", 1, "two", 2, "three", 3),
          "one", 1, "two", 2, "three", 3);
      assertMapEquals(
          ImmutableMap.of("one", 1, "two", 2, "three", 3, "four", 4),
          "one", 1, "two", 2, "three", 3, "four", 4);
      assertMapEquals(
          ImmutableMap.of("one", 1, "two", 2, "three", 3, "four", 4, "five", 5),
          "one", 1, "two", 2, "three", 3, "four", 4, "five", 5);
    }

    public void testOfNullKey() {
      try {
        ImmutableMap.of(null, 1);
        fail();
      } catch (NullPointerException expected) {
      }

      try {
        ImmutableMap.of("one", 1, null, 2);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testOfNullValue() {
      try {
        ImmutableMap.of("one", null);
        fail();
      } catch (NullPointerException expected) {
      }

      try {
        ImmutableMap.of("one", 1, "two", null);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testOfWithDuplicateKey() {
      try {
        ImmutableMap.of("one", 1, "one", 1);
        fail();
      } catch (IllegalArgumentException expected) {
        assertEquals("duplicate key: one", expected.getMessage());
      }
    }

    public void testCopyOfEmptyMap() {
      ImmutableMap<String, Integer> copy
          = ImmutableMap.copyOf(Collections.<String, Integer>emptyMap());
      assertEquals(Collections.<String, Integer>emptyMap(), copy);
      assertSame(copy, ImmutableMap.copyOf(copy));
    }

    public void testCopyOfSingletonMap() {
      ImmutableMap<String, Integer> copy
          = ImmutableMap.copyOf(Collections.singletonMap("one", 1));
      assertMapEquals(copy, "one", 1);
      assertSame(copy, ImmutableMap.copyOf(copy));
    }

    public void testCopyOf() {
      Map<String, Integer> original = new LinkedHashMap<String, Integer>();
      original.put("one", 1);
      original.put("two", 2);
      original.put("three", 3);

      ImmutableMap<String, Integer> copy = ImmutableMap.copyOf(original);
      assertMapEquals(copy, "one", 1, "two", 2, "three", 3);
      assertSame(copy, ImmutableMap.copyOf(copy));
    }
  }

  public void testNullPointers() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(ImmutableMap.class);
    tester.testAllPublicInstanceMethods(
        new ImmutableMap.Builder<Object, Object>());
    if (false) {
      // these tests aren't included due to a bug in NullPointerTester
      // TODO: fix that bug, add these tests
      tester.testAllPublicInstanceMethods(ImmutableMap.of());
      tester.testAllPublicInstanceMethods(ImmutableMap.of("one", 1));
      tester.testAllPublicInstanceMethods(
          ImmutableMap.of("one", 1, "two", 2, "three", 3));
    }
  }

  private static <K, V> void assertMapEquals(Map<K, V> map,
      Object... alternatingKeysAndValues) {
    assertEquals(map.size(), alternatingKeysAndValues.length / 2);
    int i = 0;
    for (Entry<K, V> entry : map.entrySet()) {
      assertEquals(alternatingKeysAndValues[i++], entry.getKey());
      assertEquals(alternatingKeysAndValues[i++], entry.getValue());
    }
  }

  private static class IntHolder implements Serializable {
    public int value;

    public IntHolder(int value) {
      this.value = value;
    }

    @Override public boolean equals(Object o) {
      return (o instanceof IntHolder) && ((IntHolder) o).value == value;
    }

    @Override public int hashCode() {
      return value;
    }

    private static final long serialVersionUID = 5;
  }

  public void testMutableValues() {
    IntHolder holderA = new IntHolder(1);
    IntHolder holderB = new IntHolder(2);
    Map<String, IntHolder> map = ImmutableMap.of("a", holderA, "b", holderB);
    holderA.value = 3;
    assertTrue(map.entrySet().contains(
        Maps.immutableEntry("a", new IntHolder(3))));
    Map<String, Integer> intMap = ImmutableMap.of("a", 3, "b", 2);
    assertEquals(intMap.hashCode(), map.entrySet().hashCode());
    assertEquals(intMap.hashCode(), map.hashCode());
  }

  public void testViewSerialization() {
    Map<String, Integer> map = ImmutableMap.of("one", 1, "two", 2, "three", 3);
    SerializableTester.reserializeAndAssert(map.entrySet());
    SerializableTester.reserializeAndAssert(map.keySet());
    assertEquals(Lists.newArrayList(map.values()),
        Lists.newArrayList(SerializableTester.reserialize(map.values())));
  }
}
