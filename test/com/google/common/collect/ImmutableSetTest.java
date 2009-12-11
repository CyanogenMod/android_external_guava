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

import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.testutils.NullPointerTester;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Unit test for {@link ImmutableSet}.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 * @author Nick Kralevich
 */
public class ImmutableSetTest extends AbstractImmutableSetTest {

  @Override protected Set<String> of() {
    return ImmutableSet.of();
  }

  @Override protected Set<String> of(String e) {
    return ImmutableSet.of(e);
  }

  @Override protected Set<String> of(String e1, String e2) {
    return ImmutableSet.of(e1, e2);
  }

  @Override protected Set<String> of(String e1, String e2, String e3) {
    return ImmutableSet.of(e1, e2, e3);
  }

  @Override protected Set<String> of(
      String e1, String e2, String e3, String e4) {
    return ImmutableSet.of(e1, e2, e3, e4);
  }

  @Override protected Set<String> of(
      String e1, String e2, String e3, String e4, String e5) {
    return ImmutableSet.of(e1, e2, e3, e4, e5);
  }

  @Override protected Set<String> of(String... elements) {
    return ImmutableSet.of(elements);
  }

  @Override protected Set<String> copyOf(Iterable<String> elements) {
    return ImmutableSet.copyOf(elements);
  }

  @Override protected Set<String> copyOf(Iterator<String> elements) {
    return ImmutableSet.copyOf(elements);
  }

  public void testCreation_arrayOfArray() {
    String[] array = new String[] { "a" };
    Set<String[]> set = ImmutableSet.<String[]>of(array);
    assertEquals(Collections.singleton(array), set);
  }

  public void testNullPointers() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(ImmutableSet.class);
  }

  public void testChooseTableSize() {
    assertEquals(8, Hashing.chooseTableSize(3));
    assertEquals(16, Hashing.chooseTableSize(4));

    assertEquals(1 << 30, Hashing.chooseTableSize(1 << 28));
    assertEquals(1 << 30, Hashing.chooseTableSize(1 << 29 - 1));

    // Now we hit the cap
    assertEquals(1 << 30, Hashing.chooseTableSize(1 << 29));
    assertEquals(1 << 30, Hashing.chooseTableSize(1 << 30 - 1));

    // Now we've gone too far
    try {
      Hashing.chooseTableSize(1 << 30);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testCopyOf_copiesImmutableSortedSet() {
    ImmutableSortedSet<String> sortedSet = ImmutableSortedSet.of("a");
    ImmutableSet<String> copy = ImmutableSet.copyOf(sortedSet);
    assertNotSame(sortedSet, copy);
  }

  @Override <E extends Comparable<E>> Builder<E> builder() {
    return ImmutableSet.builder();
  }

  @Override int getComplexBuilderSetLastElement() {
    return LAST_COLOR_ADDED;
  }
}
