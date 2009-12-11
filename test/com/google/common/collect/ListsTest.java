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
import com.google.common.base.Functions;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.testing.IteratorFeature.UNMODIFIABLE;
import com.google.common.collect.testing.IteratorTester;
import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import com.google.common.testutils.NullPointerTester;
import com.google.common.testutils.SerializableTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.easymock.classextension.EasyMock;

import java.io.Serializable;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * Unit test for {@code Lists}.
 *
 * @author Kevin Bourrillion
 * @author Mike Bostock
 * @author Jared Levy
 */
public class ListsTest extends TestCase {

  private static final Collection<Integer> SOME_COLLECTION
      = asList(0, 1, 1);

  private static final Iterable<Integer> SOME_ITERABLE = new SomeIterable();

  private static class SomeIterable implements Iterable<Integer>, Serializable {
    public Iterator<Integer> iterator() {
      return SOME_COLLECTION.iterator();
    }
    private static final long serialVersionUID = 0;
  }

  private static final List<Integer> SOME_LIST
      = Lists.newArrayList(1, 2, 3, 4);

  private static final List<Integer> SOME_SEQUENTIAL_LIST
      = Lists.newLinkedList(asList(1, 2, 3, 4));

  private static final List<String> SOME_STRING_LIST
      = asList("1", "2", "3", "4");

  private static final Function<Number, String> SOME_FUNCTION
      = new SomeFunction();

  private static class SomeFunction
      implements Function<Number, String>, Serializable {
    public String apply(Number n) {
      return String.valueOf(n);
    }
    private static final long serialVersionUID = 0;
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(ListsTest.class);

    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            String[] rest = new String[elements.length - 1];
            System.arraycopy(elements, 1, rest, 0, elements.length - 1);
            return Lists.asList(elements[0], rest);
          }
        })
        .named("Lists.asList, 2 parameter")
        .withFeatures(CollectionSize.SEVERAL, CollectionSize.ONE,
            CollectionFeature.ALLOWS_NULL_VALUES)
        .createTestSuite());

    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            String[] rest = new String[elements.length - 2];
            System.arraycopy(elements, 2, rest, 0, elements.length - 2);
            return Lists.asList(elements[0], elements[1], rest);
          }
        })
        .named("Lists.asList, 3 parameter")
        .withFeatures(CollectionSize.SEVERAL,
            CollectionFeature.ALLOWS_NULL_VALUES)
        .createTestSuite());

    final Function<String, String> removeFirst
        = new Function<String, String>() {
            public String apply(String from) {
              return (from.length() == 0) ? from : from.substring(1);
            }
          };

    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            List<String> fromList = Lists.newArrayList();
            for (String element : elements) {
              fromList.add("q" + checkNotNull(element));
            }
            return Lists.transform(fromList, removeFirst);
          }
        })
        .named("Lists.transform, random access, no nulls")
        .withFeatures(CollectionSize.ANY,
            ListFeature.REMOVE_OPERATIONS)
        .createTestSuite());

    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            List<String> fromList = Lists.newLinkedList();
            for (String element : elements) {
              fromList.add("q" + checkNotNull(element));
            }
            return Lists.transform(fromList, removeFirst);
          }
        })
        .named("Lists.transform, sequential access, no nulls")
        .withFeatures(CollectionSize.ANY,
            ListFeature.REMOVE_OPERATIONS)
        .createTestSuite());

    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            List<String> fromList = Lists.newArrayList(elements);
            return Lists.transform(fromList, Functions.<String>identity());
          }
        })
        .named("Lists.transform, random access, nulls")
        .withFeatures(CollectionSize.ANY,
            ListFeature.REMOVE_OPERATIONS,
            CollectionFeature.ALLOWS_NULL_VALUES)
        .createTestSuite());

    suite.addTest(ListTestSuiteBuilder.using(new TestStringListGenerator() {
          @Override protected List<String> create(String[] elements) {
            List<String> fromList =
                Lists.newLinkedList(asList(elements));
            return Lists.transform(fromList, Functions.<String>identity());
          }
        })
        .named("Lists.transform, sequential access, nulls")
        .withFeatures(CollectionSize.ANY,
            ListFeature.REMOVE_OPERATIONS,
            CollectionFeature.ALLOWS_NULL_VALUES)
        .createTestSuite());

    return suite;
  }

  public void testIllustrateVarargsWeirdness() {
    String[] array = { "foo", "bar" };

    List<String> list = ImmutableList.of(array);

    // Fortunately, this does what the caller clearly intended
    assertEquals(2, list.size());

    // If the list-of-array is desired, a special method type parameter must be
    // inserted or the code won't compile.
    List<String[]> listOfArray = ImmutableList.<String[]>of(array);

    // This also behaves as expected
    assertEquals(1, listOfArray.size());

    // It's pretty strange that the selection of overload can depend on the
    // type parameter given.  But what if raw types are used?
    @SuppressWarnings("unchecked")
    List whatIsThis = ImmutableList.of(array);

    // In most cases I think this will be what the caller intended.  If not...
    // well, ya shoulda used the generics, bub.
    assertEquals(2, whatIsThis.size());

    // Unfortunately, the below will blow up at runtime
    // But hey, can't say the compiler didn't warn ya
    try {
      ImmutableList.of((String[]) null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testNewArrayListEmpty() {
    ArrayList<Integer> list = Lists.newArrayList();
    assertEquals(Collections.emptyList(), list);
  }

  public void testNewArrayListWithCapacity() {
    ArrayList<Integer> list = Lists.newArrayListWithCapacity(0);
    assertEquals(Collections.emptyList(), list);

    ArrayList<Integer> bigger = Lists.newArrayListWithCapacity(256);
    assertEquals(Collections.emptyList(), bigger);
  }

  public void testNewArrayListWithCapacity_negative() {
    try {
      Lists.newArrayListWithCapacity(-1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testNewArrayListWithExpectedSize() {
    ArrayList<Integer> list = Lists.newArrayListWithExpectedSize(0);
    assertEquals(Collections.emptyList(), list);

    ArrayList<Integer> bigger = Lists.newArrayListWithExpectedSize(256);
    assertEquals(Collections.emptyList(), bigger);
  }

  public void testNewArrayListWithExpectedSize_negative() {
    try {
      Lists.newArrayListWithExpectedSize(-1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testNewArrayListVarArgs() {
    ArrayList<Integer> list = Lists.newArrayList(0, 1, 1);
    assertEquals(SOME_COLLECTION, list);
  }

  public void testComputeArrayListCapacity() {
    assertEquals(5, Lists.computeArrayListCapacity(0));
    assertEquals(13, Lists.computeArrayListCapacity(8));
    assertEquals(89, Lists.computeArrayListCapacity(77));
    assertEquals(22000005, Lists.computeArrayListCapacity(20000000));
    assertEquals(Integer.MAX_VALUE,
        Lists.computeArrayListCapacity(Integer.MAX_VALUE - 1000));
  }

  public void testNewArrayListFromCollection() {
    ArrayList<Integer> list = Lists.newArrayList(SOME_COLLECTION);
    assertEquals(SOME_COLLECTION, list);
  }

  public void testNewArrayListFromIterable() {
    ArrayList<Integer> list = Lists.newArrayList(SOME_ITERABLE);
    assertEquals(SOME_COLLECTION, list);
  }

  public void testNewArrayListFromIterator() {
    ArrayList<Integer> list = Lists.newArrayList(SOME_COLLECTION.iterator());
    assertEquals(SOME_COLLECTION, list);
  }

  public void testNewLinkedListEmpty() {
    LinkedList<Integer> list = Lists.newLinkedList();
    assertEquals(Collections.emptyList(), list);
  }

  public void testNewLinkedListFromCollection() {
    LinkedList<Integer> list = Lists.newLinkedList(SOME_COLLECTION);
    assertEquals(SOME_COLLECTION, list);
  }

  public void testNewLinkedListFromIterable() {
    LinkedList<Integer> list = Lists.newLinkedList(SOME_ITERABLE);
    assertEquals(SOME_COLLECTION, list);
  }

  public void testNullPointerExceptions() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(Lists.class);
  }

  /**
   * This is just here to illustrate how {@code Arrays#asList} differs from
   * {@code Lists#newArrayList}.
   */
  public void testArraysAsList() {
    List<String> ourWay = Lists.newArrayList("foo", "bar", "baz");
    List<String> otherWay = asList("foo", "bar", "baz");

    // They're logically equal
    assertEquals(ourWay, otherWay);

    // The result of Arrays.asList() is mutable
    otherWay.set(0, "FOO");
    assertEquals("FOO", otherWay.get(0));

    // But it can't grow
    try {
      otherWay.add("nope");
      fail("no exception thrown");
    } catch (UnsupportedOperationException expected) {
    }

    // And it can't shrink
    try {
      otherWay.remove(2);
      fail("no exception thrown");
    } catch (UnsupportedOperationException expected) {
    }
  }

  public void testAsList1() throws Exception {
    List<String> list = Lists.asList("foo", new String[] { "bar", "baz" });
    checkFooBarBazList(list);
    SerializableTester.reserializeAndAssert(list);
    assertTrue(list instanceof RandomAccess);

    new IteratorTester<String>(5, UNMODIFIABLE,
        asList("foo", "bar", "baz"),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<String> newTargetIterator() {
        return Lists.asList("foo", new String[] {"bar", "baz"}).iterator();
      }
    }.test();
  }

  private void checkFooBarBazList(List<String> list) {
    assertContentsInOrder(list, "foo", "bar", "baz");
    assertEquals(3, list.size());
    assertIndexIsOutOfBounds(list, -1);
    assertEquals("foo", list.get(0));
    assertEquals("bar", list.get(1));
    assertEquals("baz", list.get(2));
    assertIndexIsOutOfBounds(list, 3);
  }

  public void testAsList1Small() throws Exception {
    List<String> list = Lists.asList("foo", new String[0]);
    assertContentsInOrder(list, "foo");
    assertEquals(1, list.size());
    assertIndexIsOutOfBounds(list, -1);
    assertEquals("foo", list.get(0));
    assertIndexIsOutOfBounds(list, 1);
    assertTrue(list instanceof RandomAccess);

    new IteratorTester<String>(3, UNMODIFIABLE, singletonList("foo"),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<String> newTargetIterator() {
        return Lists.asList("foo", new String[0]).iterator();
      }
    }.test();
  }

  public void testAsList2() throws Exception {
    List<String> list = Lists.asList("foo", "bar", new String[] { "baz" });
    checkFooBarBazList(list);
    assertTrue(list instanceof RandomAccess);

    new IteratorTester<String>(5, UNMODIFIABLE, asList("foo", "bar",
        "baz"), IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<String> newTargetIterator() {
        return Lists.asList("foo", "bar", new String[] {"baz"}).iterator();
      }
    }.test();
  }

  public void testAsList2Small() throws Exception {
    List<String> list = Lists.asList("foo", "bar", new String[0]);
    assertContentsInOrder(list, "foo", "bar");
    assertEquals(2, list.size());
    assertIndexIsOutOfBounds(list, -1);
    assertEquals("foo", list.get(0));
    assertEquals("bar", list.get(1));
    assertIndexIsOutOfBounds(list, 2);
    SerializableTester.reserializeAndAssert(list);
    assertTrue(list instanceof RandomAccess);

    new IteratorTester<String>(5, UNMODIFIABLE, asList("foo", "bar"),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<String> newTargetIterator() {
        return Lists.asList("foo", "bar", new String[0]).iterator();
      }
    }.test();
  }

  private static void assertIndexIsOutOfBounds(List<String> list, int index) {
    try {
      list.get(index);
      fail();
    } catch (IndexOutOfBoundsException expected) {
    }
  }

  public void testTransformEqualityRandomAccess() {
    List<String> list = Lists.transform(SOME_LIST, SOME_FUNCTION);
    assertEquals(SOME_STRING_LIST, list);
    SerializableTester.reserializeAndAssert(list);
  }

  public void testTransformEqualitySequential() {
    List<String> list = Lists.transform(SOME_SEQUENTIAL_LIST, SOME_FUNCTION);
    assertEquals(SOME_STRING_LIST, list);
    SerializableTester.reserializeAndAssert(list);
  }

  public void testTransformHashCodeRandomAccess() {
    List<String> list = Lists.transform(SOME_LIST, SOME_FUNCTION);
    assertEquals(SOME_STRING_LIST.hashCode(), list.hashCode());
  }

  public void testTransformHashCodeSequential() {
    List<String> list = Lists.transform(SOME_SEQUENTIAL_LIST, SOME_FUNCTION);
    assertEquals(SOME_STRING_LIST.hashCode(), list.hashCode());
  }

  public void testTransformModifiableRandomAccess() {
    List<Integer> fromList = Lists.newArrayList(SOME_LIST);
    List<String> list = Lists.transform(fromList, SOME_FUNCTION);
    assertTransformModifiable(list);
  }

  public void testTransformModifiableSequential() {
    List<Integer> fromList = Lists.newLinkedList(SOME_SEQUENTIAL_LIST);
    List<String> list = Lists.transform(fromList, SOME_FUNCTION);
    assertTransformModifiable(list);
  }

  private static void assertTransformModifiable(List<String> list) {
    try {
      list.add("5");
      fail("transformed list is addable");
    } catch (UnsupportedOperationException expected) {}
    list.remove(0);
    assertEquals(asList("2", "3", "4"), list);
    list.remove("3");
    assertEquals(asList("2", "4"), list);
    try {
      list.set(0, "5");
      fail("transformed list is setable");
    } catch (UnsupportedOperationException expected) {}
    list.clear();
    assertEquals(Collections.emptyList(), list);
  }

  public void testTransformViewRandomAccess() {
    List<Integer> fromList = Lists.newArrayList(SOME_LIST);
    List<String> toList = Lists.transform(fromList, SOME_FUNCTION);
    assertTransformView(fromList, toList);
  }

  public void testTransformViewSequential() {
    List<Integer> fromList = Lists.newLinkedList(SOME_SEQUENTIAL_LIST);
    List<String> toList = Lists.transform(fromList, SOME_FUNCTION);
    assertTransformView(fromList, toList);
  }

  private static void assertTransformView(List<Integer> fromList,
      List<String> toList) {
    /* fromList modifications reflected in toList */
    fromList.set(0, 5);
    assertEquals(asList("5", "2", "3", "4"), toList);
    fromList.add(6);
    assertEquals(asList("5", "2", "3", "4", "6"), toList);
    fromList.remove(Integer.valueOf(2));
    assertEquals(asList("5", "3", "4", "6"), toList);
    fromList.remove(2);
    assertEquals(asList("5", "3", "6"), toList);

    /* toList modifications reflected in fromList */
    toList.remove(2);
    assertEquals(asList(5, 3), fromList);
    toList.remove("5");
    assertEquals(asList(3), fromList);
    toList.clear();
    assertEquals(Collections.emptyList(), fromList);
  }

  public void testTransformRandomAccess() {
    List<String> list = Lists.transform(SOME_LIST, SOME_FUNCTION);
    assertTrue(list instanceof RandomAccess);
  }

  public void testTransformSequential() {
    List<String> list = Lists.transform(SOME_SEQUENTIAL_LIST, SOME_FUNCTION);
    assertFalse(list instanceof RandomAccess);
  }

  public void testTransformListIteratorRandomAccess() {
    List<Integer> fromList = Lists.newArrayList(SOME_LIST);
    List<String> list = Lists.transform(fromList, SOME_FUNCTION);
    assertTransformListIterator(list);
  }

  public void testTransformListIteratorSequential() {
    List<Integer> fromList = Lists.newLinkedList(SOME_SEQUENTIAL_LIST);
    List<String> list = Lists.transform(fromList, SOME_FUNCTION);
    assertTransformListIterator(list);
  }

  private static void assertTransformListIterator(List<String> list) {
    ListIterator<String> iterator = list.listIterator(1);
    assertEquals(1, iterator.nextIndex());
    assertEquals("2", iterator.next());
    assertEquals("3", iterator.next());
    assertEquals("4", iterator.next());
    assertEquals(4, iterator.nextIndex());
    try {
      iterator.next();
      fail("did not detect end of list");
    } catch (NoSuchElementException expected) {}
    assertEquals(3, iterator.previousIndex());
    assertEquals("4", iterator.previous());
    assertEquals("3", iterator.previous());
    assertEquals("2", iterator.previous());
    assertTrue(iterator.hasPrevious());
    assertEquals("1", iterator.previous());
    assertFalse(iterator.hasPrevious());
    assertEquals(-1, iterator.previousIndex());
    try {
      iterator.previous();
      fail("did not detect beginning of list");
    } catch (NoSuchElementException expected) {}
    iterator.remove();
    assertEquals(asList("2", "3", "4"), list);
    assertFalse(list.isEmpty());

    // An UnsupportedOperationException or IllegalStateException may occur.
    try {
      iterator.add("1");
      fail("transformed list iterator is addable");
    } catch (UnsupportedOperationException expected) {
    } catch (IllegalStateException expected) {}
    try {
      iterator.set("1");
      fail("transformed list iterator is settable");
    } catch (UnsupportedOperationException expected) {
    } catch (IllegalStateException expected) {}
  }

  public void testTransformIteratorRandomAccess() {
    List<Integer> fromList = Lists.newArrayList(SOME_LIST);
    List<String> list = Lists.transform(fromList, SOME_FUNCTION);
    assertTransformIterator(list);
  }

  public void testTransformIteratorSequential() {
    List<Integer> fromList = Lists.newLinkedList(SOME_SEQUENTIAL_LIST);
    List<String> list = Lists.transform(fromList, SOME_FUNCTION);
    assertTransformIterator(list);
  }

  /**
   * We use this class to avoid the need to suppress generics checks with
   * easy mock.
   */
  private interface IntegerList extends List<Integer> {}

  /**
   * This test depends on the fact that {@code AbstractSequentialList.iterator}
   * transforms the {@code iterator()} call into a call on {@code
   * listIterator(int)}. This is fine because the behavior is clearly
   * documented so it's not expected to change.
   */
  public void testTransformedSequentialIterationUsesBackingListIterationOnly() {
    List<Integer> randomAccessList = Lists.newArrayList(SOME_SEQUENTIAL_LIST);
    ListIterator<Integer> sampleListIterator =
        SOME_SEQUENTIAL_LIST.listIterator();
    List<Integer> listMock = EasyMock.createMock(IntegerList.class);
    EasyMock.expect(listMock.listIterator(0)).andReturn(sampleListIterator);
    EasyMock.replay(listMock);
    List<String> transform = Lists.transform(listMock, SOME_FUNCTION);
    assertTrue(Iterables.elementsEqual(
        transform, Lists.transform(randomAccessList, SOME_FUNCTION)));
    EasyMock.verify(listMock);
  }

  private static void assertTransformIterator(List<String> list) {
    Iterator<String> iterator = list.iterator();
    assertTrue(iterator.hasNext());
    assertEquals("1", iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals("2", iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals("3", iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals("4", iterator.next());
    assertFalse(iterator.hasNext());
    try {
      iterator.next();
      fail("did not detect end of list");
    } catch (NoSuchElementException expected) {}
    iterator.remove();
    assertEquals(asList("1", "2", "3"), list);
    assertFalse(iterator.hasNext());
  }

  public void testPartition_badSize() {
    List<Integer> source = Collections.singletonList(1);
    try {
      Lists.partition(source, 0);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testPartition_empty() {
    List<Integer> source = Collections.emptyList();
    List<List<Integer>> partitions = Lists.partition(source, 1);
    assertTrue(partitions.isEmpty());
    assertEquals(0, partitions.size());
  }

  public void testPartition_1_1() {
    List<Integer> source = Collections.singletonList(1);
    List<List<Integer>> partitions = Lists.partition(source, 1);
    assertEquals(1, partitions.size());
    assertEquals(Collections.singletonList(1), partitions.get(0));
  }

  public void testPartition_1_2() {
    List<Integer> source = Collections.singletonList(1);
    List<List<Integer>> partitions = Lists.partition(source, 2);
    assertEquals(1, partitions.size());
    assertEquals(Collections.singletonList(1), partitions.get(0));
  }

  public void testPartition_2_1() {
    List<Integer> source = asList(1, 2);
    List<List<Integer>> partitions = Lists.partition(source, 1);
    assertEquals(2, partitions.size());
    assertEquals(Collections.singletonList(1), partitions.get(0));
    assertEquals(Collections.singletonList(2), partitions.get(1));
  }

  public void testPartition_3_2() {
    List<Integer> source = asList(1, 2, 3);
    List<List<Integer>> partitions = Lists.partition(source, 2);
    assertEquals(2, partitions.size());
    assertEquals(asList(1, 2), partitions.get(0));
    assertEquals(asList(3), partitions.get(1));
  }

  public void testPartitionRandomAccessTrue() {
    List<Integer> source = asList(1, 2, 3);
    List<List<Integer>> partitions = Lists.partition(source, 2);
    assertTrue(partitions instanceof RandomAccess);
    assertTrue(partitions.get(0) instanceof RandomAccess);
    assertTrue(partitions.get(1) instanceof RandomAccess);
  }

  public void testPartitionRandomAccessFalse() {
    List<Integer> source = Lists.newLinkedList(asList(1, 2, 3));
    List<List<Integer>> partitions = Lists.partition(source, 2);
    assertFalse(partitions instanceof RandomAccess);
    assertFalse(partitions.get(0) instanceof RandomAccess);
    assertFalse(partitions.get(1) instanceof RandomAccess);
  }

  // TODO: use the ListTestSuiteBuilder

  public void testPartition_view() {
    List<Integer> list = asList(1, 2, 3);
    List<List<Integer>> partitions = Lists.partition(list, 3);

    // Changes before the partition is retrieved are reflected
    list.set(0, 3);

    Iterator<List<Integer>> iterator = partitions.iterator();

    // Changes before the partition is retrieved are reflected
    list.set(1, 4);

    List<Integer> first = iterator.next();

    // Changes after are too (unlike Iterables.partition)
    list.set(2, 5);

    assertEquals(asList(3, 4, 5), first);

    // Changes to a sublist also write through to the original list
    first.set(1, 6);
    assertEquals(asList(3, 6, 5), list);
  }
}
