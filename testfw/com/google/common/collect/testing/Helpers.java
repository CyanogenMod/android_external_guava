/*
 * Copyright (C) 2009 Google Inc.
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

package com.google.common.collect.testing;

import junit.framework.Assert;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import junit.framework.AssertionFailedError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;

// This class is GWT compatible.
public class Helpers {
  public static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }

  static boolean equal(Object a, Object b) {
    return a == b || (a != null && a.equals(b));
  }

  public static <E> List<E> copyToList(Iterable<? extends E> elements) {
    List<E> list = new ArrayList<E>();
    addAll(list, elements);
    return list;
  }

  public static <E> List<E> copyToList(E[] elements) {
    return copyToList(Arrays.asList(elements));
  }

  public static <E> Set<E> copyToSet(Iterable<? extends E> elements) {
    Set<E> set = new LinkedHashSet<E>();
    addAll(set, elements);
    return set;
  }

  public static <E> Set<E> copyToSet(E[] elements) {
    return copyToSet(Arrays.asList(elements));
  }

  public static <K, V> Entry<K, V> mapEntry(K key, V value) {
    return Collections.singletonMap(key, value).entrySet().iterator().next();
  }

  public static void assertEqualIgnoringOrder(
      Iterable<?> expected, Iterable<?> actual) {
    List<?> exp = copyToList(expected);
    List<?> act = copyToList(actual);
    String actString = act.toString();

    // Of course we could take pains to give the complete description of the
    // problem on any failure.

    // Yeah it's n^2.
    for (Object object : exp) {
      if (!act.remove(object)) {
        Assert.fail("did not contain expected element " + object + ", "
            + "expected = " + exp + ", actual = " + actString);
      }
    }
    assertTrue("unexpected elements: " + act, act.isEmpty());
  }

  public static void assertContentsAnyOrder(
      Iterable<?> actual, Object... expected) {
    assertEqualIgnoringOrder(Arrays.asList(expected), actual);
  }

  public static <E> boolean addAll(
      Collection<E> addTo, Iterable<? extends E> elementsToAdd) {
    boolean modified = false;
    for (E e : elementsToAdd) {
      modified |= addTo.add(e);
    }
    return modified;
  }

  static <T> Iterable<T> reverse(final List<T> list) {
    return new Iterable<T>() {
      public Iterator<T> iterator() {
        final ListIterator<T> listIter = list.listIterator(list.size());
        return new Iterator<T>() {
          public boolean hasNext() {
            return listIter.hasPrevious();
          }
          public T next() {
            return listIter.previous();
          }
          public void remove() {
            listIter.remove();
          }
        };
      }
    };
  }

  static <T> Iterator<T> cycle(final Iterable<T> iterable) {
    return new Iterator<T>() {
      Iterator<T> iterator = Collections.<T>emptySet().iterator();
      public boolean hasNext() {
        return true;
      }
      public T next() {
        if (!iterator.hasNext()) {
          iterator = iterable.iterator();
        }
        return iterator.next();
      }
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  static <T> T get(Iterator<T> iterator, int position) {
    for (int i = 0; i < position; i++) {
      iterator.next();
    }
    return iterator.next();
  }

  static void fail(Throwable cause, Object message) {
    AssertionFailedError assertionFailedError =
        new AssertionFailedError(String.valueOf(message));
    assertionFailedError.initCause(cause);
    throw assertionFailedError;
  }

  public static <T> void testComparator(
      Comparator<? super T> comparator, T... valuesInExpectedOrder) {
    testComparator(comparator, Arrays.asList(valuesInExpectedOrder));
  }

  public static <T> void testComparator(
      Comparator<? super T> comparator, List<T> valuesInExpectedOrder) {
    // This does an O(n^2) test of all pairs of values in both orders
    for (int i = 0; i < valuesInExpectedOrder.size(); i++) {
      T t = valuesInExpectedOrder.get(i);

      for (int j = 0; j < i; j++) {
        T lesser = valuesInExpectedOrder.get(j);
        assertTrue(comparator + ".compare(" + lesser + ", " + t + ")",
            comparator.compare(lesser, t) < 0);
      }

      assertEquals(comparator + ".compare(" + t + ", " + t + ")",
          0, comparator.compare(t, t));

      for (int j = i + 1; j < valuesInExpectedOrder.size(); j++) {
        T greater = valuesInExpectedOrder.get(j);
        assertTrue(comparator + ".compare(" + greater + ", " + t + ")",
            comparator.compare(greater, t) > 0);
      }
    }
  }

  public static <T extends Comparable<? super T>> void testCompareToAndEquals(
      List<T> valuesInExpectedOrder) {
    // This does an O(n^2) test of all pairs of values in both orders
    for (int i = 0; i < valuesInExpectedOrder.size(); i++) {
      T t = valuesInExpectedOrder.get(i);

      for (int j = 0; j < i; j++) {
        T lesser = valuesInExpectedOrder.get(j);
        assertTrue(lesser + ".compareTo(" + t + ')', lesser.compareTo(t) < 0);
        assertFalse(lesser.equals(t));
      }

      assertEquals(t + ".compareTo(" + t + ')', 0, t.compareTo(t));
      assertTrue(t.equals(t));

      for (int j = i + 1; j < valuesInExpectedOrder.size(); j++) {
        T greater = valuesInExpectedOrder.get(j);
        assertTrue(greater + ".compareTo(" + t + ')', greater.compareTo(t) > 0);
        assertFalse(greater.equals(t));
      }
    }
  }
}
