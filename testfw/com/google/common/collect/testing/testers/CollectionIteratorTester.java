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

package com.google.common.collect.testing.testers;
// TODO

import com.google.common.collect.testing.AbstractCollectionTester;
import com.google.common.collect.testing.Helpers;
import com.google.common.collect.testing.IteratorFeature;
import static com.google.common.collect.testing.IteratorFeature.MODIFIABLE;
import static com.google.common.collect.testing.IteratorFeature.UNMODIFIABLE;
import com.google.common.collect.testing.IteratorTester;
import com.google.common.collect.testing.features.CollectionFeature;
import static com.google.common.collect.testing.features.CollectionFeature.KNOWN_ORDER;
import static com.google.common.collect.testing.features.CollectionFeature.SUPPORTS_REMOVE;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A generic JUnit test which tests {@code iterator} operations on a collection.
 * Can't be invoked directly; please see
 * {@link com.google.common.collect.testing.CollectionTestSuiteBuilder}.
 *
 * @author Chris Povirk
 */
public class CollectionIteratorTester<E> extends AbstractCollectionTester<E> {
  public void testIterator() {
    List<E> iteratorElements = new ArrayList<E>();
    for (E element : collection) { // uses iterator()
      iteratorElements.add(element);
    }
    Helpers.assertEqualIgnoringOrder(
        Arrays.asList(createSamplesArray()), iteratorElements);
  }

  @CollectionFeature.Require(KNOWN_ORDER)
  public void testIterationOrdering() {
    List<E> iteratorElements = new ArrayList<E>();
    for (E element : collection) { // uses iterator()
      iteratorElements.add(element);
    }
    List<E> expected = Helpers.copyToList(getOrderedElements());
    assertEquals("Different ordered iteration", expected, iteratorElements);
  }

  // TODO(Chris Povirk): switch to DerivedIteratorTestSuiteBuilder

  @CollectionFeature.Require({KNOWN_ORDER, SUPPORTS_REMOVE})
  public void testIterator_knownOrderRemoveSupported() throws Exception {
    runIteratorTest(MODIFIABLE, IteratorTester.KnownOrder.KNOWN_ORDER);
  }

  @CollectionFeature.Require(value = KNOWN_ORDER, absent = SUPPORTS_REMOVE)
  public void testIterator_knownOrderRemoveUnsupported() throws Exception {
    runIteratorTest(UNMODIFIABLE, IteratorTester.KnownOrder.KNOWN_ORDER);
  }

  @CollectionFeature.Require(absent = KNOWN_ORDER, value = SUPPORTS_REMOVE)
  public void testIterator_unknownOrderRemoveSupported() throws Exception {
    runIteratorTest(MODIFIABLE, IteratorTester.KnownOrder.UNKNOWN_ORDER);
  }

  @CollectionFeature.Require(absent = {KNOWN_ORDER, SUPPORTS_REMOVE})
  public void testIterator_unknownOrderRemoveUnsupported() throws Exception {
    runIteratorTest(UNMODIFIABLE, IteratorTester.KnownOrder.UNKNOWN_ORDER);
  }

  private void runIteratorTest(Set<IteratorFeature> features,
      IteratorTester.KnownOrder knownOrder) throws Exception {
    new IteratorTester<E>(5, features, getOrderedElements(), knownOrder) {
      {
        // TODO(Chris Povirk): don't set this universally
        ignoreSunJavaBug6529795();
      }

      @Override protected Iterator<E> newTargetIterator() {
        resetCollection();
        return collection.iterator();
      }

      @Override protected void verify(List<E> elements) {
        expectContents(elements);
      }
    }.test();
  }

  /**
   * Returns the {@link Method} instance for
   * {@link #testIterator_knownOrderRemoveSupported()} so that tests of
   * {@link CopyOnWriteArraySet} and {@link CopyOnWriteArrayList} can suppress
   * it with {@code FeatureSpecificTestSuiteBuilder.suppressing()} until <a
   * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6570575">Sun bug
   * 6570575</a> is fixed.
   */
  public static Method getIteratorKnownOrderRemoveSupportedMethod() {
    try {
      return CollectionIteratorTester.class
          .getMethod("testIterator_knownOrderRemoveSupported");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the {@link Method} instance for
   * {@link #testIterator_unknownOrderRemoveSupported()} so that tests of
   * classes with unmodifiable iterators can suppress it.
   */
  public static Method getIteratorUnknownOrderRemoveSupportedMethod() {
    try {
      return CollectionIteratorTester.class
          .getMethod("testIterator_unknownOrderRemoveSupported");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public void testIteratorNoSuchElementException() {
    Iterator<E> iterator = collection.iterator();
    while (iterator.hasNext()) {
      iterator.next();
    }

    try {
      iterator.next();
      fail("iterator.next() should throw NoSuchElementException");
    } catch (NoSuchElementException expected) {}
  }

  /**
   * Returns the {@link Method} instance for
   * {@link #testIterator_knownOrderRemoveUnsupported()} so that tests of
   * {@code ArrayStack} can suppress it with
   * {@code FeatureSpecificTestSuiteBuilder.suppressing()}. {@code ArrayStack}
   * supports {@code remove()} on only the first element, and the iterator
   * tester can't handle that.
   */
  public static Method getIteratorKnownOrderRemoveUnsupportedMethod() {
    try {
      return CollectionIteratorTester.class
          .getMethod("testIterator_knownOrderRemoveUnsupported");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
