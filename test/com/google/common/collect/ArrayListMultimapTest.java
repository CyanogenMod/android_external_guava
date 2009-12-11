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

import com.google.common.testing.junit3.JUnitAsserts;

import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.RandomAccess;

/**
 * Unit tests for {@code ArrayListMultimap}.
 *
 * @author Jared Levy
 */
public class ArrayListMultimapTest extends AbstractListMultimapTest {

  @Override protected ListMultimap<String, Integer> create() {
    return ArrayListMultimap.create();
  }

  /**
   * Confirm that get() returns a List implementing RandomAccess.
   */
  public void testGetRandomAccess() {
    Multimap<String, Integer> multimap = create();
    multimap.put("foo", 1);
    multimap.put("foo", 3);
    assertTrue(multimap.get("foo") instanceof RandomAccess);
    assertTrue(multimap.get("bar") instanceof RandomAccess);
  }

  /**
   * Confirm that removeAll() returns a List implementing RandomAccess.
   */
  public void testRemoveAllRandomAccess() {
    Multimap<String, Integer> multimap = create();
    multimap.put("foo", 1);
    multimap.put("foo", 3);
    assertTrue(multimap.removeAll("foo") instanceof RandomAccess);
    assertTrue(multimap.removeAll("bar") instanceof RandomAccess);
  }

  /**
   * Confirm that replaceValues() returns a List implementing RandomAccess.
   */
  public void testReplaceValuesRandomAccess() {
    Multimap<String, Integer> multimap = create();
    multimap.put("foo", 1);
    multimap.put("foo", 3);
    assertTrue(multimap.replaceValues("foo", Arrays.asList(2, 4))
        instanceof RandomAccess);
    assertTrue(multimap.replaceValues("bar", Arrays.asList(2, 4))
        instanceof RandomAccess);
  }

  /**
   * Test throwing ConcurrentModificationException when a sublist's ancestor's
   * delegate changes.
   */
  public void testSublistConcurrentModificationException() {
    ListMultimap<String, Integer> multimap = create();
    multimap.putAll("foo", Arrays.asList(1, 2, 3, 4, 5));
    List<Integer> list = multimap.get("foo");
    JUnitAsserts.assertContentsInOrder(multimap.get("foo"), 1, 2, 3, 4, 5);
    List<Integer> sublist = list.subList(0, 5);
    JUnitAsserts.assertContentsInOrder(sublist, 1, 2, 3, 4, 5);

    sublist.retainAll(Collections.EMPTY_LIST);
    assertTrue(sublist.isEmpty());
    multimap.put("foo", 6);

    try {
      sublist.isEmpty();
      fail("Expected ConcurrentModificationException");
    } catch (ConcurrentModificationException expected) {}
  }

  public void testCreateFromMultimap() {
    Multimap<String, Integer> multimap = createSample();
    ArrayListMultimap<String, Integer> copy
        = ArrayListMultimap.create(multimap);
    assertEquals(multimap, copy);
  }

  public void testCreate() {
    ArrayListMultimap<String, Integer> multimap
        = ArrayListMultimap.create();
    assertEquals(10, multimap.expectedValuesPerKey);
  }

  public void testCreateFromSizes() {
    ArrayListMultimap<String, Integer> multimap
        = ArrayListMultimap.create(15, 20);
    assertEquals(20, multimap.expectedValuesPerKey);
  }

  public void testCreateFromIllegalSizes() {
    try {
      ArrayListMultimap.create(15, -2);
      fail();
    } catch (IllegalArgumentException expected) {}

    try {
      ArrayListMultimap.create(-15, 2);
      fail();
    } catch (IllegalArgumentException expected) {}
  }

  public void testCreateFromHashMultimap() {
    Multimap<String, Integer> original = HashMultimap.create();
    ArrayListMultimap<String, Integer> multimap
        = ArrayListMultimap.create(original);
    assertEquals(10, multimap.expectedValuesPerKey);
  }

  public void testCreateFromArrayListMultimap() {
    ArrayListMultimap<String, Integer> original
        = ArrayListMultimap.create(15, 20);
    ArrayListMultimap<String, Integer> multimap
        = ArrayListMultimap.create(original);
    assertEquals(20, multimap.expectedValuesPerKey);
  }

  public void testTrimToSize() {
    ArrayListMultimap<String, Integer> multimap
        = ArrayListMultimap.create();
    multimap.put("foo", 1);
    multimap.put("foo", 2);
    multimap.put("bar", 3);
    multimap.trimToSize();
    assertEquals(3, multimap.size());
    assertEquals(Arrays.asList(1, 2), multimap.get("foo"));
    assertEquals(Arrays.asList(3), multimap.get("bar"));
  }
}
