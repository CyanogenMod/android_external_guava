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

import com.google.common.collect.SetsTest.Derived;
import static com.google.common.collect.testing.Helpers.assertContentsAnyOrder;
import com.google.common.testing.junit3.JUnitAsserts;
import static com.google.common.testutils.SerializableTester.reserializeAndAssert;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests for {@link Multisets}.
 *
 * @author Mike Bostock
 * @author Jared Levy
 */
public class MultisetsTest extends TestCase {

  /* See MultisetsImmutableEntryTest for immutableEntry() tests. */

  public void testForSet() {
    Set<String> set = new HashSet<String>();
    set.add("foo");
    set.add("bar");
    set.add(null);
    Multiset<String> multiset = HashMultiset.create();
    multiset.addAll(set);
    Multiset<String> multisetView = Multisets.forSet(set);
    assertTrue(multiset.equals(multisetView));
    assertTrue(multisetView.equals(multiset));
    assertEquals(multiset.toString(), multisetView.toString());
    assertEquals(multiset.hashCode(), multisetView.hashCode());
    assertEquals(multiset.size(), multisetView.size());
    assertTrue(multisetView.contains("foo"));
    assertEquals(set, multisetView.elementSet());
    assertEquals(multisetView.elementSet(), set);
    assertEquals(multiset.elementSet(), multisetView.elementSet());
    assertEquals(multisetView.elementSet(), multiset.elementSet());
    reserializeAndAssert(multisetView);
    try {
      multisetView.add("baz");
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    try {
      multisetView.addAll(Collections.singleton("baz"));
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    try {
      multisetView.elementSet().add("baz");
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    try {
      multisetView.elementSet().addAll(Collections.singleton("baz"));
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException expected) {}
    multisetView.remove("bar");
    assertFalse(multisetView.contains("bar"));
    assertFalse(set.contains("bar"));
    assertEquals(set, multisetView.elementSet());
    assertContentsAnyOrder(multisetView.elementSet(), "foo", null);
    assertContentsAnyOrder(multisetView.entrySet(),
        Multisets.immutableEntry("foo", 1), Multisets.immutableEntry(null, 1));
    multisetView.clear();
    assertFalse(multisetView.contains("foo"));
    assertFalse(set.contains("foo"));
    assertTrue(set.isEmpty());
    assertTrue(multisetView.isEmpty());
    multiset.clear();
    assertEquals(multiset.toString(), multisetView.toString());
    assertEquals(multiset.hashCode(), multisetView.hashCode());
    assertEquals(multiset.size(), multisetView.size());
  }

  public void testNewTreeMultisetDerived() {
    TreeMultiset<Derived> set = TreeMultiset.create();
    assertTrue(set.isEmpty());
    set.add(new Derived("foo"), 2);
    set.add(new Derived("bar"), 3);
    JUnitAsserts.assertContentsInOrder(set,
        new Derived("bar"), new Derived("bar"), new Derived("bar"),
        new Derived("foo"), new Derived("foo"));
  }

  public void testNewTreeMultisetNonGeneric() {
    TreeMultiset<LegacyComparable> set = TreeMultiset.create();
    assertTrue(set.isEmpty());
    set.add(new LegacyComparable("foo"), 2);
    set.add(new LegacyComparable("bar"), 3);
    JUnitAsserts.assertContentsInOrder(set, new LegacyComparable("bar"),
        new LegacyComparable("bar"), new LegacyComparable("bar"),
        new LegacyComparable("foo"), new LegacyComparable("foo"));
  }

  public void testNewTreeMultisetComparator() {
    TreeMultiset<String> multiset
        = TreeMultiset.create(Collections.reverseOrder());
    multiset.add("bar", 3);
    multiset.add("foo", 2);
    JUnitAsserts.assertContentsInOrder(
        multiset, "foo", "foo", "bar", "bar", "bar");
  }
}
