/*
 * Copyright (C) 2007 Google Inc.
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import static com.google.common.collect.Iterators.get;
import static com.google.common.collect.Iterators.getLast;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.testing.IteratorFeature;
import static com.google.common.collect.testing.IteratorFeature.MODIFIABLE;
import static com.google.common.collect.testing.IteratorFeature.UNMODIFIABLE;
import com.google.common.collect.testing.IteratorTester;
import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import com.google.common.testutils.NullPointerTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.singleton;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;
import java.util.Vector;

/**
 * Unit test for {@code Iterators}.
 *
 * @author Kevin Bourrillion
 */
public class IteratorsTest extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite(IteratorsTest.class.getSimpleName());
    suite.addTest(testsForRemoveAllAndRetainAll());
    suite.addTestSuite(IteratorsTest.class);
    return suite;
  }

  public void testEmptyIterator() {
    Iterator<String> iterator = Iterators.emptyIterator();
    assertFalse(iterator.hasNext());
    try {
      iterator.next();
      fail("no exception thrown");
    } catch (NoSuchElementException expected) {
    }
    try {
      iterator.remove();
      fail("no exception thrown");
    } catch (UnsupportedOperationException expected) {
    }
  }

  public void testSize0() {
    Iterator<String> iterator = Iterators.emptyIterator();
    assertEquals(0, Iterators.size(iterator));
  }

  public void testSize1() {
    Iterator<Integer> iterator = Collections.singleton(0).iterator();
    assertEquals(1, Iterators.size(iterator));
  }

  public void testSize_partiallyConsumed() {
    Iterator<Integer> iterator = asList(1, 2, 3, 4, 5).iterator();
    iterator.next();
    iterator.next();
    assertEquals(3, Iterators.size(iterator));
  }

  public void test_contains_nonnull_yes() {
    Iterator<String> set = asList("a", null, "b").iterator();
    assertTrue(Iterators.contains(set, "b"));
  }

  public void test_contains_nonnull_no() {
    Iterator<String> set = asList("a", "b").iterator();
    assertFalse(Iterators.contains(set, "c"));
  }

  public void test_contains_null_yes() {
    Iterator<String> set = asList("a", null, "b").iterator();
    assertTrue(Iterators.contains(set, null));
  }

  public void test_contains_null_no() {
    Iterator<String> set = asList("a", "b").iterator();
    assertFalse(Iterators.contains(set, null));
  }

  public void testGetOnlyElement_noDefault_valid() {
    Iterator<String> iterator = Collections.singletonList("foo").iterator();
    assertEquals("foo", Iterators.getOnlyElement(iterator));
  }

  public void testGetOnlyElement_noDefault_empty() {
    Iterator<String> iterator = Iterators.emptyIterator();
    try {
      Iterators.getOnlyElement(iterator);
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testGetOnlyElement_noDefault_moreThanOneLessThanFiveElements() {
    Iterator<String> iterator = asList("one", "two").iterator();
    try {
      Iterators.getOnlyElement(iterator);
      fail();
    } catch (IllegalArgumentException expected) {
      assertEquals("expected one element but was: <one, two>",
                   expected.getMessage());
    }
  }

  public void testGetOnlyElement_noDefault_fiveElements() {
    Iterator<String> iterator =
        asList("one", "two", "three", "four", "five").iterator();
    try {
      Iterators.getOnlyElement(iterator);
      fail();
    } catch (IllegalArgumentException expected) {
      assertEquals("expected one element but was: "
                   + "<one, two, three, four, five>",
                   expected.getMessage());
    }
  }

  public void testGetOnlyElement_noDefault_moreThanFiveElements() {
    Iterator<String> iterator =
        asList("one", "two", "three", "four", "five", "six").iterator();
    try {
      Iterators.getOnlyElement(iterator);
      fail();
    } catch (IllegalArgumentException expected) {
      assertEquals("expected one element but was: "
                   + "<one, two, three, four, five, ...>",
                   expected.getMessage());
    }
  }

  public void testGetOnlyElement_withDefault_singleton() {
    Iterator<String> iterator = Collections.singletonList("foo").iterator();
    assertEquals("foo", Iterators.getOnlyElement(iterator, "bar"));
  }

  public void testGetOnlyElement_withDefault_empty() {
    Iterator<String> iterator = Iterators.emptyIterator();
    assertEquals("bar", Iterators.getOnlyElement(iterator, "bar"));
  }

  public void testGetOnlyElement_withDefault_empty_null() {
    Iterator<String> iterator = Iterators.emptyIterator();
    assertNull(Iterators.getOnlyElement(iterator, null));
  }

  public void testGetOnlyElement_withDefault_two() {
    Iterator<String> iterator = asList("foo", "bar").iterator();
    try {
      Iterators.getOnlyElement(iterator, "x");
      fail();
    } catch (IllegalArgumentException expected) {
      assertEquals("expected one element but was: <foo, bar>",
                   expected.getMessage());
    }
  }

  public void testToArrayEmpty() {
    Iterator<String> iterator = Collections.<String>emptyList().iterator();
    String[] array = Iterators.toArray(iterator, String.class);
    assertTrue(Arrays.equals(new String[0], array));
  }

  public void testToArraySingleton() {
    Iterator<String> iterator = Collections.singletonList("a").iterator();
    String[] array = Iterators.toArray(iterator, String.class);
    assertTrue(Arrays.equals(new String[] { "a" }, array));
  }

  public void testToArray() {
    String[] sourceArray = new String[] {"a", "b", "c"};
    Iterator<String> iterator = asList(sourceArray).iterator();
    String[] newArray = Iterators.toArray(iterator, String.class);
    assertTrue(Arrays.equals(sourceArray, newArray));
  }

  public void testFilterSimple() {
    Iterator<String> unfiltered = Lists.newArrayList("foo", "bar").iterator();
    Iterator<String> filtered = Iterators.filter(unfiltered,
                                                 Predicates.equalTo("foo"));
    List<String> expected = Collections.singletonList("foo");
    List<String> actual = Lists.newArrayList(filtered);
    assertEquals(expected, actual);
  }

  public void testFilterNoMatch() {
    Iterator<String> unfiltered = Lists.newArrayList("foo", "bar").iterator();
    Iterator<String> filtered = Iterators.filter(unfiltered,
                                                 Predicates.alwaysFalse());
    List<String> expected = Collections.emptyList();
    List<String> actual = Lists.newArrayList(filtered);
    assertEquals(expected, actual);
  }

  public void testFilterMatchAll() {
    Iterator<String> unfiltered = Lists.newArrayList("foo", "bar").iterator();
    Iterator<String> filtered = Iterators.filter(unfiltered,
                                                 Predicates.alwaysTrue());
    List<String> expected = Lists.newArrayList("foo", "bar");
    List<String> actual = Lists.newArrayList(filtered);
    assertEquals(expected, actual);
  }

  public void testFilterNothing() {
    Iterator<String> unfiltered = Collections.<String>emptyList().iterator();
    Iterator<String> filtered = Iterators.filter(unfiltered,
        new Predicate<String>() {
          public boolean apply(String s) {
            fail("Should never be evaluated");
            return false;
          }
        });

    List<String> expected = Collections.emptyList();
    List<String> actual = Lists.newArrayList(filtered);
    assertEquals(expected, actual);
  }

  public void testFilterUsingIteratorTester() throws Exception {
    final List<Integer> list = asList(1, 2, 3, 4, 5);
    final Predicate<Integer> isEven = new Predicate<Integer>() {
      public boolean apply(Integer integer) {
        return integer % 2 == 0;
      }
    };
    new IteratorTester<Integer>(5, UNMODIFIABLE, asList(2, 4),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.filter(list.iterator(), isEven);
      }
    }.test();
  }

  public void testAny() {
    List<String> list = Lists.newArrayList();
    Predicate<String> predicate = Predicates.equalTo("pants");

    assertFalse(Iterators.any(list.iterator(), predicate));
    list.add("cool");
    assertFalse(Iterators.any(list.iterator(), predicate));
    list.add("pants");
    assertTrue(Iterators.any(list.iterator(), predicate));
  }

  public void testAll() {
    List<String> list = Lists.newArrayList();
    Predicate<String> predicate = Predicates.equalTo("cool");

    assertTrue(Iterators.all(list.iterator(), predicate));
    list.add("cool");
    assertTrue(Iterators.all(list.iterator(), predicate));
    list.add("pants");
    assertFalse(Iterators.all(list.iterator(), predicate));
  }

  public void testFind() {
    Iterable<String> list = Lists.newArrayList("cool", "pants");
    assertEquals("cool",
        Iterators.find(list.iterator(), Predicates.equalTo("cool")));
    assertEquals("pants",
        Iterators.find(list.iterator(), Predicates.equalTo("pants")));
    try {
      Iterators.find(list.iterator(), Predicates.alwaysFalse());
      fail();
    } catch (NoSuchElementException e) {
    }
    assertEquals("cool",
        Iterators.find(list.iterator(), Predicates.alwaysTrue()));
  }

  public void testTransform() {
    Iterator<String> input = asList("1", "2", "3").iterator();
    Iterator<Integer> result = Iterators.transform(input,
        new Function<String, Integer>() {
          public Integer apply(String from) {
            return Integer.valueOf(from);
          }
        });

    List<Integer> actual = Lists.newArrayList(result);
    List<Integer> expected = asList(1, 2, 3);
    assertEquals(expected, actual);
  }

  public void testTransformRemove() {
    List<String> list = Lists.newArrayList("1", "2", "3");
    Iterator<String> input = list.iterator();
    Iterator<Integer> iterator = Iterators.transform(input,
        new Function<String, Integer>() {
          public Integer apply(String from) {
            return Integer.valueOf(from);
          }
        });

    assertEquals(Integer.valueOf(1), iterator.next());
    assertEquals(Integer.valueOf(2), iterator.next());
    iterator.remove();
    assertEquals(asList("1", "3"), list);
  }

  public void testPoorlyBehavedTransform() {
    Iterator<String> input = asList("1", null, "3").iterator();
    Iterator<Integer> result = Iterators.transform(input,
        new Function<String, Integer>() {
          public Integer apply(String from) {
            return Integer.valueOf(from);
          }
        });

    result.next();
    try {
      result.next();
      fail("Expected NFE");
    } catch (NumberFormatException nfe) {
      // Expected to fail.
    }
  }

  public void testNullFriendlyTransform() {
    Iterator<Integer> input = asList(1, 2, null, 3).iterator();
    Iterator<String> result = Iterators.transform(input,
        new Function<Integer, String>() {
          public String apply(Integer from) {
            return String.valueOf(from);
          }
        });

    List<String> actual = Lists.newArrayList(result);
    List<String> expected = asList("1", "2", "null", "3");
    assertEquals(expected, actual);
  }

  public void testCycleOfEmpty() {
    // "<String>" for javac 1.5.
    Iterator<String> cycle = Iterators.<String>cycle();
    assertFalse(cycle.hasNext());
  }

  public void testCycleOfOne() {
    Iterator<String> cycle = Iterators.cycle("a");
    for (int i = 0; i < 3; i++) {
      assertTrue(cycle.hasNext());
      assertEquals("a", cycle.next());
    }
  }

  public void testCycleOfOneWithRemove() {
    Iterable<String> iterable = Lists.newArrayList("a");
    Iterator<String> cycle = Iterators.cycle(iterable);
    assertTrue(cycle.hasNext());
    assertEquals("a", cycle.next());
    cycle.remove();
    assertEquals(Collections.emptyList(), iterable);
    assertFalse(cycle.hasNext());
  }

  public void testCycleOfTwo() {
    Iterator<String> cycle = Iterators.cycle("a", "b");
    for (int i = 0; i < 3; i++) {
      assertTrue(cycle.hasNext());
      assertEquals("a", cycle.next());
      assertTrue(cycle.hasNext());
      assertEquals("b", cycle.next());
    }
  }

  public void testCycleOfTwoWithRemove() {
    Iterable<String> iterable = Lists.newArrayList("a", "b");
    Iterator<String> cycle = Iterators.cycle(iterable);
    assertTrue(cycle.hasNext());
    assertEquals("a", cycle.next());
    assertTrue(cycle.hasNext());
    assertEquals("b", cycle.next());
    assertTrue(cycle.hasNext());
    assertEquals("a", cycle.next());
    cycle.remove();
    assertEquals(Collections.singletonList("b"), iterable);
    assertTrue(cycle.hasNext());
    assertEquals("b", cycle.next());
    assertTrue(cycle.hasNext());
    assertEquals("b", cycle.next());
    cycle.remove();
    assertEquals(Collections.emptyList(), iterable);
    assertFalse(cycle.hasNext());
  }

  public void testCycleRemoveWithoutNext() {
    Iterator<String> cycle = Iterators.cycle("a", "b");
    assertTrue(cycle.hasNext());
    try {
      cycle.remove();
      fail("no exception thrown");
    } catch (IllegalStateException expected) {
    }
  }

  public void testCycleRemoveSameElementTwice() {
    Iterator<String> cycle = Iterators.cycle("a", "b");
    cycle.next();
    cycle.remove();
    try {
      cycle.remove();
      fail("no exception thrown");
    } catch (IllegalStateException expected) {
    }
  }

  public void testCycleWhenRemoveIsNotSupported() {
    Iterable<String> iterable = asList("a", "b");
    Iterator<String> cycle = Iterators.cycle(iterable);
    cycle.next();
    try {
      cycle.remove();
      fail("no exception thrown");
    } catch (UnsupportedOperationException expected) {
    }
  }

  public void testCycleRemoveAfterHasNext() {
    Iterable<String> iterable = Lists.newArrayList("a");
    Iterator<String> cycle = Iterators.cycle(iterable);
    assertTrue(cycle.hasNext());
    assertEquals("a", cycle.next());
    assertTrue(cycle.hasNext());
    cycle.remove();
    assertEquals(Collections.emptyList(), iterable);
    assertFalse(cycle.hasNext());
  }

  public void testCycleNoSuchElementException() {
    Iterable<String> iterable = Lists.newArrayList("a");
    Iterator<String> cycle = Iterators.cycle(iterable);
    assertTrue(cycle.hasNext());
    assertEquals("a", cycle.next());
    cycle.remove();
    assertFalse(cycle.hasNext());
    try {
      cycle.next();
      fail();
    } catch (NoSuchElementException expected) {}
  }

  public void testCycleUsingIteratorTester() throws Exception {
    new IteratorTester<Integer>(5, UNMODIFIABLE, asList(1, 2, 1, 2, 1,
        2, 1, 2, 1, 2, 1, 2), IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.cycle(asList(1, 2));
      }
    }.test();
  }

  public void testConcatNoIteratorsYieldsEmpty() throws Exception {
    new EmptyIteratorTester() {
      @SuppressWarnings("unchecked")
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.concat();
      }
    }.test();
  }

  public void testConcatOneEmptyIteratorYieldsEmpty() throws Exception {
    new EmptyIteratorTester() {
      @SuppressWarnings("unchecked")
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.concat(iterateOver());
      }
    }.test();
  }

  public void testConcatMultipleEmptyIteratorsYieldsEmpty() throws Exception {
    new EmptyIteratorTester() {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.concat(iterateOver(), iterateOver());
      }
    }.test();
  }

  public void testConcatSingletonYieldsSingleton() throws Exception {
    new SingletonIteratorTester() {
      @SuppressWarnings("unchecked")
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.concat(iterateOver(1));
      }
    }.test();
  }

  public void testConcatEmptyAndSingletonAndEmptyYieldsSingleton()
      throws Exception {
    new SingletonIteratorTester() {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.concat(iterateOver(), iterateOver(1), iterateOver());
      }
    }.test();
  }

  public void testConcatSingletonAndSingletonYieldsDoubleton()
      throws Exception {
    new DoubletonIteratorTester() {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.concat(iterateOver(1), iterateOver(2));
      }
    }.test();
  }

  public void testConcatSingletonAndSingletonWithEmptiesYieldsDoubleton()
      throws Exception {
    new DoubletonIteratorTester() {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.concat(
            iterateOver(1), iterateOver(), iterateOver(), iterateOver(2));
      }
    }.test();
  }

  public void testConcatUnmodifiable() throws Exception {
    new IteratorTester<Integer>(5, UNMODIFIABLE, asList(1, 2),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.concat(asList(1).iterator(),
            Arrays.<Integer>asList().iterator(), asList(2).iterator());
      }
    }.test();
  }

  /**
   * Illustrates the somewhat bizarre behavior when a null is passed in.
   */
  public void testConcatContainingNull() {
    @SuppressWarnings("unchecked")
    Iterator<Iterator<Integer>> input
        = asList(iterateOver(1, 2), null, iterateOver(3)).iterator();
    Iterator<Integer> result = Iterators.concat(input);
    assertEquals(1, (int) result.next());
    assertEquals(2, (int) result.next());
    try {
      result.hasNext();
      fail("no exception thrown");
    } catch (NullPointerException e) {
    }
    try {
      result.next();
      fail("no exception thrown");
    } catch (NullPointerException e) {
    }
    // There is no way to get "through" to the 3.  Buh-bye
  }

  @SuppressWarnings("unchecked")
  public void testConcatVarArgsContainingNull() {
    try {
      Iterators.concat(iterateOver(1, 2), null, iterateOver(3), iterateOver(4),
          iterateOver(5));
      fail("no exception thrown");
    } catch (NullPointerException e) {
    }
  }

  public void testAddAllWithEmptyIterator() {
    List<String> alreadyThere = Lists.newArrayList("already", "there");

    boolean changed = Iterators.addAll(alreadyThere,
                                       Iterators.<String>emptyIterator());
    assertContentsInOrder(alreadyThere, "already", "there");
    assertFalse(changed);
  }

  public void testAddAllToList() {
    List<String> alreadyThere = Lists.newArrayList("already", "there");
    List<String> freshlyAdded = Lists.newArrayList("freshly", "added");

    boolean changed = Iterators.addAll(alreadyThere, freshlyAdded.iterator());

    assertContentsInOrder(alreadyThere, "already", "there", "freshly", "added");
    assertTrue(changed);
  }

  public void testAddAllToSet() {
    Set<String> alreadyThere
        = Sets.newLinkedHashSet(asList("already", "there"));
    List<String> oneMore = Lists.newArrayList("there");

    boolean changed = Iterators.addAll(alreadyThere, oneMore.iterator());
    assertContentsInOrder(alreadyThere, "already", "there");
    assertFalse(changed);
  }

  public void testNullPointerExceptions() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(Iterators.class);
  }

  private static abstract class EmptyIteratorTester
      extends IteratorTester<Integer> {
    protected EmptyIteratorTester() {
      super(3, MODIFIABLE, Collections.<Integer>emptySet(),
          IteratorTester.KnownOrder.KNOWN_ORDER);
    }
  }

  private static abstract class SingletonIteratorTester
      extends IteratorTester<Integer> {
    protected SingletonIteratorTester() {
      super(3, MODIFIABLE, singleton(1), IteratorTester.KnownOrder.KNOWN_ORDER);
    }
  }

  private static abstract class DoubletonIteratorTester
      extends IteratorTester<Integer> {
    protected DoubletonIteratorTester() {
      super(5, MODIFIABLE, newArrayList(1, 2),
          IteratorTester.KnownOrder.KNOWN_ORDER);
    }
  }

  private static Iterator<Integer> iterateOver(final Integer... values) {
    return newArrayList(values).iterator();
  }

  public void testElementsEqual() {
    Iterable<?> a;
    Iterable<?> b;

    // Base case.
    a = Lists.newArrayList();
    b = Collections.emptySet();
    assertTrue(Iterators.elementsEqual(a.iterator(), b.iterator()));

    // A few elements.
    a = asList(4, 8, 15, 16, 23, 42);
    b = asList(4, 8, 15, 16, 23, 42);
    assertTrue(Iterators.elementsEqual(a.iterator(), b.iterator()));

    // The same, but with nulls.
    a = asList(4, 8, null, 16, 23, 42);
    b = asList(4, 8, null, 16, 23, 42);
    assertTrue(Iterators.elementsEqual(a.iterator(), b.iterator()));

    // Different Iterable types (still equal elements, though).
    a = ImmutableList.of(4, 8, 15, 16, 23, 42);
    b = asList(4, 8, 15, 16, 23, 42);
    assertTrue(Iterators.elementsEqual(a.iterator(), b.iterator()));

    // An element differs.
    a = asList(4, 8, 15, 12, 23, 42);
    b = asList(4, 8, 15, 16, 23, 42);
    assertFalse(Iterators.elementsEqual(a.iterator(), b.iterator()));

    // null versus non-null.
    a = asList(4, 8, 15, null, 23, 42);
    b = asList(4, 8, 15, 16, 23, 42);
    assertFalse(Iterators.elementsEqual(a.iterator(), b.iterator()));
    assertFalse(Iterators.elementsEqual(b.iterator(), a.iterator()));

    // Different lengths.
    a = asList(4, 8, 15, 16, 23);
    b = asList(4, 8, 15, 16, 23, 42);
    assertFalse(Iterators.elementsEqual(a.iterator(), b.iterator()));
    assertFalse(Iterators.elementsEqual(b.iterator(), a.iterator()));

    // Different lengths, one is empty.
    a = Collections.emptySet();
    b = asList(4, 8, 15, 16, 23, 42);
    assertFalse(Iterators.elementsEqual(a.iterator(), b.iterator()));
    assertFalse(Iterators.elementsEqual(b.iterator(), a.iterator()));
  }

  public void testPartition_badSize() {
    Iterator<Integer> source = Iterators.singletonIterator(1);
    try {
      Iterators.partition(source, 0);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testPartition_empty() {
    Iterator<Integer> source = Iterators.emptyIterator();
    Iterator<List<Integer>> partitions = Iterators.partition(source, 1);
    assertFalse(partitions.hasNext());
  }

  public void testPartition_singleton1() {
    Iterator<Integer> source = Iterators.singletonIterator(1);
    Iterator<List<Integer>> partitions = Iterators.partition(source, 1);
    assertTrue(partitions.hasNext());
    assertTrue(partitions.hasNext());
    assertEquals(ImmutableList.of(1), partitions.next());
    assertFalse(partitions.hasNext());
  }

  public void testPartition_singleton2() {
    Iterator<Integer> source = Iterators.singletonIterator(1);
    Iterator<List<Integer>> partitions = Iterators.partition(source, 2);
    assertTrue(partitions.hasNext());
    assertTrue(partitions.hasNext());
    assertEquals(ImmutableList.of(1), partitions.next());
    assertFalse(partitions.hasNext());
  }

  public void testPartition_general() throws Exception {
    new IteratorTester<List<Integer>>(5,
        IteratorFeature.UNMODIFIABLE,
        ImmutableList.of(
            asList(1, 2, 3),
            asList(4, 5, 6),
            asList(7)),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<List<Integer>> newTargetIterator() {
        Iterator<Integer> source = Iterators.forArray(1, 2, 3, 4, 5, 6, 7);
        return Iterators.partition(source, 3);
      }
    }.test();
  }

  public void testPartition_view() {
    List<Integer> list = asList(1, 2);
    Iterator<List<Integer>> partitions
        = Iterators.partition(list.iterator(), 1);

    // Changes before the partition is retrieved are reflected
    list.set(0, 3);
    List<Integer> first = partitions.next();

    // Changes after are not
    list.set(0, 4);

    assertEquals(ImmutableList.of(3), first);
  }

  public void testPartitionRandomAccess() {
    Iterator<Integer> source = asList(1, 2, 3).iterator();
    Iterator<List<Integer>> partitions = Iterators.partition(source, 2);
    assertTrue(partitions.next() instanceof RandomAccess);
    assertTrue(partitions.next() instanceof RandomAccess);
  }

  public void testPaddedPartition_badSize() {
    Iterator<Integer> source = Iterators.singletonIterator(1);
    try {
      Iterators.paddedPartition(source, 0);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testPaddedPartition_empty() {
    Iterator<Integer> source = Iterators.emptyIterator();
    Iterator<List<Integer>> partitions = Iterators.paddedPartition(source, 1);
    assertFalse(partitions.hasNext());
  }

  public void testPaddedPartition_singleton1() {
    Iterator<Integer> source = Iterators.singletonIterator(1);
    Iterator<List<Integer>> partitions = Iterators.paddedPartition(source, 1);
    assertTrue(partitions.hasNext());
    assertTrue(partitions.hasNext());
    assertEquals(ImmutableList.of(1), partitions.next());
    assertFalse(partitions.hasNext());
  }

  public void testPaddedPartition_singleton2() {
    Iterator<Integer> source = Iterators.singletonIterator(1);
    Iterator<List<Integer>> partitions = Iterators.paddedPartition(source, 2);
    assertTrue(partitions.hasNext());
    assertTrue(partitions.hasNext());
    assertEquals(asList(1, null), partitions.next());
    assertFalse(partitions.hasNext());
  }

  public void testPaddedPartition_general() throws Exception {
    new IteratorTester<List<Integer>>(5,
        IteratorFeature.UNMODIFIABLE,
        ImmutableList.of(
            asList(1, 2, 3),
            asList(4, 5, 6),
            asList(7, null, null)),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<List<Integer>> newTargetIterator() {
        Iterator<Integer> source = Iterators.forArray(1, 2, 3, 4, 5, 6, 7);
        return Iterators.paddedPartition(source, 3);
      }
    }.test();
  }

  public void testPaddedPartition_view() {
    List<Integer> list = asList(1, 2);
    Iterator<List<Integer>> partitions
        = Iterators.paddedPartition(list.iterator(), 1);

    // Changes before the PaddedPartition is retrieved are reflected
    list.set(0, 3);
    List<Integer> first = partitions.next();

    // Changes after are not
    list.set(0, 4);

    assertEquals(ImmutableList.of(3), first);
  }

  public void testPaddedPartitionRandomAccess() {
    Iterator<Integer> source = asList(1, 2, 3).iterator();
    Iterator<List<Integer>> partitions = Iterators.paddedPartition(source, 2);
    assertTrue(partitions.next() instanceof RandomAccess);
    assertTrue(partitions.next() instanceof RandomAccess);
  }

  public void testForArrayEmpty() {
    String[] array = new String[0];
    Iterator<String> iterator = Iterators.forArray(array);
    assertFalse(iterator.hasNext());
    try {
      iterator.next();
      fail();
    } catch (NoSuchElementException expected) {}
  }

  public void testForArrayTypical() {
    String[] array = {"foo", "bar"};
    Iterator<String> iterator = Iterators.forArray(array);
    assertTrue(iterator.hasNext());
    assertEquals("foo", iterator.next());
    assertTrue(iterator.hasNext());
    try {
      iterator.remove();
      fail();
    } catch (UnsupportedOperationException expected) {}
    assertEquals("bar", iterator.next());
    assertFalse(iterator.hasNext());
    try {
      iterator.next();
      fail();
    } catch (NoSuchElementException expected) {}
  }

  public void testForArrayOffset() {
    String[] array = {"foo", "bar", "cat", "dog"};
    Iterator<String> iterator = Iterators.forArray(array, 1, 2);
    assertTrue(iterator.hasNext());
    assertEquals("bar", iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals("cat", iterator.next());
    assertFalse(iterator.hasNext());
    try {
      Iterators.forArray(array, 2, 3);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
  }

  public void testForArrayLength0() {
    String[] array = {"foo", "bar"};
    assertFalse(Iterators.forArray(array, 0, 0).hasNext());
    assertFalse(Iterators.forArray(array, 1, 0).hasNext());
    assertFalse(Iterators.forArray(array, 2, 0).hasNext());
    try {
      Iterators.forArray(array, -1, 0);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
    try {
      Iterators.forArray(array, 3, 0);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
  }

  public void testForArrayUsingTester() throws Exception {
    new IteratorTester<Integer>(6, UNMODIFIABLE, asList(1, 2, 3),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.forArray(1, 2, 3);
      }
    }.test();
  }

  public void testForArrayWithOffsetUsingTester() throws Exception {
    new IteratorTester<Integer>(6, UNMODIFIABLE, asList(1, 2, 3),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.forArray(new Integer[] { 0, 1, 2, 3, 4 }, 1, 3);
      }
    }.test();
  }

  public void testForEnumerationEmpty() {
    Enumeration<Integer> enumer = enumerate();
    Iterator<Integer> iter = Iterators.forEnumeration(enumer);

    assertFalse(iter.hasNext());
    try {
      iter.next();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testForEnumerationSingleton() {
    Enumeration<Integer> enumer = enumerate(1);
    Iterator<Integer> iter = Iterators.forEnumeration(enumer);

    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());
    assertEquals(1, (int) iter.next());
    try {
      iter.remove();
      fail();
    } catch (UnsupportedOperationException expected) {
    }
    assertFalse(iter.hasNext());
    try {
      iter.next();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testForEnumerationTypical() {
    Enumeration<Integer> enumer = enumerate(1, 2, 3);
    Iterator<Integer> iter = Iterators.forEnumeration(enumer);

    assertTrue(iter.hasNext());
    assertEquals(1, (int) iter.next());
    assertTrue(iter.hasNext());
    assertEquals(2, (int) iter.next());
    assertTrue(iter.hasNext());
    assertEquals(3, (int) iter.next());
    assertFalse(iter.hasNext());
  }

  public void testAsEnumerationEmpty() {
    Iterator<Integer> iter = Iterators.emptyIterator();
    Enumeration<Integer> enumer = Iterators.asEnumeration(iter);

    assertFalse(enumer.hasMoreElements());
    try {
      enumer.nextElement();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testAsEnumerationSingleton() {
    Iterator<Integer> iter = ImmutableList.of(1).iterator();
    Enumeration<Integer> enumer = Iterators.asEnumeration(iter);

    assertTrue(enumer.hasMoreElements());
    assertTrue(enumer.hasMoreElements());
    assertEquals(1, (int) enumer.nextElement());
    assertFalse(enumer.hasMoreElements());
    try {
      enumer.nextElement();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testAsEnumerationTypical() {
    Iterator<Integer> iter = ImmutableList.of(1, 2, 3).iterator();
    Enumeration<Integer> enumer = Iterators.asEnumeration(iter);

    assertTrue(enumer.hasMoreElements());
    assertEquals(1, (int) enumer.nextElement());
    assertTrue(enumer.hasMoreElements());
    assertEquals(2, (int) enumer.nextElement());
    assertTrue(enumer.hasMoreElements());
    assertEquals(3, (int) enumer.nextElement());
    assertFalse(enumer.hasMoreElements());
  }

  private static Enumeration<Integer> enumerate(Integer... ints) {
    Vector<Integer> vector = new Vector<Integer>();
    vector.addAll(asList(ints));
    return vector.elements();
  }

  public void testToString() {
    List<String> list = Collections.emptyList();
    assertEquals("[]", Iterators.toString(list.iterator()));

    list = Lists.newArrayList("yam", "bam", "jam", "ham");
    assertEquals("[yam, bam, jam, ham]", Iterators.toString(list.iterator()));
  }

  public void testGetLast_basic() {
    List<String> list = newArrayList();
    list.add("a");
    list.add("b");
    assertEquals("b", getLast(list.iterator()));
  }

  public void testGetLast_exception() {
    List<String> list = newArrayList();
    try {
      getLast(list.iterator());
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testGet_basic() {
    List<String> list = newArrayList();
    list.add("a");
    list.add("b");
    Iterator<String> iterator = list.iterator();
    assertEquals("b", get(iterator, 1));
    assertFalse(iterator.hasNext());
  }

  public void testGet_atSize() {
    List<String> list = newArrayList();
    list.add("a");
    list.add("b");
    Iterator<String> iterator = list.iterator();
    try {
      get(iterator, 2);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
    assertFalse(iterator.hasNext());
  }

  public void testGet_pastEnd() {
    List<String> list = newArrayList();
    list.add("a");
    list.add("b");
    Iterator<String> iterator = list.iterator();
    try {
      get(iterator, 5);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
    assertFalse(iterator.hasNext());
  }

  public void testGet_empty() {
    List<String> list = newArrayList();
    Iterator<String> iterator = list.iterator();
    try {
      get(iterator, 0);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
    assertFalse(iterator.hasNext());
  }

  public void testGet_negativeIndex() {
    List<String> list = newArrayList("a", "b", "c");
    Iterator<String> iterator = list.iterator();
    try {
      get(iterator, -1);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
  }

  public void testFrequency() {
    List<String> list = newArrayList("a", null, "b", null, "a", null);
    assertEquals(2, Iterators.frequency(list.iterator(), "a"));
    assertEquals(1, Iterators.frequency(list.iterator(), "b"));
    assertEquals(0, Iterators.frequency(list.iterator(), "c"));
    assertEquals(0, Iterators.frequency(list.iterator(), 4.2));
    assertEquals(3, Iterators.frequency(list.iterator(), null));
  }

  public void testSingletonIterator() throws Exception {
    new IteratorTester<Integer>(
        3, UNMODIFIABLE, singleton(1), IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterators.singletonIterator(1);
      }
    }.test();
  }

  public void testRemoveAll() {
    List<String> list = newArrayList("a", "b", "c", "d", "e");
    assertTrue(Iterators.removeAll(
        list.iterator(), newArrayList("b", "d", "f")));
    assertEquals(newArrayList("a", "c", "e"), list);
    assertFalse(Iterators.removeAll(
        list.iterator(), newArrayList("x", "y", "z")));
    assertEquals(newArrayList("a", "c", "e"), list);
  }

  public void testRemoveIf() {
    List<String> list = newArrayList("a", "b", "c", "d", "e");
    assertTrue(Iterators.removeIf(
        list.iterator(),
        new Predicate<String>() {
          public boolean apply(String s) {
            return s.equals("b") || s.equals("d") || s.equals("f");
          }
        }));
    assertEquals(newArrayList("a", "c", "e"), list);
    assertFalse(Iterators.removeIf(
        list.iterator(),
        new Predicate<String>() {
          public boolean apply(String s) {
            return s.equals("x") || s.equals("y") || s.equals("z");
          }
        }));
    assertEquals(newArrayList("a", "c", "e"), list);
  }

  public void testRetainAll() {
    List<String> list = newArrayList("a", "b", "c", "d", "e");
    assertTrue(Iterators.retainAll(
        list.iterator(), newArrayList("b", "d", "f")));
    assertEquals(newArrayList("b", "d"), list);
    assertFalse(Iterators.retainAll(
        list.iterator(), newArrayList("b", "e", "d")));
    assertEquals(newArrayList("b", "d"), list);
  }

  private static Test testsForRemoveAllAndRetainAll() {
    return ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override public List<String> create(final String[] elements) {
            final List<String> delegate = newArrayList(elements);
            return new ForwardingList<String>() {
              @Override protected List<String> delegate() {
                return delegate;
              }

              @Override public boolean removeAll(Collection<?> c) {
                return Iterators.removeAll(iterator(), c);
              }

              @Override public boolean retainAll(Collection<?> c) {
                return Iterators.retainAll(iterator(), c);
              }
            };
          }
        })
        .named("ArrayList with Iterators.removeAll and retainAll")
        .withFeatures(
            ListFeature.GENERAL_PURPOSE,
            CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionSize.ANY)
        .createTestSuite();
  }

  public void testRecursiveCallsToPeekingIteratorShouldAvoidRewrapping() {
    Iterator<Integer> iterator = newArrayList(1, 2, 3).iterator();
    // Should be able to make a PeekingIterator<T> from an Iterator<? extends T>
    PeekingIterator<Number> first = Iterators.<Number>peekingIterator(iterator);
    PeekingIterator<Number> second = Iterators.peekingIterator(first);
    assertSame("Should not rewrap iterator returned by peekingIterator()",
        first, second);
  }
}