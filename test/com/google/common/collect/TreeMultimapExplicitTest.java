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

import static com.google.common.collect.testing.Helpers.assertContentsAnyOrder;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import com.google.common.testutils.SerializableTester;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

/**
 * Unit tests for {@code TreeMultimap} with explicit comparators.
 *
 * @author Jared Levy
 */
public class TreeMultimapExplicitTest extends AbstractSetMultimapTest {

  /**
   * Compare strings lengths, and if the lengths are equal compare the strings.
   * A {@code null} is less than any non-null value.
   */
  private enum StringLength implements Comparator<String> {
    COMPARATOR;

    public int compare(String first, String second) {
      if (first == second) {
        return 0;
      } else if (first == null) {
        return -1;
      } else if (second == null) {
        return 1;
      } else if (first.length() != second.length()) {
        return first.length() - second.length();
      } else {
        return first.compareTo(second);
      }
    }
  }

  /**
   * Decreasing integer values. A {@code null} comes before any non-null value.
   */
  private static final Comparator<Integer> DECREASING_INT_COMPARATOR =
      Ordering.<Integer>natural().reverse().nullsFirst();

  @Override protected Multimap<String, Integer> create() {
    return TreeMultimap.create(
        StringLength.COMPARATOR, DECREASING_INT_COMPARATOR);
  }

  /**
   * Create and populate a {@code TreeMultimap} with explicit comparators.
   */
  private TreeMultimap<String, Integer> createPopulate() {
    TreeMultimap<String, Integer> multimap = TreeMultimap.create(
        StringLength.COMPARATOR, DECREASING_INT_COMPARATOR);
    multimap.put("google", 2);
    multimap.put("google", 6);
    multimap.put(null, 3);
    multimap.put(null, 1);
    multimap.put(null, 7);
    multimap.put("tree", 0);
    multimap.put("tree", null);
    return multimap;
  }

  /**
   * Test that a TreeMultimap created from another uses the natural ordering.
   */
  public void testMultimapCreateFromTreeMultimap() {
    TreeMultimap<String, Integer> tree = TreeMultimap.create(
        StringLength.COMPARATOR, DECREASING_INT_COMPARATOR);
    tree.put("google", 2);
    tree.put("google", 6);
    tree.put("tree", 0);
    tree.put("tree", 3);
    assertContentsInOrder(tree.keySet(), "tree", "google");
    assertContentsInOrder(tree.get("google"), 6, 2);

    TreeMultimap<String, Integer> copy = TreeMultimap.create(tree);
    assertEquals(tree, copy);
    assertContentsInOrder(copy.keySet(), "google", "tree");
    assertContentsInOrder(copy.get("google"), 2, 6);
    assertEquals(Ordering.natural(), copy.keyComparator());
    assertEquals(Ordering.natural(), copy.valueComparator());
    assertEquals(Ordering.natural(), copy.get("google").comparator());
  }

  public void testToString() {
    assertEquals("{bar=[3, 2, 1], foo=[4, 3, 2, 1, -1]}",
        createSample().toString());
  }

  public void testGetComparator() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertEquals(StringLength.COMPARATOR, multimap.keyComparator());
    assertEquals(DECREASING_INT_COMPARATOR, multimap.valueComparator());
  }

  public void testOrderedGet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertContentsInOrder(multimap.get(null), 7, 3, 1);
    assertContentsInOrder(multimap.get("google"), 6, 2);
    assertContentsInOrder(multimap.get("tree"), null, 0);
  }

  public void testOrderedKeySet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertContentsInOrder(
        multimap.keySet(), null, "tree", "google");
  }

  public void testOrderedAsMapEntries() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    Iterator<Map.Entry<String, Collection<Integer>>> iterator =
        multimap.asMap().entrySet().iterator();
    Map.Entry<String, Collection<Integer>> entry = iterator.next();
    assertEquals(null, entry.getKey());
    assertContentsAnyOrder(entry.getValue(), 7, 3, 1);
    entry = iterator.next();
    assertEquals("tree", entry.getKey());
    assertContentsAnyOrder(entry.getValue(), null, 0);
    entry = iterator.next();
    assertEquals("google", entry.getKey());
    assertContentsAnyOrder(entry.getValue(), 6, 2);
  }

  public void testOrderedEntries() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertContentsInOrder(multimap.entries(),
        Maps.immutableEntry(null, 7),
        Maps.immutableEntry(null, 3),
        Maps.immutableEntry(null, 1),
        Maps.immutableEntry("tree", null),
        Maps.immutableEntry("tree", 0),
        Maps.immutableEntry("google", 6),
        Maps.immutableEntry("google", 2));
  }

  public void testOrderedValues() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertContentsInOrder(multimap.values(),
        7, 3, 1, null, 0, 6, 2);
  }

  public void testComparator() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    assertEquals(DECREASING_INT_COMPARATOR, multimap.get("foo").comparator());
    assertEquals(DECREASING_INT_COMPARATOR,
        multimap.get("missing").comparator());
  }

  public void testSortedKeySet() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    SortedSet<String> keySet = multimap.keySet();

    assertEquals(null, keySet.first());
    assertEquals("google", keySet.last());
    assertEquals(StringLength.COMPARATOR, keySet.comparator());
    assertEquals(Sets.newHashSet(null, "tree"), keySet.headSet("yahoo"));
    assertEquals(Sets.newHashSet("google"), keySet.tailSet("yahoo"));
    assertEquals(Sets.newHashSet("tree"), keySet.subSet("ask", "yahoo"));
  }

  public void testExplicitComparatorSerialization() {
    TreeMultimap<String, Integer> multimap = createPopulate();
    TreeMultimap<String, Integer> copy
        = SerializableTester.reserializeAndAssert(multimap);
    assertContentsInOrder(
        copy.values(), 7, 3, 1, null, 0, 6, 2);
    assertContentsInOrder(
        copy.keySet(), null, "tree", "google");
    assertEquals(multimap.keyComparator(), copy.keyComparator());
    assertEquals(multimap.valueComparator(), copy.valueComparator());
  }
}
