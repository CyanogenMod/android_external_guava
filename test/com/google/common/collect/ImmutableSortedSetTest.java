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

import com.google.common.collect.ImmutableSet.Builder;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import static com.google.common.testing.junit3.JUnitAsserts.assertNotEqual;
import com.google.common.testutils.NullPointerTester;
import com.google.common.testutils.SerializableTester;

import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

/**
 * Unit tests for {@link ImmutableSortedSet}.
 *
 * @author Jared Levy
 */
public class ImmutableSortedSetTest extends AbstractImmutableSetTest {

  // enum singleton pattern
  private enum StringLengthComparator implements Comparator<String> {
    INSTANCE;

    public int compare(String a, String b) {
      return a.length() - b.length();
    }
  }

  private static final Comparator<String> STRING_LENGTH
      = StringLengthComparator.INSTANCE;

  @Override protected Set<String> of() {
    return ImmutableSortedSet.of();
  }

  @Override protected Set<String> of(String e) {
    return ImmutableSortedSet.of(e);
  }

  @Override protected Set<String> of(String e1, String e2) {
    return ImmutableSortedSet.of(e1, e2);
  }

  @Override protected Set<String> of(String e1, String e2, String e3) {
    return ImmutableSortedSet.of(e1, e2, e3);
  }

  @Override protected Set<String> of(
      String e1, String e2, String e3, String e4) {
    return ImmutableSortedSet.of(e1, e2, e3, e4);
  }

  @Override protected Set<String> of(
      String e1, String e2, String e3, String e4, String e5) {
    return ImmutableSortedSet.of(e1, e2, e3, e4, e5);
  }

  @Override protected Set<String> of(String... elements) {
    return ImmutableSortedSet.of(elements);
  }

  @Override protected Set<String> copyOf(Iterable<String> elements) {
    return ImmutableSortedSet.copyOf(elements);
  }

  @Override protected Set<String> copyOf(Iterator<String> elements) {
    return ImmutableSortedSet.copyOf(elements);
  }

  public void testNullPointers() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(ImmutableSortedSet.class);
  }

  public void testEmpty_comparator() {
    SortedSet<String> set = ImmutableSortedSet.of();
    assertSame(Ordering.natural(), set.comparator());
  }

  public void testEmpty_headSet() {
    SortedSet<String> set = ImmutableSortedSet.of();
    assertSame(set, set.headSet("c"));
  }

  public void testEmpty_tailSet() {
    SortedSet<String> set = ImmutableSortedSet.of();
    assertSame(set, set.tailSet("f"));
  }

  public void testEmpty_subSet() {
    SortedSet<String> set = ImmutableSortedSet.of();
    assertSame(set, set.subSet("c", "f"));
  }

  public void testEmpty_first() {
    SortedSet<String> set = ImmutableSortedSet.of();
    try {
      set.first();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testEmpty_last() {
    SortedSet<String> set = ImmutableSortedSet.of();
    try {
      set.last();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testEmpty_serialization() {
    SortedSet<String> set = ImmutableSortedSet.of();
    SortedSet<String> copy = SerializableTester.reserialize(set);
    assertSame(set, copy);
  }

  public void testSingle_comparator() {
    SortedSet<String> set = ImmutableSortedSet.of("e");
    assertSame(Ordering.natural(), set.comparator());
  }

  public void testSingle_headSet() {
    SortedSet<String> set = ImmutableSortedSet.of("e");
    assertTrue(set.headSet("g") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.headSet("g"), "e");
    assertSame(ImmutableSortedSet.of(), set.headSet("c"));
    assertSame(ImmutableSortedSet.of(), set.headSet("e"));
  }

  public void testSingle_tailSet() {
    SortedSet<String> set = ImmutableSortedSet.of("e");
    assertTrue(set.tailSet("c") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.tailSet("c"), "e");
    assertContentsInOrder(set.tailSet("e"), "e");
    assertSame(ImmutableSortedSet.of(), set.tailSet("g"));
  }

  public void testSingle_subSet() {
    SortedSet<String> set = ImmutableSortedSet.of("e");
    assertTrue(set.subSet("c", "g") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.subSet("c", "g"), "e");
    assertContentsInOrder(set.subSet("e", "g"), "e");
    assertSame(ImmutableSortedSet.of(), set.subSet("f", "g"));
    assertSame(ImmutableSortedSet.of(), set.subSet("c", "e"));
    assertSame(ImmutableSortedSet.of(), set.subSet("c", "d"));
  }

  public void testSingle_first() {
    SortedSet<String> set = ImmutableSortedSet.of("e");
    assertEquals("e", set.first());
  }

  public void testSingle_last() {
    SortedSet<String> set = ImmutableSortedSet.of("e");
    assertEquals("e", set.last());
  }

  public void testSingle_serialization() {
    SortedSet<String> set = ImmutableSortedSet.of("e");
    SortedSet<String> copy = SerializableTester.reserializeAndAssert(set);
    assertEquals(set.comparator(), copy.comparator());
  }

  public void testOf_ordering() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "a", "f", "b", "d", "c");
    assertContentsInOrder(set, "a", "b", "c", "d", "e", "f");
  }

  public void testOf_ordering_dupes() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "a", "e", "f", "b", "b", "d", "a", "c");
    assertContentsInOrder(set, "a", "b", "c", "d", "e", "f");
  }

  public void testOf_comparator() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "a", "f", "b", "d", "c");
    assertSame(Ordering.natural(), set.comparator());
  }

  public void testOf_headSet() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "f", "b", "d", "c");
    assertTrue(set.headSet("e") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.headSet("e"), "b", "c", "d");
    assertContentsInOrder(set.headSet("g"), "b", "c", "d", "e", "f");
    assertSame(ImmutableSortedSet.of(), set.headSet("a"));
    assertSame(ImmutableSortedSet.of(), set.headSet("b"));
  }

  public void testOf_tailSet() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "f", "b", "d", "c");
    assertTrue(set.tailSet("e") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.tailSet("e"), "e", "f");
    assertContentsInOrder(set.tailSet("a"), "b", "c", "d", "e", "f");
    assertSame(ImmutableSortedSet.of(), set.tailSet("g"));
  }

  public void testOf_subSet() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "f", "b", "d", "c");
    assertTrue(set.subSet("c", "e") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.subSet("c", "e"), "c", "d");
    assertContentsInOrder(set.subSet("a", "g"), "b", "c", "d", "e", "f");
    assertSame(ImmutableSortedSet.of(), set.subSet("a", "b"));
    assertSame(ImmutableSortedSet.of(), set.subSet("g", "h"));
    assertSame(ImmutableSortedSet.of(), set.subSet("c", "c"));
    try {
      set.subSet("e", "c");
      fail();
    } catch (IllegalArgumentException expected) {
    }
    SerializableTester.reserializeAndAssert(set.subSet("c", "e"));
  }

  public void testOf_first() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "f", "b", "d", "c");
    assertEquals("b", set.first());
  }

  public void testOf_last() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "f", "b", "d", "c");
    assertEquals("f", set.last());
  }

  public void testOf_serialization() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "f", "b", "d", "c");
    SortedSet<String> copy = SerializableTester.reserializeAndAssert(set);
    assertTrue(Iterables.elementsEqual(set, copy));
    assertEquals(set.comparator(), copy.comparator());
  }

  /* "Explicit" indicates an explicit comparator. */

  public void testExplicit_ordering() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    assertContentsInOrder(set, "a", "in", "the", "over", "quick", "jumped");
  }

  public void testExplicit_ordering_dupes() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "brown", "fox", "jumped",
        "over", "a", "lazy", "dog").build();
    assertContentsInOrder(set, "a", "in", "the", "over", "quick", "jumped");
  }

  public void testExplicit_contains() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    assertTrue(set.contains("quick"));
    assertTrue(set.contains("google"));
    assertFalse(set.contains(""));
    assertFalse(set.contains("california"));
    assertFalse(set.contains(null));
    assertFalse(set.contains(3.7));
  }

  public void testExplicit_comparator() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    assertSame(STRING_LENGTH, set.comparator());
  }

  public void testExplicit_headSet() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    assertTrue(set.headSet("a") instanceof ImmutableSortedSet<?>);
    assertTrue(set.headSet("fish") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.headSet("fish"), "a", "in", "the");
    assertContentsInOrder(
        set.headSet("california"), "a", "in", "the", "over", "quick", "jumped");
    assertTrue(set.headSet("a").isEmpty());
    assertTrue(set.headSet("").isEmpty());
  }

  public void testExplicit_tailSet() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    assertTrue(set.tailSet("california") instanceof ImmutableSortedSet<?>);
    assertTrue(set.tailSet("fish") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.tailSet("fish"), "over", "quick", "jumped");
    assertContentsInOrder(
        set.tailSet("a"), "a", "in", "the", "over", "quick", "jumped");
    assertTrue(set.tailSet("california").isEmpty());
  }

  public void testExplicit_subSet() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    assertTrue(set.subSet("the", "quick") instanceof ImmutableSortedSet<?>);
    assertTrue(set.subSet("", "b") instanceof ImmutableSortedSet<?>);
    assertContentsInOrder(set.subSet("the", "quick"), "the", "over");
    assertContentsInOrder(
        set.subSet("a", "california"), "a", "in", "the", "over", "quick",
        "jumped");
    assertTrue(set.subSet("", "b").isEmpty());
    assertTrue(set.subSet("vermont", "california").isEmpty());
    assertTrue(set.subSet("aaa", "zzz").isEmpty());
    try {
      set.subSet("quick", "the");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testExplicit_first() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    assertEquals("a", set.first());
  }

  public void testExplicit_last() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    assertEquals("jumped", set.last());
  }

  public void testExplicitEmpty_serialization() {
    @SuppressWarnings("unchecked")
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).build();
    SortedSet<String> copy = SerializableTester.reserializeAndAssert(set);
    assertTrue(set.isEmpty());
    assertTrue(copy.isEmpty());
    assertSame(set.comparator(), copy.comparator());
  }

  public void testExplicit_serialization() {
    SortedSet<String> set = ImmutableSortedSet.orderedBy(STRING_LENGTH).add(
        "in", "the", "quick", "jumped", "over", "a").build();
    SortedSet<String> copy = SerializableTester.reserializeAndAssert(set);
    assertTrue(Iterables.elementsEqual(set, copy));
    assertSame(set.comparator(), copy.comparator());
  }

  public void testCopyOf_ordering() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(asList(
            "e", "a", "f", "b", "d", "c"));
    assertContentsInOrder(set, "a", "b", "c", "d", "e", "f");
  }

  public void testCopyOf_ordering_dupes() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(asList(
            "e", "a", "e", "f", "b", "b", "d", "a", "c"));
    assertContentsInOrder(set, "a", "b", "c", "d", "e", "f");
  }

  public void testCopyOf_subSet() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "a", "f", "b", "d", "c");
    SortedSet<String> subset = set.subSet("c", "e");
    SortedSet<String> copy = ImmutableSortedSet.copyOf(subset);
    assertEquals(subset, copy);
    assertFalse(subset == copy);
  }

  public void testCopyOf_headSet() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "a", "f", "b", "d", "c");
    SortedSet<String> headset = set.headSet("d");
    SortedSet<String> copy = ImmutableSortedSet.copyOf(headset);
    assertEquals(headset, copy);
    assertFalse(headset == copy);
  }

  public void testCopyOf_tailSet() {
    SortedSet<String> set =
        ImmutableSortedSet.of("e", "a", "f", "b", "d", "c");
    SortedSet<String> tailset = set.tailSet("d");
    SortedSet<String> copy = ImmutableSortedSet.copyOf(tailset);
    assertEquals(tailset, copy);
    assertFalse(tailset == copy);
  }

  public void testCopyOf_comparator() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(asList(
            "e", "a", "f", "b", "d", "c"));
    assertSame(Ordering.natural(), set.comparator());
  }

  public void testCopyOf_iterator_ordering() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(asIterator(
            "e", "a", "f", "b", "d", "c"));
    assertContentsInOrder(set, "a", "b", "c", "d", "e", "f");
  }

  public void testCopyOf_iterator_ordering_dupes() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(asIterator(
            "e", "a", "e", "f", "b", "b", "d", "a", "c"));
    assertContentsInOrder(set, "a", "b", "c", "d", "e", "f");
  }

  public void testCopyOf_iterator_comparator() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(asIterator(
            "e", "a", "f", "b", "d", "c"));
    assertSame(Ordering.natural(), set.comparator());
  }

  public void testCopyOf_sortedSet_ordering() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(Sets.newTreeSet(asList(
            "e", "a", "f", "b", "d", "c")));
    assertContentsInOrder(set, "a", "b", "c", "d", "e", "f");
  }

  public void testCopyOf_sortedSet_comparator() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(Sets.<String>newTreeSet());
    assertSame(Ordering.natural(), set.comparator());
  }

  public void testCopyOfExplicit_ordering() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(STRING_LENGTH, asList(
            "in", "the", "quick", "jumped", "over", "a"));
    assertContentsInOrder(set, "a", "in", "the", "over", "quick", "jumped");
  }

  public void testCopyOfExplicit_ordering_dupes() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(STRING_LENGTH, asList(
            "in", "the", "quick", "brown", "fox", "jumped", "over", "a",
            "lazy", "dog"));
    assertContentsInOrder(set, "a", "in", "the", "over", "quick", "jumped");
  }

  public void testCopyOfExplicit_comparator() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(STRING_LENGTH, asList(
            "in", "the", "quick", "jumped", "over", "a"));
    assertSame(STRING_LENGTH, set.comparator());
  }

  public void testCopyOfExplicit_iterator_ordering() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(STRING_LENGTH, asIterator(
            "in", "the", "quick", "jumped", "over", "a"));
    assertContentsInOrder(set, "a", "in", "the", "over", "quick", "jumped");
  }

  public void testCopyOfExplicit_iterator_ordering_dupes() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(STRING_LENGTH, asIterator(
            "in", "the", "quick", "brown", "fox", "jumped", "over", "a",
            "lazy", "dog"));
    assertContentsInOrder(set, "a", "in", "the", "over", "quick", "jumped");
  }

  public void testCopyOfExplicit_iterator_comparator() {
    SortedSet<String> set =
        ImmutableSortedSet.copyOf(STRING_LENGTH, asIterator(
            "in", "the", "quick", "jumped", "over", "a"));
    assertSame(STRING_LENGTH, set.comparator());
  }

  public void testCopyOf_sortedSetIterable() {
    SortedSet<String> input = Sets.newTreeSet(STRING_LENGTH);
    Collections.addAll(input, "in", "the", "quick", "jumped", "over", "a");
    SortedSet<String> set = ImmutableSortedSet.copyOf(input);
    assertContentsInOrder(set, "a", "in", "jumped", "over", "quick", "the");
  }

  public void testCopyOfSorted_natural_ordering() {
    SortedSet<String> input = Sets.newTreeSet(
        asList("in", "the", "quick", "jumped", "over", "a"));
    SortedSet<String> set = ImmutableSortedSet.copyOfSorted(input);
    assertContentsInOrder(set, "a", "in", "jumped", "over", "quick", "the");
  }

  public void testCopyOfSorted_natural_comparator() {
    SortedSet<String> input =
        Sets.newTreeSet(asList("in", "the", "quick", "jumped", "over", "a"));
    SortedSet<String> set = ImmutableSortedSet.copyOfSorted(input);
    assertSame(Ordering.natural(), set.comparator());
  }

  public void testCopyOfSorted_explicit_ordering() {
    SortedSet<String> input = Sets.newTreeSet(STRING_LENGTH);
    Collections.addAll(input, "in", "the", "quick", "jumped", "over", "a");
    SortedSet<String> set = ImmutableSortedSet.copyOfSorted(input);
    assertContentsInOrder(set, "a", "in", "the", "over", "quick", "jumped");
    assertSame(STRING_LENGTH, set.comparator());
  }

  public void testEquals_bothDefaultOrdering() {
    SortedSet<String> set = ImmutableSortedSet.of("a", "b", "c");
    assertEquals(set, Sets.newTreeSet(asList("a", "b", "c")));
    assertEquals(Sets.newTreeSet(asList("a", "b", "c")), set);
    assertNotEqual(set, Sets.newTreeSet(asList("a", "b", "d")));
    assertNotEqual(Sets.newTreeSet(asList("a", "b", "d")), set);
    assertNotEqual(set, Sets.newTreeSet(asList(4, 5, 6)));
    assertNotEqual(Sets.newTreeSet(asList(4, 5, 6)), set);
  }

  public void testEquals_bothExplicitOrdering() {
    SortedSet<String> set = ImmutableSortedSet.of("in", "the", "a");
    assertEquals(Sets.newTreeSet(asList("in", "the", "a")), set);
    assertNotEqual(set, Sets.newTreeSet(asList("in", "the", "house")));
    assertNotEqual(Sets.newTreeSet(asList("in", "the", "house")), set);
    assertNotEqual(set, Sets.newTreeSet(asList(4, 5, 6)));
    assertNotEqual(Sets.newTreeSet(asList(4, 5, 6)), set);

    Set<String> complex = Sets.newTreeSet(STRING_LENGTH);
    Collections.addAll(complex, "in", "the", "a");
    assertEquals(set, complex);
  }

  public void testContainsAll_notSortedSet() {
    SortedSet<String> set = ImmutableSortedSet.of("a", "b", "f");
    assertTrue(set.containsAll(Collections.emptyList()));
    assertTrue(set.containsAll(asList("b")));
    assertTrue(set.containsAll(asList("b", "b")));
    assertTrue(set.containsAll(asList("b", "f")));
    assertTrue(set.containsAll(asList("b", "f", "a")));
    assertFalse(set.containsAll(asList("d")));
    assertFalse(set.containsAll(asList("z")));
    assertFalse(set.containsAll(asList("b", "d")));
    assertFalse(set.containsAll(asList("f", "d", "a")));
  }

  public void testContainsAll_sameComparator() {
    SortedSet<String> set = ImmutableSortedSet.of("a", "b", "f");
    assertTrue(set.containsAll(Sets.newTreeSet()));
    assertTrue(set.containsAll(Sets.newTreeSet(asList("b"))));
    assertTrue(set.containsAll(Sets.newTreeSet(asList("a", "f"))));
    assertTrue(set.containsAll(Sets.newTreeSet(asList("a", "b", "f"))));
    assertFalse(set.containsAll(Sets.newTreeSet(asList("d"))));
    assertFalse(set.containsAll(Sets.newTreeSet(asList("z"))));
    assertFalse(set.containsAll(Sets.newTreeSet(asList("b", "d"))));
    assertFalse(set.containsAll(Sets.newTreeSet(asList("f", "d", "a"))));
  }

  public void testContainsAll_differentComparator() {
    Comparator<Comparable<?>> comparator = Collections.reverseOrder();
    SortedSet<String> set = new ImmutableSortedSet.Builder<String>(comparator)
        .add("a", "b", "f").build();
    assertTrue(set.containsAll(Sets.newTreeSet()));
    assertTrue(set.containsAll(Sets.newTreeSet(asList("b"))));
    assertTrue(set.containsAll(Sets.newTreeSet(asList("a", "f"))));
    assertTrue(set.containsAll(Sets.newTreeSet(asList("a", "b", "f"))));
    assertFalse(set.containsAll(Sets.newTreeSet(asList("d"))));
    assertFalse(set.containsAll(Sets.newTreeSet(asList("z"))));
    assertFalse(set.containsAll(Sets.newTreeSet(asList("b", "d"))));
    assertFalse(set.containsAll(Sets.newTreeSet(asList("f", "d", "a"))));
  }

  public void testDifferentComparator_serialization() {
    Comparator<Comparable<?>> comparator = Collections.reverseOrder();
    SortedSet<String> set = new ImmutableSortedSet.Builder<String>(comparator)
        .add("a", "b", "c").build();
    SortedSet<String> copy = SerializableTester.reserializeAndAssert(set);
    assertTrue(Iterables.elementsEqual(set, copy));
    assertEquals(set.comparator(), copy.comparator());
  }

  public void testReverseOrder() {
    SortedSet<String> set = ImmutableSortedSet.<String>reverseOrder()
        .add("a", "b", "c").build();
    assertContentsInOrder(set, "c", "b", "a");
    assertEquals(Ordering.natural().reverse(), set.comparator());
  }

  private static final Comparator<Object> TO_STRING
      = new Comparator<Object>() {
          public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
          }
        };

  public void testSupertypeComparator() {
    SortedSet<Integer> set = new ImmutableSortedSet.Builder<Integer>(TO_STRING)
        .add(3, 12, 101, 44).build();
    assertContentsInOrder(set, 101, 12, 3, 44);
  }

  public void testSupertypeComparatorSubtypeElements() {
    SortedSet<Number> set = new ImmutableSortedSet.Builder<Number>(TO_STRING)
        .add(3, 12, 101, 44).build();
    assertContentsInOrder(set, 101, 12, 3, 44);
  }

  @Override <E extends Comparable<E>> Builder<E> builder() {
    return ImmutableSortedSet.naturalOrder();
  }

  @Override int getComplexBuilderSetLastElement() {
    return 0x00FFFFFF;
  }

  public void testLegacyComparable_of() {
    ImmutableSortedSet<LegacyComparable> set0 = ImmutableSortedSet.of();

    @SuppressWarnings("unchecked") // using a legacy comparable
    ImmutableSortedSet<LegacyComparable> set1 = ImmutableSortedSet.of(
        LegacyComparable.Z);

    @SuppressWarnings("unchecked") // using a legacy comparable
    ImmutableSortedSet<LegacyComparable> set2 = ImmutableSortedSet.of(
        LegacyComparable.Z, LegacyComparable.Y);
  }

  public void testLegacyComparable_copyOf_collection() {
    @SuppressWarnings("unchecked") // using a legacy comparable
    ImmutableSortedSet<LegacyComparable> set
        = ImmutableSortedSet.copyOf(LegacyComparable.VALUES_BACKWARD);
    assertTrue(Iterables.elementsEqual(LegacyComparable.VALUES_FORWARD, set));
  }

  public void testLegacyComparable_copyOf_iterator() {
    @SuppressWarnings("unchecked") // using a legacy comparable
    ImmutableSortedSet<LegacyComparable> set = ImmutableSortedSet.copyOf(
        LegacyComparable.VALUES_BACKWARD.iterator());
    assertTrue(Iterables.elementsEqual(LegacyComparable.VALUES_FORWARD, set));
  }

  public void testLegacyComparable_builder_natural() {
    @SuppressWarnings("unchecked")
    // Note: IntelliJ wrongly reports an error for this statement
    ImmutableSortedSet.Builder<LegacyComparable> builder
        = ImmutableSortedSet.<LegacyComparable>naturalOrder();

    builder.addAll(LegacyComparable.VALUES_BACKWARD);
    builder.add(LegacyComparable.X);
    builder.add(LegacyComparable.Y, LegacyComparable.Z);

    ImmutableSortedSet<LegacyComparable> set = builder.build();
    assertTrue(Iterables.elementsEqual(LegacyComparable.VALUES_FORWARD, set));
  }

  public void testLegacyComparable_builder_reverse() {
    @SuppressWarnings("unchecked")
    // Note: IntelliJ wrongly reports an error for this statement
    ImmutableSortedSet.Builder<LegacyComparable> builder
        = ImmutableSortedSet.<LegacyComparable>reverseOrder();

    builder.addAll(LegacyComparable.VALUES_FORWARD);
    builder.add(LegacyComparable.X);
    builder.add(LegacyComparable.Y, LegacyComparable.Z);

    ImmutableSortedSet<LegacyComparable> set = builder.build();
    assertTrue(Iterables.elementsEqual(LegacyComparable.VALUES_BACKWARD, set));
  }

  @SuppressWarnings("deprecation")
  public void testBuilderMethod() {
    try {
      ImmutableSortedSet.Builder<String> builder = ImmutableSortedSet.builder();
      fail();
    } catch (UnsupportedOperationException expected) {
    }
  }

  private static final <E> Iterator<E> asIterator(E... elements) {
    return asList(elements).iterator();
  }
}
