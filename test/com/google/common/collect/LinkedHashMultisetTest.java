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
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Unit test for {@link LinkedHashMultiset}.
 *
 * @author Kevin Bourrillion
 */
public class LinkedHashMultisetTest extends AbstractMultisetTest {
  @Override protected <E> Multiset<E> create() {
    return LinkedHashMultiset.create();
  }

  public void testCreate() {
    Multiset<String> multiset = LinkedHashMultiset.create();
    multiset.add("foo", 2);
    multiset.add("bar");
    assertEquals(3, multiset.size());
    assertEquals(2, multiset.count("foo"));
    assertEquals("[foo x 2, bar]", multiset.toString());
  }

  public void testCreateWithSize() {
    Multiset<String> multiset = LinkedHashMultiset.create(50);
    multiset.add("foo", 2);
    multiset.add("bar");
    assertEquals(3, multiset.size());
    assertEquals(2, multiset.count("foo"));
    assertEquals("[foo x 2, bar]", multiset.toString());
  }

  public void testCreateFromIterable() {
    Multiset<String> multiset
        = LinkedHashMultiset.create(Arrays.asList("foo", "bar", "foo"));
    assertEquals(3, multiset.size());
    assertEquals(2, multiset.count("foo"));
    assertEquals("[foo x 2, bar]", multiset.toString());
  }

  public void testIteratorBashing() throws Exception {
    ms = createSample();
    IteratorTester<String> tester =
        new IteratorTester<String>(6, MODIFIABLE, newArrayList(ms),
            IteratorTester.KnownOrder.KNOWN_ORDER) {
          @Override protected Iterator<String> newTargetIterator() {
            return createSample().iterator();
          }
        };
    tester.test();
  }

  public void testElementSetIteratorBashing() throws Exception {
    IteratorTester<String> tester =
        new IteratorTester<String>(5, MODIFIABLE, newArrayList("a", "c", "b"),
            IteratorTester.KnownOrder.KNOWN_ORDER) {
          @Override protected Iterator<String> newTargetIterator() {
            Multiset<String> multiset = create();
            multiset.add("a", 3);
            multiset.add("c", 1);
            multiset.add("b", 2);
            return multiset.elementSet().iterator();
          }
        };
    tester.test();
  }

  public void testToString() {
    ms.add("a", 3);
    ms.add("c", 1);
    ms.add("b", 2);

    assertEquals("[a x 3, c, b x 2]", ms.toString());
  }

  public void testLosesPlaceInLine() throws Exception {
    ms.add("a");
    ms.add("b", 2);
    ms.add("c");
    assertContentsInOrder(ms.elementSet(), "a", "b", "c");
    ms.remove("b");
    assertContentsInOrder(ms.elementSet(), "a", "b", "c");
    ms.add("b");
    assertContentsInOrder(ms.elementSet(), "a", "b", "c");
    ms.remove("b", 2);
    ms.add("b");
    assertContentsInOrder(ms.elementSet(), "a", "c", "b");
  }

  public void testIteratorRemoveConcurrentModification() {
    ms.add("a");
    ms.add("b");
    Iterator<String> iterator = ms.iterator();
    iterator.next();
    ms.remove("a");
    assertEquals(1, ms.size());
    assertTrue(ms.contains("b"));
    try {
      iterator.remove();
      fail();
    } catch (ConcurrentModificationException expected) {}
    assertEquals(1, ms.size());
    assertTrue(ms.contains("b"));
  }
}
