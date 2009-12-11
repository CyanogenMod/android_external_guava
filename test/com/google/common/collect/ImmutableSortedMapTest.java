/*
 * Copyright (C) 2009 Google Inc.
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
import com.google.common.collect.ImmutableSortedMap.Builder;
import com.google.common.collect.testing.CollectionTestSuiteBuilder;
import com.google.common.collect.testing.ReserializingTestCollectionGenerator;
import com.google.common.collect.testing.ReserializingTestSetGenerator;
import com.google.common.collect.testing.SampleElements;
import static com.google.common.collect.testing.SampleElements.Strings.AFTER_LAST;
import static com.google.common.collect.testing.SampleElements.Strings.BEFORE_FIRST;
import static com.google.common.collect.testing.SampleElements.Strings.MIN_ELEMENT;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.SortedMapInterfaceTest;
import com.google.common.collect.testing.TestCollectionGenerator;
import com.google.common.collect.testing.TestMapEntrySetGenerator;
import com.google.common.collect.testing.TestStringSetGenerator;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

/**
 * Tests for {@link ImmutableSortedMap}.
 *
 * @author Kevin Bourrillion
 * @author Jesse Wilson
 * @author Jared Levy
 */
public class ImmutableSortedMapTest extends TestCase {
  // TODO: Avoid duplicating code in ImmutableMapTest

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(ImmutableSortedMapTest.class);

    suite.addTest(SetTestSuiteBuilder.using(keySetGenerator())
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableSortedMap.keySet")
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(entrySetGenerator())
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableSortedMap.entrySet")
        .createTestSuite());

    suite.addTest(CollectionTestSuiteBuilder.using(valuesGenerator())
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .named("ImmutableSortedMap.values")
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        ReserializingTestSetGenerator.newInstance(keySetGenerator()))
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableSortedMap.keySet, reserialized")
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        ReserializingTestSetGenerator.newInstance(entrySetGenerator()))
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableSortedMap.entrySet, reserialized")
        .createTestSuite());

    suite.addTest(CollectionTestSuiteBuilder.using(
        ReserializingTestCollectionGenerator.newInstance(valuesGenerator()))
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .named("ImmutableSortedMap.values, reserialized")
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(headMapKeySetGenerator())
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableSortedMap.headMap.keySet")
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(subMapEntrySetGenerator())
        .withFeatures(
            CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            SetFeature.REJECTS_DUPLICATES_AT_CREATION)
        .named("ImmutableSortedMap.subMap.entrySet")
        .createTestSuite());

    suite.addTest(CollectionTestSuiteBuilder.using(tailMapValuesGenerator())
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .named("ImmutableSortedMap.tailMap.values")
        .createTestSuite());
    
    return suite;
  }

  private static final Comparator<Entry<String, String>> ENTRY_COMPARATOR
      = new Comparator<Entry<String, String>>() {
        public int compare(Entry<String, String> o1, Entry<String, String> o2) {
          return o1.getKey().compareTo(o2.getKey());
        }    
      };
  
  static TestMapEntrySetGenerator<String, String> entrySetGenerator() {
    SampleElements.Strings sampleStrings = new SampleElements.Strings();
    return new TestMapEntrySetGenerator<String, String>(
        sampleStrings, sampleStrings) {
      @Override public Set<Entry<String, String>> createFromEntries(
          Entry<String, String>[] entries) {
        Builder<String, String> builder = ImmutableSortedMap.naturalOrder();
        for (Entry<String, String> entry : entries) {
          builder.put(entry.getKey(), entry.getValue());
        }
        return builder.build().entrySet();
      }
      @Override public List<Entry<String, String>> order(
          List<Entry<String, String>> insertionOrder) {
        Collections.sort(insertionOrder, ENTRY_COMPARATOR);
        return insertionOrder;
      }
    };
  }

  static TestStringSetGenerator keySetGenerator() {
    return new TestStringSetGenerator() {
      @Override protected Set<String> create(String[] elements) {
        Builder<String, Integer> builder = ImmutableSortedMap.naturalOrder();
        for (String key : elements) {
          builder.put(key, key.length());
        }
        return builder.build().keySet();
      }
      @Override public List<String> order(List<String> insertionOrder) {
        Collections.sort(insertionOrder);
        return insertionOrder;
      }
    };
  }

  static TestCollectionGenerator<String> valuesGenerator() {
    return new TestCollectionGenerator<String>() {
      public SampleElements<String> samples() {
        return new SampleElements.Strings();
      }

      public Collection<String> create(Object... elements) {
        Builder<Integer, String> builder = ImmutableSortedMap.naturalOrder();
        for (int i = 0; i < elements.length; i++) {
          builder.put(i, elements[i].toString());
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

  static TestMapEntrySetGenerator<String, String> subMapEntrySetGenerator() {
    SampleElements.Strings sampleStrings = new SampleElements.Strings();
    return new TestMapEntrySetGenerator<String, String>(
        sampleStrings, sampleStrings) {
      @Override public Set<Entry<String, String>> createFromEntries(
          Entry<String, String>[] entries) {
        Builder<String, String> builder = ImmutableSortedMap.naturalOrder();
        builder.put(BEFORE_FIRST, "begin");
        builder.put(AFTER_LAST, "end");
        for (Entry<String, String> entry : entries) {
          builder.put(entry.getKey(), entry.getValue());
        }
        return builder.build().subMap(MIN_ELEMENT, AFTER_LAST).entrySet();
      }
      @Override public List<Entry<String, String>> order(
          List<Entry<String, String>> insertionOrder) {
        Collections.sort(insertionOrder, ENTRY_COMPARATOR);
        return insertionOrder;
      }
    };
  }

  static TestStringSetGenerator headMapKeySetGenerator() {
    return new TestStringSetGenerator() {
      @Override protected Set<String> create(String[] elements) {
        Builder<String, Integer> builder = ImmutableSortedMap.naturalOrder();
        builder.put(AFTER_LAST, -1);
        for (String key : elements) {
          builder.put(key, key.length());
        }
        return builder.build().headMap(AFTER_LAST).keySet();
      }
      @Override public List<String> order(List<String> insertionOrder) {
        Collections.sort(insertionOrder);
        return insertionOrder;
      }
    };
  }

  static TestCollectionGenerator<String> tailMapValuesGenerator() {
    return new TestCollectionGenerator<String>() {
      public SampleElements<String> samples() {
        return new SampleElements.Strings();
      }

      public Collection<String> create(Object... elements) {
        Builder<Integer, String> builder = ImmutableSortedMap.naturalOrder();
        builder.put(-1, "begin");
        for (int i = 0; i < elements.length; i++) {
          builder.put(i, elements[i].toString());
        }
        return builder.build().tailMap(0).values();
      }

      public String[] createArray(int length) {
        return new String[length];
      }

      public List<String> order(List<String> insertionOrder) {
        return insertionOrder;
      }
    };
  }

  public abstract static class AbstractMapTests<K, V>
      extends SortedMapInterfaceTest<K, V> {
    public AbstractMapTests() {
      super(false, false, false, false, false);
    }

    @Override protected SortedMap<K, V> makeEmptyMap() {
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

      assertEquals(Sets.newHashSet(map.entrySet()), map.entrySet());
      assertEquals(Sets.newHashSet(map.keySet()), map.keySet());
    }
  }

  public static class MapTests extends AbstractMapTests<String, Integer> {
    @Override protected SortedMap<String, Integer> makeEmptyMap() {
      return ImmutableSortedMap.of();
    }

    @Override protected SortedMap<String, Integer> makePopulatedMap() {
      return ImmutableSortedMap.of("one", 1, "two", 2, "three", 3);
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
    @Override protected SortedMap<String, Integer> makePopulatedMap() {
      return ImmutableSortedMap.of("one", 1);
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
    @Override protected SortedMap<String, Integer> makePopulatedMap() {
      return SerializableTester.reserialize(
          ImmutableSortedMap.of("one", 1, "two", 2, "three", 3));
    }

    @Override protected String getKeyNotInPopulatedMap() {
      return "minus one";
    }

    @Override protected Integer getValueNotInPopulatedMap() {
      return -1;
    }
  }

  public static class HeadMapTests extends AbstractMapTests<String, Integer> {
    @Override protected SortedMap<String, Integer> makePopulatedMap() {
      return ImmutableSortedMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5)
          .headMap("d");
    }
    
    @Override protected String getKeyNotInPopulatedMap() {
      return "d";
    }
    
    @Override protected Integer getValueNotInPopulatedMap() {
      return 4;
    }
  }
  
  public static class TailMapTests extends AbstractMapTests<String, Integer> {
    @Override protected SortedMap<String, Integer> makePopulatedMap() {
      return ImmutableSortedMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5)
          .tailMap("b");
    }
    
    @Override protected String getKeyNotInPopulatedMap() {
      return "a";
    }
    
    @Override protected Integer getValueNotInPopulatedMap() {
      return 1;
    }
  }
  
  public static class SubMapTests extends AbstractMapTests<String, Integer> {
    @Override protected SortedMap<String, Integer> makePopulatedMap() {
      return ImmutableSortedMap.of("a", 1, "b", 2, "c", 3, "d", 4, "e", 5)
          .subMap("b", "d");
    }
    
    @Override protected String getKeyNotInPopulatedMap() {
      return "a";
    }
    
    @Override protected Integer getValueNotInPopulatedMap() {
      return 4;
    }
  }

  public static class CreationTests extends TestCase {
    public void testEmptyBuilder() {
      ImmutableSortedMap<String, Integer> map
          = ImmutableSortedMap.<String, Integer>naturalOrder().build();
      assertEquals(Collections.<String, Integer>emptyMap(), map);
    }

    public void testSingletonBuilder() {
      ImmutableSortedMap<String, Integer> map
          = ImmutableSortedMap.<String, Integer>naturalOrder()
              .put("one", 1)
              .build();
      assertMapEquals(map, "one", 1);
    }

    public void testBuilder() {
      ImmutableSortedMap<String, Integer> map
          = ImmutableSortedMap.<String, Integer>naturalOrder()
              .put("one", 1)
              .put("two", 2)
              .put("three", 3)
              .put("four", 4)
              .put("five", 5)
              .build();
      assertMapEquals(map,
          "five", 5, "four", 4, "one", 1, "three", 3, "two", 2);
    }

    public void testBuilderPutAllWithEmptyMap() {
      ImmutableSortedMap<String, Integer> map
          = ImmutableSortedMap.<String, Integer>naturalOrder()
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

      ImmutableSortedMap<String, Integer> map
          = ImmutableSortedMap.<String, Integer>naturalOrder()
              .putAll(toPut)
              .putAll(moreToPut)
              .build();
      assertMapEquals(map,
          "five", 5, "four", 4, "one", 1, "three", 3, "two", 2);
    }

    public void testBuilderReuse() {
      Builder<String, Integer> builder = ImmutableSortedMap.naturalOrder();
      ImmutableSortedMap<String, Integer> mapOne = builder
          .put("one", 1)
          .put("two", 2)
          .build();
      ImmutableSortedMap<String, Integer> mapTwo = builder
          .put("three", 3)
          .put("four", 4)
          .build();

      assertMapEquals(mapOne, "one", 1, "two", 2);
      assertMapEquals(mapTwo, "four", 4, "one", 1, "three", 3, "two", 2);
    }

    public void testBuilderPutNullKey() {
      Builder<String, Integer> builder = ImmutableSortedMap.naturalOrder();
      try {
        builder.put(null, 1);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testBuilderPutNullValue() {
      Builder<String, Integer> builder = ImmutableSortedMap.naturalOrder();
      try {
        builder.put("one", null);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testBuilderPutNullKeyViaPutAll() {
      Builder<String, Integer> builder = ImmutableSortedMap.naturalOrder();
      try {
        builder.putAll(Collections.<String, Integer>singletonMap(null, 1));
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testBuilderPutNullValueViaPutAll() {
      Builder<String, Integer> builder = ImmutableSortedMap.naturalOrder();
      try {
        builder.putAll(Collections.<String, Integer>singletonMap("one", null));
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testPuttingTheSameKeyTwiceThrowsOnBuild() {
      Builder<String, Integer> builder
          = ImmutableSortedMap.<String,Integer>naturalOrder()
              .put("one", 1)
              .put("one", 2); // throwing on this line would be even better

      try {
        builder.build();
        fail();
      } catch (IllegalArgumentException expected) {
        assertEquals("Duplicate keys in mappings one=1 and one=2",
            expected.getMessage());
      }
    }

    public void testOf() {
      assertMapEquals(
          ImmutableSortedMap.of("one", 1),
          "one", 1);
      assertMapEquals(
          ImmutableSortedMap.of("one", 1, "two", 2),
          "one", 1, "two", 2);
      assertMapEquals(
          ImmutableSortedMap.of("one", 1, "two", 2, "three", 3),
          "one", 1, "three", 3, "two", 2);
      assertMapEquals(
          ImmutableSortedMap.of("one", 1, "two", 2, "three", 3, "four", 4),
          "four", 4, "one", 1, "three", 3, "two", 2);
      assertMapEquals(
          ImmutableSortedMap.of(
              "one", 1, "two", 2, "three", 3, "four", 4, "five", 5),
          "five", 5, "four", 4, "one", 1, "three", 3, "two", 2);
    }

    public void testOfNullKey() {
      Integer n = null;
      try {
        ImmutableSortedMap.of(n, 1);
        fail();
      } catch (NullPointerException expected) {
      }

      try {
        ImmutableSortedMap.of("one", 1, null, 2);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testOfNullValue() {
      try {
        ImmutableSortedMap.of("one", null);
        fail();
      } catch (NullPointerException expected) {
      }

      try {
        ImmutableSortedMap.of("one", 1, "two", null);
        fail();
      } catch (NullPointerException expected) {
      }
    }

    public void testOfWithDuplicateKey() {
      try {
        ImmutableSortedMap.of("one", 1, "one", 1);
        fail();
      } catch (IllegalArgumentException expected) {
        assertEquals("Duplicate keys in mappings one=1 and one=1",
            expected.getMessage());
      }
    }

    public void testCopyOfEmptyMap() {
      ImmutableSortedMap<String, Integer> copy
          = ImmutableSortedMap.copyOf(Collections.<String, Integer>emptyMap());
      assertEquals(Collections.<String, Integer>emptyMap(), copy);
      assertSame(copy, ImmutableSortedMap.copyOf(copy));
      assertSame(Ordering.natural(), copy.comparator());
    }

    public void testCopyOfSingletonMap() {
      ImmutableSortedMap<String, Integer> copy
          = ImmutableSortedMap.copyOf(Collections.singletonMap("one", 1));
      assertMapEquals(copy, "one", 1);
      assertSame(copy, ImmutableSortedMap.copyOf(copy));
      assertSame(Ordering.natural(), copy.comparator());
    }

    public void testCopyOf() {
      Map<String, Integer> original = new LinkedHashMap<String, Integer>();
      original.put("one", 1);
      original.put("two", 2);
      original.put("three", 3);

      ImmutableSortedMap<String, Integer> copy
          = ImmutableSortedMap.copyOf(original);
      assertMapEquals(copy, "one", 1, "three", 3, "two", 2);
      assertSame(copy, ImmutableSortedMap.copyOf(copy));
      assertSame(Ordering.natural(), copy.comparator());
    }
    
    public void testCopyOfExplicitComparator() {
      Comparator<String> comparator = Ordering.natural().reverse();
      Map<String, Integer> original = new LinkedHashMap<String, Integer>();
      original.put("one", 1);
      original.put("two", 2);
      original.put("three", 3);

      ImmutableSortedMap<String, Integer> copy
          = ImmutableSortedMap.copyOf(original, comparator);
      assertMapEquals(copy, "two", 2, "three", 3, "one", 1);
      assertSame(copy, ImmutableSortedMap.copyOf(copy, comparator));
      assertSame(comparator, copy.comparator());      
    }
    
    public void testCopyOfImmutableSortedSetDifferentComparator() {
      Comparator<String> comparator = Ordering.natural().reverse();
      Map<String, Integer> original
          = ImmutableSortedMap.of("one", 1, "two", 2, "three", 3);
      ImmutableSortedMap<String, Integer> copy
          = ImmutableSortedMap.copyOf(original, comparator);
      assertMapEquals(copy, "two", 2, "three", 3, "one", 1);
      assertSame(copy, ImmutableSortedMap.copyOf(copy, comparator));
      assertSame(comparator, copy.comparator());      
    }
    
    public void testCopyOfSortedNatural() {
      SortedMap<String, Integer> original = Maps.newTreeMap();
      original.put("one", 1);
      original.put("two", 2);
      original.put("three", 3);

      ImmutableSortedMap<String, Integer> copy
          = ImmutableSortedMap.copyOfSorted(original);
      assertMapEquals(copy, "one", 1, "three", 3, "two", 2);
      assertSame(copy, ImmutableSortedMap.copyOfSorted(copy));
      assertSame(Ordering.natural(), copy.comparator());
    }

    public void testCopyOfSortedExplicit() {
      Comparator<String> comparator = Ordering.natural().reverse();
      SortedMap<String, Integer> original = Maps.newTreeMap(comparator);
      original.put("one", 1);
      original.put("two", 2);
      original.put("three", 3);

      ImmutableSortedMap<String, Integer> copy
          = ImmutableSortedMap.copyOfSorted(original);
      assertMapEquals(copy, "two", 2, "three", 3, "one", 1);
      assertSame(copy, ImmutableSortedMap.copyOfSorted(copy));
      assertSame(comparator, copy.comparator());
    }
    
    private static class IntegerDiv10 implements Comparable<IntegerDiv10> {
      final int value;

      IntegerDiv10(int value) {
        this.value = value;
      }

      public int compareTo(IntegerDiv10 o) {
        return value / 10 - o.value / 10;
      }
      
      @Override public String toString() {
        return Integer.toString(value);
      } 
    }
    
    public void testCopyOfDuplicateKey() {
      Map<IntegerDiv10, String> original = ImmutableMap.of(
          new IntegerDiv10(3), "three",
          new IntegerDiv10(20), "twenty",
          new IntegerDiv10(11), "eleven",
          new IntegerDiv10(25), "twenty five",
          new IntegerDiv10(12), "twelve");
      
      try {
        ImmutableSortedMap.copyOf(original);
        fail("Expected IllegalArgumentException");
      } catch (IllegalArgumentException expected) {
        assertEquals("Duplicate keys in mappings 11=eleven and 12=twelve",
            expected.getMessage());
      }
    }
    
    public void testImmutableMapCopyOfImmutableSortedMap() {
      IntegerDiv10 three = new IntegerDiv10(3);
      IntegerDiv10 eleven = new IntegerDiv10(11);
      IntegerDiv10 twelve = new IntegerDiv10(12);
      IntegerDiv10 twenty = new IntegerDiv10(20);
      Map<IntegerDiv10, String> original = ImmutableSortedMap.of(
          three, "three", eleven, "eleven", twenty, "twenty");
      Map<IntegerDiv10, String> copy = ImmutableMap.copyOf(original);
      assertTrue(original.containsKey(twelve));
      assertFalse(copy.containsKey(twelve));
    }
    
    public void testBuilderReverseOrder() {
      ImmutableSortedMap<String, Integer> map
          = ImmutableSortedMap.<String, Integer>reverseOrder()
              .put("one", 1)
              .put("two", 2)
              .put("three", 3)
              .put("four", 4)
              .put("five", 5)
              .build();
      assertMapEquals(map,
          "two", 2, "three", 3, "one", 1, "four", 4, "five", 5);
      assertEquals(Ordering.natural().reverse(), map.comparator());
    }
    
    public void testBuilderComparator() {
      Comparator<String> comparator = Ordering.natural().reverse();
      ImmutableSortedMap<String, Integer> map
          = new ImmutableSortedMap.Builder<String, Integer>(comparator)
              .put("one", 1)
              .put("two", 2)
              .put("three", 3)
              .put("four", 4)
              .put("five", 5)
              .build();
      assertMapEquals(map,
          "two", 2, "three", 3, "one", 1, "four", 4, "five", 5);
      assertSame(comparator, map.comparator());
    }    
  }

  public void testNullPointers() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(ImmutableSortedMap.class);
    tester.testAllPublicInstanceMethods(
        ImmutableSortedMap.<String, Integer>naturalOrder());
    if (false) {
      // these tests aren't included due to a bug in NullPointerTester
      // TODO: fix that bug, add these tests
      tester.testAllPublicInstanceMethods(ImmutableSortedMap.of());
      tester.testAllPublicInstanceMethods(ImmutableSortedMap.of("one", 1));
      tester.testAllPublicInstanceMethods(
          ImmutableSortedMap.of("one", 1, "two", 2, "three", 3));
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
    Map<String, IntHolder> map
        = ImmutableSortedMap.of("a", holderA, "b", holderB);
    holderA.value = 3;
    assertTrue(map.entrySet().contains(
        Maps.immutableEntry("a", new IntHolder(3))));
    Map<String, Integer> intMap
        = ImmutableSortedMap.of("a", 3, "b", 2);
    assertEquals(intMap.hashCode(), map.entrySet().hashCode());
    assertEquals(intMap.hashCode(), map.hashCode());
  }

  public void testViewSerialization() {
    Map<String, Integer> map
        = ImmutableSortedMap.of("one", 1, "two", 2, "three", 3);
    SerializableTester.reserializeAndAssert(map.entrySet());
    SerializableTester.reserializeAndAssert(map.keySet());
    assertEquals(Lists.newArrayList(map.values()),
        Lists.newArrayList(SerializableTester.reserialize(map.values())));
  }
}
