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
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.testing.IteratorFeature.MODIFIABLE;
import com.google.common.collect.testing.IteratorTester;
import com.google.common.testing.junit3.JUnitAsserts;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import com.google.common.testutils.NullPointerTester;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;

/**
 * Unit test for {@code Iterables}.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 */
public class IterablesTest extends TestCase {
  public void testSize0() {
    Iterable<String> iterable = Collections.emptySet();
    assertEquals(0, Iterables.size(iterable));
  }

  public void testSize1Collection() {
    Iterable<String> iterable = Collections.singleton("a");
    assertEquals(1, Iterables.size(iterable));
  }

  public void testSize2NonCollection() {
    Iterable<Integer> iterable = new Iterable<Integer>() {
      public Iterator<Integer> iterator() {
        return asList(0, 1).iterator();
      }
    };
    assertEquals(2, Iterables.size(iterable));
  }

  @SuppressWarnings("serial")
  public void testSize_collection_doesntIterate() {
    List<Integer> nums = asList(1, 2, 3, 4, 5);
    List<Integer> collection = new ArrayList<Integer>(nums) {
      @Override public Iterator<Integer> iterator() {
        fail("Don't iterate me!");
        return null;
      }
    };
    assertEquals(5, Iterables.size(collection));
  }

  private static Iterable<String> iterable(String... elements) {
    final List<String> list = asList(elements);
    return new Iterable<String>() {
      public Iterator<String> iterator() {
        return list.iterator();
      }
    };
  }

  public void test_contains_null_set_yes() {
    Iterable<String> set = Sets.newHashSet("a", null, "b");
    assertTrue(Iterables.contains(set, null));
  }

  public void test_contains_null_set_no() {
    Iterable<String> set = Sets.newHashSet("a", "b");
    assertFalse(Iterables.contains(set, null));
  }

  public void test_contains_null_iterable_yes() {
    Iterable<String> set = iterable("a", null, "b");
    assertTrue(Iterables.contains(set, null));
  }

  public void test_contains_null_iterable_no() {
    Iterable<String> set = iterable("a", "b");
    assertFalse(Iterables.contains(set, null));
  }

  public void test_contains_nonnull_set_yes() {
    Iterable<String> set = Sets.newHashSet("a", null, "b");
    assertTrue(Iterables.contains(set, "b"));
  }

  public void test_contains_nonnull_set_no() {
    Iterable<String> set = Sets.newHashSet("a", "b");
    assertFalse(Iterables.contains(set, "c"));
  }

  public void test_contains_nonnull_iterable_yes() {
    Iterable<String> set = iterable("a", null, "b");
    assertTrue(Iterables.contains(set, "b"));
  }

  public void test_contains_nonnull_iterable_no() {
    Iterable<String> set = iterable("a", "b");
    assertFalse(Iterables.contains(set, "c"));
  }

  public void testGetOnlyElement_noDefault_valid() {
    Iterable<String> iterable = Collections.singletonList("foo");
    assertEquals("foo", Iterables.getOnlyElement(iterable));
  }

  public void testGetOnlyElement_noDefault_empty() {
    Iterable<String> iterable = Collections.emptyList();
    try {
      Iterables.getOnlyElement(iterable);
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testGetOnlyElement_noDefault_multiple() {
    Iterable<String> iterable = asList("foo", "bar");
    try {
      Iterables.getOnlyElement(iterable);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testGetOnlyElement_withDefault_singleton() {
    Iterable<String> iterable = Collections.singletonList("foo");
    assertEquals("foo", Iterables.getOnlyElement(iterable, "bar"));
  }

  public void testGetOnlyElement_withDefault_empty() {
    Iterable<String> iterable = Collections.emptyList();
    assertEquals("bar", Iterables.getOnlyElement(iterable, "bar"));
  }

  public void testGetOnlyElement_withDefault_empty_null() {
    Iterable<String> iterable = Collections.emptyList();
    assertNull(Iterables.getOnlyElement(iterable, null));
  }

  public void testGetOnlyElement_withDefault_multiple() {
    Iterable<String> iterable = asList("foo", "bar");
    try {
      Iterables.getOnlyElement(iterable, "x");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testToArrayEmpty() {
    Iterable<String> iterable = Collections.emptyList();
    String[] array = Iterables.toArray(iterable, String.class);
    assertTrue(Arrays.equals(new String[0], array));
  }

  public void testToArraySingleton() {
    Iterable<String> iterable = Collections.singletonList("a");
    String[] array = Iterables.toArray(iterable, String.class);
    assertTrue(Arrays.equals(new String[] { "a" }, array));
  }

  public void testToArray() {
    String[] sourceArray = new String[] {"a", "b", "c"};
    Iterable<String> iterable = asList(sourceArray);
    String[] newArray = Iterables.toArray(iterable, String.class);
    assertTrue(Arrays.equals(sourceArray, newArray));
  }

  public void testFilter() {
    Iterable<String> unfiltered = newArrayList("foo", "bar");
    Iterable<String> filtered = Iterables.filter(unfiltered,
                                                 Predicates.equalTo("foo"));

    List<String> expected = Collections.singletonList("foo");
    List<String> actual = newArrayList(filtered);
    assertEquals(expected, actual);
    assertCanIterateAgain(filtered);
    assertEquals("[foo]", filtered.toString());
  }

  public void testAny() {
    List<String> list = newArrayList();
    Predicate<String> predicate = Predicates.equalTo("pants");

    assertFalse(Iterables.any(list, predicate));
    list.add("cool");
    assertFalse(Iterables.any(list, predicate));
    list.add("pants");
    assertTrue(Iterables.any(list, predicate));
  }

  public void testAll() {
    List<String> list = newArrayList();
    Predicate<String> predicate = Predicates.equalTo("cool");

    assertTrue(Iterables.all(list, predicate));
    list.add("cool");
    assertTrue(Iterables.all(list, predicate));
    list.add("pants");
    assertFalse(Iterables.all(list, predicate));
  }

  public void testFind() {
    Iterable<String> list = newArrayList("cool", "pants");
    assertEquals("cool", Iterables.find(list, Predicates.equalTo("cool")));
    assertEquals("pants", Iterables.find(list, Predicates.equalTo("pants")));
    try {
      Iterables.find(list, Predicates.alwaysFalse());
      fail();
    } catch (NoSuchElementException e) {
    }
    assertEquals("cool", Iterables.find(list, Predicates.alwaysTrue()));
    assertCanIterateAgain(list);
  }

  private static class TypeA {}
  private interface TypeB {}
  private static class HasBoth extends TypeA implements TypeB {}

  public void testFilterByType() throws Exception {
    HasBoth hasBoth = new HasBoth();
    Iterable<TypeA> alist =
        newArrayList(new TypeA(), new TypeA(), hasBoth, new TypeA());
    Iterable<TypeB> blist = Iterables.filter(alist, TypeB.class);
    JUnitAsserts.assertContentsInOrder(blist, hasBoth);
  }

  public void testTransform() {
    List<String> input = asList("1", "2", "3");
    Iterable<Integer> result = Iterables.transform(input,
        new Function<String, Integer>() {
          public Integer apply(String from) {
            return Integer.valueOf(from);
          }
        });

    List<Integer> actual = newArrayList(result);
    List<Integer> expected = asList(1, 2, 3);
    assertEquals(expected, actual);
    assertCanIterateAgain(result);
    assertEquals("[1, 2, 3]", result.toString());
  }

  public void testPoorlyBehavedTransform() {
    List<String> input = asList("1", null, "3");
    Iterable<Integer> result = Iterables.transform(input,
        new Function<String, Integer>() {
          public Integer apply(String from) {
            return Integer.valueOf(from);
          }
        });

    Iterator<Integer> resultIterator = result.iterator();
    resultIterator.next();

    try {
      resultIterator.next();
      fail("Expected NFE");
    } catch (NumberFormatException nfe) {
      // Expected to fail.
    }
  }

  public void testNullFriendlyTransform() {
    List<Integer> input = asList(1, 2, null, 3);
    Iterable<String> result = Iterables.transform(input,
        new Function<Integer, String>() {
          public String apply(Integer from) {
            return String.valueOf(from);
          }
        });

    List<String> actual = newArrayList(result);
    List<String> expected = asList("1", "2", "null", "3");
    assertEquals(expected, actual);
  }

  // Far less exhaustive than the tests in IteratorsTest
  public void testCycle() {
    Iterable<String> cycle = Iterables.cycle("a", "b");

    int howManyChecked = 0;
    for (String string : cycle) {
      String expected = (howManyChecked % 2 == 0) ? "a" : "b";
      assertEquals(expected, string);
      if (howManyChecked++ == 5) {
        break;
      }
    }

    // We left the last iterator pointing to "b". But a new iterator should
    // always point to "a".
    for (String string : cycle) {
      assertEquals("a", string);
      break;
    }

    assertEquals("[a, b] (cycled)", cycle.toString());
  }

  // Again, the exhaustive tests are in IteratorsTest
  public void testConcatIterable() {
    List<Integer> list1 = newArrayList(1);
    List<Integer> list2 = newArrayList(4);

    @SuppressWarnings("unchecked")
    List<List<Integer>> input = newArrayList(list1, list2);

    Iterable<Integer> result = Iterables.concat(input);
    assertEquals(asList(1, 4), newArrayList(result));

    // Now change the inputs and see result dynamically change as well

    list1.add(2);
    List<Integer> list3 = newArrayList(3);
    input.add(1, list3);

    assertEquals(asList(1, 2, 3, 4), newArrayList(result));
    assertEquals("[1, 2, 3, 4]", result.toString());
  }

  public void testConcatVarargs() {
    List<Integer> list1 = newArrayList(1);
    List<Integer> list2 = newArrayList(4);
    List<Integer> list3 = newArrayList(7, 8);
    List<Integer> list4 = newArrayList(9);
    List<Integer> list5 = newArrayList(10);
    @SuppressWarnings("unchecked")
    Iterable<Integer> result =
        Iterables.concat(list1, list2, list3, list4, list5);
    assertEquals(asList(1, 4, 7, 8, 9, 10), newArrayList(result));
    assertEquals("[1, 4, 7, 8, 9, 10]", result.toString());
  }

  public void testConcatNullPointerException() {
    List<Integer> list1 = newArrayList(1);
    List<Integer> list2 = newArrayList(4);

    try {
      Iterables.concat(list1, null, list2);
      fail();
    } catch (NullPointerException expected) {}
  }

  public void testConcatPeformingFiniteCycle() {
    Iterable<Integer> iterable = asList(1, 2, 3);
    int n = 4;
    Iterable<Integer> repeated
        = Iterables.concat(Collections.nCopies(n, iterable));
    assertContentsInOrder(repeated, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3);
  }

  public void testPartition_badSize() {
    Iterable<Integer> source = Collections.singleton(1);
    try {
      Iterables.partition(source, 0);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testPartition_empty() {
    Iterable<Integer> source = Collections.emptySet();
    Iterable<List<Integer>> partitions = Iterables.partition(source, 1);
    assertTrue(Iterables.isEmpty(partitions));
  }

  public void testPartition_singleton1() {
    Iterable<Integer> source = Collections.singleton(1);
    Iterable<List<Integer>> partitions = Iterables.partition(source, 1);
    assertEquals(1, Iterables.size(partitions));
    assertEquals(Collections.singletonList(1), partitions.iterator().next());
  }

  public void testPartition_view() {
    List<Integer> list = asList(1, 2);
    Iterable<List<Integer>> partitions = Iterables.partition(list, 2);

    // Changes before the partition is retrieved are reflected
    list.set(0, 3);

    Iterator<List<Integer>> iterator = partitions.iterator();

    // Changes before the partition is retrieved are reflected
    list.set(1, 4);

    List<Integer> first = iterator.next();

    // Changes after are not
    list.set(0, 5);

    assertEquals(ImmutableList.of(3, 4), first);
  }

  public void testPartitionRandomAccessInput() {
    Iterable<Integer> source = asList(1, 2, 3);
    Iterable<List<Integer>> partitions = Iterables.partition(source, 2);
    Iterator<List<Integer>> iterator = partitions.iterator();
    assertTrue(iterator.next() instanceof RandomAccess);
    assertTrue(iterator.next() instanceof RandomAccess);
  }

  public void testPartitionNonRandomAccessInput() {
    Iterable<Integer> source = Lists.newLinkedList(asList(1, 2, 3));
    Iterable<List<Integer>> partitions = Iterables.partition(source, 2);
    Iterator<List<Integer>> iterator = partitions.iterator();
    // Even though the input list doesn't implement RandomAccess, the output
    // lists do.
    assertTrue(iterator.next() instanceof RandomAccess);
    assertTrue(iterator.next() instanceof RandomAccess);
  }

  public void testPaddedPartition_basic() {
    List<Integer> list = asList(1, 2, 3, 4, 5);
    Iterable<List<Integer>> partitions = Iterables.paddedPartition(list, 2);
    assertEquals(3, Iterables.size(partitions));
    assertEquals(asList(5, null), Iterables.getLast(partitions));
  }

  public void testPaddedPartitionRandomAccessInput() {
    Iterable<Integer> source = asList(1, 2, 3);
    Iterable<List<Integer>> partitions = Iterables.paddedPartition(source, 2);
    Iterator<List<Integer>> iterator = partitions.iterator();
    assertTrue(iterator.next() instanceof RandomAccess);
    assertTrue(iterator.next() instanceof RandomAccess);
  }

  public void testPaddedPartitionNonRandomAccessInput() {
    Iterable<Integer> source = Lists.newLinkedList(asList(1, 2, 3));
    Iterable<List<Integer>> partitions = Iterables.paddedPartition(source, 2);
    Iterator<List<Integer>> iterator = partitions.iterator();
    // Even though the input list doesn't implement RandomAccess, the output
    // lists do.
    assertTrue(iterator.next() instanceof RandomAccess);
    assertTrue(iterator.next() instanceof RandomAccess);
  }

  // More tests in IteratorsTest
  public void testAddAllToList() {
    List<String> alreadyThere = newArrayList("already", "there");
    List<String> freshlyAdded = newArrayList("freshly", "added");

    boolean changed = Iterables.addAll(alreadyThere, freshlyAdded);
    assertContentsInOrder(alreadyThere, "already", "there", "freshly", "added");
    assertTrue(changed);
  }

  private static void assertCanIterateAgain(Iterable<?> iterable) {
    for (@SuppressWarnings("unused") Object obj : iterable) {
    }
  }

  public void testNullPointerExceptions() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(Iterables.class);
  }

  // More exhaustive tests are in IteratorsTest.
  public void testElementsEqual() throws Exception {
    Iterable<?> a;
    Iterable<?> b;

    // A few elements.
    a = asList(4, 8, 15, 16, 23, 42);
    b = asList(4, 8, 15, 16, 23, 42);
    assertTrue(Iterables.elementsEqual(a, b));

    // An element differs.
    a = asList(4, 8, 15, 12, 23, 42);
    b = asList(4, 8, 15, 16, 23, 42);
    assertFalse(Iterables.elementsEqual(a, b));

    // null versus non-null.
    a = asList(4, 8, 15, null, 23, 42);
    b = asList(4, 8, 15, 16, 23, 42);
    assertFalse(Iterables.elementsEqual(a, b));
    assertFalse(Iterables.elementsEqual(b, a));

    // Different lengths.
    a = asList(4, 8, 15, 16, 23);
    b = asList(4, 8, 15, 16, 23, 42);
    assertFalse(Iterables.elementsEqual(a, b));
    assertFalse(Iterables.elementsEqual(b, a));
  }

  public void testReversePassesIteratorsTester() throws Exception {
    new IteratorTester<Integer>(5, MODIFIABLE, newArrayList(2, 4, 6, 8),
        IteratorTester.KnownOrder.KNOWN_ORDER) {
      @Override protected Iterator<Integer> newTargetIterator() {
        return Iterables.reverse(newArrayList(8, 6, 4, 2)).iterator();
      }
    }.test();
  }

  public void testReverseWorksAsExpected() {
    String[] testStrs = new String[] {"foo", "bar", "baz"};
    Object[] expected = new Object[] {"baz", "bar", "foo"};

    List<String> stuff = ImmutableList.of(testStrs);

    Iterable<String> reversed = Iterables.reverse(stuff);
    JUnitAsserts.assertContentsInOrder(reversed, expected);
    assertEquals("[baz, bar, foo]", reversed.toString());

    List<String> removable = newArrayList("foo", "bar", "bad", "baz");

    reversed = Iterables.reverse(removable);
    JUnitAsserts.assertContentsInOrder(reversed, "baz", "bad", "bar", "foo");

    Iterator<String> reverseIter = reversed.iterator();
    assertEquals("baz", reverseIter.next());
    assertEquals("bad", reverseIter.next());
    reverseIter.remove();

    JUnitAsserts.assertContentsInOrder(reversed, expected);
    JUnitAsserts.assertContentsInOrder(reversed, expected);
  }

  public void testToString() {
    List<String> list = Collections.emptyList();
    assertEquals("[]", Iterables.toString(list));

    list = newArrayList("yam", "bam", "jam", "ham");
    assertEquals("[yam, bam, jam, ham]", Iterables.toString(list));
  }

  public void testIsEmpty() {
    Iterable<String> emptyList = Collections.emptyList();
    assertTrue(Iterables.isEmpty(emptyList));

    Iterable<String> singletonList = Collections.singletonList("foo");
    assertFalse(Iterables.isEmpty(singletonList));
  }

  private void testGetOnAbc(Iterable<String> iterable) {
    try {
      Iterables.get(iterable, -1);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
    assertEquals("a", Iterables.get(iterable, 0));
    assertEquals("b", Iterables.get(iterable, 1));
    assertEquals("c", Iterables.get(iterable, 2));
    try {
      Iterables.get(iterable, 3);
      fail();
    } catch (IndexOutOfBoundsException nsee) {}
    try {
      Iterables.get(iterable, 4);
      fail();
    } catch (IndexOutOfBoundsException nsee) {}
  }

  private void testGetOnEmpty(Iterable<String> iterable) {
    try {
      Iterables.get(iterable, 0);
      fail();
    } catch (IndexOutOfBoundsException expected) {}
  }

  public void testGet_list() {
    testGetOnAbc(newArrayList("a", "b", "c"));
  }

  public void testGet_emptyList() {
    testGetOnEmpty(Collections.<String>emptyList());
  }

  public void testGet_sortedSet() {
    testGetOnAbc(ImmutableSortedSet.of("b", "c", "a"));
  }

  public void testGet_emptySortedSet() {
    testGetOnEmpty(ImmutableSortedSet.<String>of());
  }

  public void testGet_iterable() {
    testGetOnAbc(ImmutableSet.of("a", "b", "c"));
  }

  public void testGet_emptyIterable() {
    testGetOnEmpty(Sets.<String>newHashSet());
  }

  public void testGetLast_list() {
    List<String> list = newArrayList("a", "b", "c");
    assertEquals("c", Iterables.getLast(list));
  }

  public void testGetLast_emptyList() {
    List<String> list = Collections.emptyList();
    try {
      Iterables.getLast(list);
      fail();
    } catch (NoSuchElementException e) {}
  }

  public void testGetLast_sortedSet() {
    SortedSet<String> sortedSet = ImmutableSortedSet.of("b", "c", "a");
    assertEquals("c", Iterables.getLast(sortedSet));
  }

  public void testGetLast_emptySortedSet() {
    SortedSet<String> sortedSet = ImmutableSortedSet.of();
    try {
      Iterables.getLast(sortedSet);
      fail();
    } catch (NoSuchElementException e) {}
  }

  public void testGetLast_iterable() {
    Set<String> set = ImmutableSet.of("a", "b", "c");
    assertEquals("c", Iterables.getLast(set));
  }

  public void testGetLast_emptyIterable() {
    Set<String> set = Sets.newHashSet();
    try {
      Iterables.getLast(set);
      fail();
    } catch (NoSuchElementException e) {}
  }

  public void testUnmodifiableIterable() {
    List<String> list = newArrayList("a", "b", "c");
    Iterable<String> iterable = Iterables.unmodifiableIterable(list);
    Iterator<String> iterator = iterable.iterator();
    iterator.next();
    try {
      iterator.remove();
      fail();
    } catch (UnsupportedOperationException expected) {}
    assertEquals("[a, b, c]", iterable.toString());
  }

  public void testFrequency_multiset() {
    Multiset<String> multiset
        = ImmutableMultiset.of("a", "b", "a", "c", "b", "a");
    assertEquals(3, Iterables.frequency(multiset, "a"));
    assertEquals(2, Iterables.frequency(multiset, "b"));
    assertEquals(1, Iterables.frequency(multiset, "c"));
    assertEquals(0, Iterables.frequency(multiset, "d"));
    assertEquals(0, Iterables.frequency(multiset, 4.2));
    assertEquals(0, Iterables.frequency(multiset, null));
  }

  public void testFrequency_set() {
    Set<String> set = Sets.newHashSet("a", "b", "c");
    assertEquals(1, Iterables.frequency(set, "a"));
    assertEquals(1, Iterables.frequency(set, "b"));
    assertEquals(1, Iterables.frequency(set, "c"));
    assertEquals(0, Iterables.frequency(set, "d"));
    assertEquals(0, Iterables.frequency(set, 4.2));
    assertEquals(0, Iterables.frequency(set, null));
  }

  public void testFrequency_list() {
    List<String> list = newArrayList("a", "b", "a", "c", "b", "a");
    assertEquals(3, Iterables.frequency(list, "a"));
    assertEquals(2, Iterables.frequency(list, "b"));
    assertEquals(1, Iterables.frequency(list, "c"));
    assertEquals(0, Iterables.frequency(list, "d"));
    assertEquals(0, Iterables.frequency(list, 4.2));
    assertEquals(0, Iterables.frequency(list, null));
  }

  public void testRemoveAll_collection() {
    List<String> list = newArrayList("a", "b", "c", "d", "e");
    assertTrue(Iterables.removeAll(list, newArrayList("b", "d", "f")));
    assertEquals(newArrayList("a", "c", "e"), list);
    assertFalse(Iterables.removeAll(list, newArrayList("x", "y", "z")));
    assertEquals(newArrayList("a", "c", "e"), list);
  }

  public void testRemoveAll_iterable() {
    final List<String> list = newArrayList("a", "b", "c", "d", "e");
    Iterable<String> iterable = new Iterable<String>() {
      public Iterator<String> iterator() {
        return list.iterator();
      }
    };
    assertTrue(Iterables.removeAll(iterable, newArrayList("b", "d", "f")));
    assertEquals(newArrayList("a", "c", "e"), list);
    assertFalse(Iterables.removeAll(iterable, newArrayList("x", "y", "z")));
    assertEquals(newArrayList("a", "c", "e"), list);
  }

  public void testRetainAll_collection() {
    List<String> list = newArrayList("a", "b", "c", "d", "e");
    assertTrue(Iterables.retainAll(list, newArrayList("b", "d", "f")));
    assertEquals(newArrayList("b", "d"), list);
    assertFalse(Iterables.retainAll(list, newArrayList("b", "e", "d")));
    assertEquals(newArrayList("b", "d"), list);
  }

  public void testRetainAll_iterable() {
    final List<String> list = newArrayList("a", "b", "c", "d", "e");
    Iterable<String> iterable = new Iterable<String>() {
      public Iterator<String> iterator() {
        return list.iterator();
      }
    };
    assertTrue(Iterables.retainAll(iterable, newArrayList("b", "d", "f")));
    assertEquals(newArrayList("b", "d"), list);
    assertFalse(Iterables.retainAll(iterable, newArrayList("b", "e", "d")));
    assertEquals(newArrayList("b", "d"), list);
  }

  public void testRemoveIf_randomAccess() {
    List<String> list = newArrayList("a", "b", "c", "d", "e");
    assertTrue(Iterables.removeIf(list,
        new Predicate<String>() {
          public boolean apply(String s) {
            return s.equals("b") || s.equals("d") || s.equals("f");
          }
        }));
    assertEquals(newArrayList("a", "c", "e"), list);
    assertFalse(Iterables.removeIf(list,
        new Predicate<String>() {
          public boolean apply(String s) {
            return s.equals("x") || s.equals("y") || s.equals("z");
          }
        }));
    assertEquals(newArrayList("a", "c", "e"), list);
  }

  public void testRemoveIf_noRandomAccess() {
    List<String> list = Lists.newLinkedList(asList("a", "b", "c", "d", "e"));
    assertTrue(Iterables.removeIf(list,
        new Predicate<String>() {
          public boolean apply(String s) {
            return s.equals("b") || s.equals("d") || s.equals("f");
          }
        }));
    assertEquals(newArrayList("a", "c", "e"), list);
    assertFalse(Iterables.removeIf(list,
        new Predicate<String>() {
          public boolean apply(String s) {
            return s.equals("x") || s.equals("y") || s.equals("z");
          }
        }));
    assertEquals(newArrayList("a", "c", "e"), list);
  }

  // The Maps returned by Maps.filterEntries(), Maps.filterKeys(), and
  // Maps.filterValues() are not tested with removeIf() since Maps are not
  // Iterable.  Those returned by Iterators.filter() and Iterables.filter()
  // are not tested because they are unmodifiable.

  public void testIterableWithToString() {
    assertEquals("[]", create().toString());
    assertEquals("[a]", create("a").toString());
    assertEquals("[a, b, c]", create("a", "b", "c").toString());
    assertEquals("[c, a, a]", create("c", "a", "a").toString());
  }

  public void testIterableWithToStringNull() {
    assertEquals("[null]", create((String) null).toString());
    assertEquals("[null, null]", create(null, null).toString());
    assertEquals("[, null, a]", create("", null, "a").toString());
  }

  /** Returns a new iterable over the specified strings. */
  private static Iterable<String> create(String... strings) {
    final List<String> list = asList(strings);
    return new Iterables.IterableWithToString<String>() {
      public Iterator<String> iterator() {
        return list.iterator();
      }
    };
  }
}
