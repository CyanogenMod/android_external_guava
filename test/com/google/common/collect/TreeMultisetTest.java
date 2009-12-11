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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.testing.IteratorFeature.MODIFIABLE;
import com.google.common.collect.testing.IteratorTester;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Unit test for {@link TreeMultiset}.
 *
 * @author Neal Kanodia
 */
public class TreeMultisetTest extends AbstractMultisetTest {
  @SuppressWarnings("unchecked")
  @Override protected <E> Multiset<E> create() {
    return (Multiset) TreeMultiset.create();
  }

  public void testCreate() {
    Multiset<String> multiset = TreeMultiset.create();
    multiset.add("foo", 2);
    multiset.add("bar");
    assertEquals(3, multiset.size());
    assertEquals(2, multiset.count("foo"));
    assertEquals("[bar, foo x 2]", multiset.toString());
  }

  public void testCreateWithComparator() {
    Multiset<String> multiset = TreeMultiset.create(Collections.reverseOrder());
    multiset.add("foo", 2);
    multiset.add("bar");
    assertEquals(3, multiset.size());
    assertEquals(2, multiset.count("foo"));
    assertEquals("[foo x 2, bar]", multiset.toString());
  }

  public void testCreateFromIterable() {
    Multiset<String> multiset
        = TreeMultiset.create(Arrays.asList("foo", "bar", "foo"));
    assertEquals(3, multiset.size());
    assertEquals(2, multiset.count("foo"));
    assertEquals("[bar, foo x 2]", multiset.toString());
  }

  public void testToString() {
    ms.add("a", 3);
    ms.add("c", 1);
    ms.add("b", 2);

    assertEquals("[a x 3, b x 2, c]", ms.toString());
  }

  public void testIteratorBashing() throws Exception {
    IteratorTester<String> tester =
        new IteratorTester<String>(createSample().size() + 2, MODIFIABLE,
            newArrayList(createSample()),
            IteratorTester.KnownOrder.KNOWN_ORDER) {
          private Multiset<String> targetMultiset;

          @Override protected Iterator<String> newTargetIterator() {
            targetMultiset = createSample();
            return targetMultiset.iterator();
          }

          @Override protected void verify(List<String> elements) {
            assertEquals(elements, Lists.newArrayList(targetMultiset));
          }
        };

    /* This next line added as a stopgap until JDK6 bug is fixed. */
    tester.ignoreSunJavaBug6529795();

    tester.test();
  }

  public void testElementSetIteratorBashing() throws Exception {
    IteratorTester<String> tester = new IteratorTester<String>(5, MODIFIABLE,
        newArrayList("a", "b", "c"), IteratorTester.KnownOrder.KNOWN_ORDER) {
      private Set<String> targetSet;
      @Override protected Iterator<String> newTargetIterator() {
        Multiset<String> multiset = create();
        multiset.add("a", 3);
        multiset.add("c", 1);
        multiset.add("b", 2);
        targetSet = multiset.elementSet();
        return targetSet.iterator();
      }
      @Override protected void verify(List<String> elements) {
        assertEquals(elements, Lists.newArrayList(targetSet));
      }
    };

    /* This next line added as a stopgap until JDK6 bug is fixed. */
    tester.ignoreSunJavaBug6529795();

    tester.test();
  }

  public void testElementSetSortedSetMethods() {
    @SuppressWarnings("hiding")
    TreeMultiset<String> ms = TreeMultiset.create();
    ms.add("c", 1);
    ms.add("a", 3);
    ms.add("b", 2);
    SortedSet<String> elementSet = ms.elementSet();

    assertEquals("a", elementSet.first());
    assertEquals("c", elementSet.last());
    assertNull(elementSet.comparator());
    assertContentsInOrder(elementSet.headSet("b"), "a");
    assertContentsInOrder(elementSet.tailSet("b"), "b", "c");
    assertContentsInOrder(elementSet.subSet("a", "c"), "a", "b");
  }

  public void testElementSetSubsetRemove() {
    @SuppressWarnings("hiding")
    TreeMultiset<String> ms = TreeMultiset.create();
    ms.add("a", 1);
    ms.add("b", 3);
    ms.add("c", 2);
    ms.add("d", 1);
    ms.add("e", 3);
    ms.add("f", 2);

    SortedSet<String> elementSet = ms.elementSet();
    assertContentsInOrder(elementSet, "a", "b", "c", "d", "e", "f");
    SortedSet<String> subset = elementSet.subSet("b", "f");
    assertContentsInOrder(subset, "b", "c", "d", "e");

    assertTrue(subset.remove("c"));
    assertContentsInOrder(elementSet, "a", "b", "d", "e", "f");
    assertContentsInOrder(subset, "b", "d", "e");
    assertEquals(10, ms.size());

    assertFalse(subset.remove("a"));
    assertContentsInOrder(elementSet, "a", "b", "d", "e", "f");
    assertContentsInOrder(subset, "b", "d", "e");
    assertEquals(10, ms.size());
  }

  public void testElementSetSubsetRemoveAll() {
    @SuppressWarnings("hiding")
    TreeMultiset<String> ms = TreeMultiset.create();
    ms.add("a", 1);
    ms.add("b", 3);
    ms.add("c", 2);
    ms.add("d", 1);
    ms.add("e", 3);
    ms.add("f", 2);

    SortedSet<String> elementSet = ms.elementSet();
    assertContentsInOrder(elementSet, "a", "b", "c", "d", "e", "f");
    SortedSet<String> subset = elementSet.subSet("b", "f");
    assertContentsInOrder(subset, "b", "c", "d", "e");

    assertTrue(subset.removeAll(Arrays.asList("a", "c")));
    assertContentsInOrder(elementSet, "a", "b", "d", "e", "f");
    assertContentsInOrder(subset, "b", "d", "e");
    assertEquals(10, ms.size());
  }

  public void testElementSetSubsetRetainAll() {
    @SuppressWarnings("hiding")
    TreeMultiset<String> ms = TreeMultiset.create();
    ms.add("a", 1);
    ms.add("b", 3);
    ms.add("c", 2);
    ms.add("d", 1);
    ms.add("e", 3);
    ms.add("f", 2);

    SortedSet<String> elementSet = ms.elementSet();
    assertContentsInOrder(elementSet, "a", "b", "c", "d", "e", "f");
    SortedSet<String> subset = elementSet.subSet("b", "f");
    assertContentsInOrder(subset, "b", "c", "d", "e");

    assertTrue(subset.retainAll(Arrays.asList("a", "c")));
    assertContentsInOrder(elementSet, "a", "c", "f");
    assertContentsInOrder(subset, "c");
    assertEquals(5, ms.size());
  }

  public void testElementSetSubsetClear() {
    @SuppressWarnings("hiding")
    TreeMultiset<String> ms = TreeMultiset.create();
    ms.add("a", 1);
    ms.add("b", 3);
    ms.add("c", 2);
    ms.add("d", 1);
    ms.add("e", 3);
    ms.add("f", 2);

    SortedSet<String> elementSet = ms.elementSet();
    assertContentsInOrder(elementSet, "a", "b", "c", "d", "e", "f");
    SortedSet<String> subset = elementSet.subSet("b", "f");
    assertContentsInOrder(subset, "b", "c", "d", "e");

    subset.clear();
    assertContentsInOrder(elementSet, "a", "f");
    assertContentsInOrder(subset);
    assertEquals(3, ms.size());
  }

  public void testCustomComparator() throws Exception {
    Comparator<String> comparator = new Comparator<String>() {
      public int compare(String o1, String o2) {
        return o2.compareTo(o1);
      }
    };
    @SuppressWarnings("hiding")
    TreeMultiset<String> ms = TreeMultiset.create(comparator);

    ms.add("b");
    ms.add("c");
    ms.add("a");
    ms.add("b");
    ms.add("d");

    assertContentsInOrder(ms, "d", "c", "b", "b", "a");

    SortedSet<String> elementSet = ms.elementSet();
    assertEquals("d", elementSet.first());
    assertEquals("a", elementSet.last());
    assertEquals(comparator, elementSet.comparator());
  }

  public void testNullAcceptingComparator() throws Exception {
    Comparator<String> comparator = Ordering.<String>natural().nullsFirst();
    @SuppressWarnings("hiding")
    TreeMultiset<String> ms = TreeMultiset.create(comparator);

    ms.add("b");
    ms.add(null);
    ms.add("a");
    ms.add("b");
    ms.add(null, 2);

    assertContentsInOrder(ms, null, null, null, "a", "b", "b");
    assertEquals(3, ms.count(null));

    SortedSet<String> elementSet = ms.elementSet();
    assertEquals(null, elementSet.first());
    assertEquals("b", elementSet.last());
    assertEquals(comparator, elementSet.comparator());
  }

  private static final Comparator<String> DEGENERATE_COMPARATOR =
      new Comparator<String>() {
        public int compare(String o1, String o2) {
          return o1.length() - o2.length();
        }
      };

  /**
   * Test a TreeMultiset with a comparator that can return 0 when comparing
   * unequal values.
   */
  public void testDegenerateComparator() throws Exception {
    @SuppressWarnings("hiding")
    TreeMultiset<String> ms = TreeMultiset.create(DEGENERATE_COMPARATOR);

    ms.add("foo");
    ms.add("a");
    ms.add("bar");
    ms.add("b");
    ms.add("c");

    assertEquals(2, ms.count("bar"));
    assertEquals(3, ms.count("b"));

    Multiset<String> ms2 = TreeMultiset.create(DEGENERATE_COMPARATOR);

    ms2.add("cat", 2);
    ms2.add("x", 3);

    assertEquals(ms, ms2);
    assertEquals(ms2, ms);

    SortedSet<String> elementSet = ms.elementSet();
    assertEquals("a", elementSet.first());
    assertEquals("foo", elementSet.last());
    assertEquals(DEGENERATE_COMPARATOR, elementSet.comparator());
  }

  @Override public void testToStringNull() {
    try {
      super.testToStringNull();
      fail("exception expected");
    } catch (NullPointerException expected) {}
  }
}
