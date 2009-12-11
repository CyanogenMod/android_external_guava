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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import com.google.common.collect.testing.CollectionTestSuiteBuilder;
import com.google.common.collect.testing.TestStringCollectionGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.testers.CollectionIteratorTester.getIteratorKnownOrderRemoveSupportedMethod;
import com.google.common.testing.junit3.JUnitAsserts;
import com.google.common.testutils.NullPointerTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Tests for {@link Collections2}.
 *
 * <p>Intentionally not testing {@link Collections2#setEquals(Set, Object)} as
 * it is not public, and it can be better tested indirectly using the collection
 * testers.
 *
 * @author Chris Povirk
 * @author Jared Levy
 */
public class Collections2Test extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite(Collections2Test.class.getSimpleName());
    suite.addTest(testsForFilter());
    suite.addTest(testsForFilterAll());
    suite.addTest(testsForFilterLinkedList());
    suite.addTest(testsForFilterNoNulls());
    suite.addTest(testsForFilterFiltered());
    suite.addTest(testsForTransform());    
    suite.addTestSuite(Collections2Test.class);
    return suite;
  }

  static final Predicate<String> NOT_YYY_ZZZ = new Predicate<String>() {
      public boolean apply(String input) {
        return !"yyy".equals(input) && !"zzz".equals(input);
      }
  };

  static final Predicate<String> LENGTH_1 = new Predicate<String>() {
    public boolean apply(String input) {
      return input.length() == 1;
    }
  };

  static final Predicate<String> STARTS_WITH_VOWEL = new Predicate<String>() {
    public boolean apply(String input) {
      return Arrays.asList('a', 'e', 'i', 'o', 'u').contains(input.charAt(0));
    }
  };

  private static Test testsForFilter() {
    return CollectionTestSuiteBuilder.using(
        new TestStringCollectionGenerator() {
          @Override public Collection<String> create(String[] elements) {
            List<String> unfiltered = newArrayList();
            unfiltered.add("yyy");
            unfiltered.addAll(Arrays.asList(elements));
            unfiltered.add("zzz");
            return Collections2.filter(unfiltered, NOT_YYY_ZZZ);
          }
        })
        .named("Collections2.filter")
        .withFeatures(
            CollectionFeature.GENERAL_PURPOSE,
            CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .suppressing(getIteratorKnownOrderRemoveSupportedMethod())
        .createTestSuite();
  }

  private static Test testsForFilterAll() {
    return CollectionTestSuiteBuilder.using(
        new TestStringCollectionGenerator() {
          @Override public Collection<String> create(String[] elements) {
            List<String> unfiltered = newArrayList();
            unfiltered.addAll(Arrays.asList(elements));
            return Collections2.filter(unfiltered, NOT_YYY_ZZZ);
          }
        })
        .named("Collections2.filter")
        .withFeatures(
            CollectionFeature.GENERAL_PURPOSE,
            CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .suppressing(getIteratorKnownOrderRemoveSupportedMethod())
        .createTestSuite();
  }

  private static Test testsForFilterLinkedList() {
    return CollectionTestSuiteBuilder.using(
        new TestStringCollectionGenerator() {
          @Override public Collection<String> create(String[] elements) {
            List<String> unfiltered = newLinkedList();
            unfiltered.add("yyy");
            unfiltered.addAll(Arrays.asList(elements));
            unfiltered.add("zzz");
            return Collections2.filter(unfiltered, NOT_YYY_ZZZ);
          }
        })
        .named("Collections2.filter")
        .withFeatures(
            CollectionFeature.GENERAL_PURPOSE,
            CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .suppressing(getIteratorKnownOrderRemoveSupportedMethod())
        .createTestSuite();
  }

  private static Test testsForFilterNoNulls() {
    return CollectionTestSuiteBuilder.using(
        new TestStringCollectionGenerator() {
          @Override public Collection<String> create(String[] elements) {
            List<String> unfiltered = newArrayList();
            unfiltered.add("yyy");
            unfiltered.addAll(ImmutableList.of(elements));
            unfiltered.add("zzz");
            return Collections2.filter(unfiltered, LENGTH_1);
          }
        })
        .named("Collections2.filter, no nulls")
        .withFeatures(
            CollectionFeature.GENERAL_PURPOSE,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .suppressing(getIteratorKnownOrderRemoveSupportedMethod())
        .createTestSuite();
  }

  private static Test testsForFilterFiltered() {
    return CollectionTestSuiteBuilder.using(
        new TestStringCollectionGenerator() {
          @Override public Collection<String> create(String[] elements) {
            List<String> unfiltered = newArrayList();
            unfiltered.add("yyy");
            unfiltered.addAll(ImmutableList.of(elements));
            unfiltered.add("zzz");
            unfiltered.add("abc");
            return Collections2.filter(
                Collections2.filter(unfiltered, LENGTH_1), NOT_YYY_ZZZ);
          }
        })
        .named("Collections2.filter, filtered input")
        .withFeatures(
            CollectionFeature.GENERAL_PURPOSE,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .suppressing(getIteratorKnownOrderRemoveSupportedMethod())
        .createTestSuite();
  }

  abstract public static class FilterChangeTest extends TestCase {
    protected abstract <E> List<E> newList();

    public void testFilterIllegalAdd() {
      List<String> unfiltered = newList();
      Collection<String> filtered
          = Collections2.filter(unfiltered, NOT_YYY_ZZZ);
      filtered.add("a");
      filtered.add("b");
      JUnitAsserts.assertContentsInOrder(filtered, "a", "b");

      try {
        filtered.add("yyy");
        fail();
      } catch (IllegalArgumentException expected) {}

      try {
        filtered.addAll(Arrays.asList("c", "zzz", "d"));
        fail();
      } catch (IllegalArgumentException expected) {}

      JUnitAsserts.assertContentsInOrder(filtered, "a", "b");
    }
    
    public void testFilterChangeUnfiltered() {
      List<String> unfiltered = newList();
      Collection<String> filtered
          = Collections2.filter(unfiltered, NOT_YYY_ZZZ);

      unfiltered.add("a");
      unfiltered.add("yyy");
      unfiltered.add("b");
      JUnitAsserts.assertContentsInOrder(unfiltered, "a", "yyy", "b");
      JUnitAsserts.assertContentsInOrder(filtered, "a", "b");

      unfiltered.remove("a");
      JUnitAsserts.assertContentsInOrder(unfiltered, "yyy", "b");
      JUnitAsserts.assertContentsInOrder(filtered, "b");

      unfiltered.clear();
      JUnitAsserts.assertContentsInOrder(unfiltered);
      JUnitAsserts.assertContentsInOrder(filtered);
      
      unfiltered.add("yyy");
      JUnitAsserts.assertContentsInOrder(unfiltered, "yyy");
      JUnitAsserts.assertContentsInOrder(filtered);
      filtered.clear();
      JUnitAsserts.assertContentsInOrder(unfiltered, "yyy");
      JUnitAsserts.assertContentsInOrder(filtered);

      unfiltered.clear();
      filtered.clear();
      JUnitAsserts.assertContentsInOrder(unfiltered);
      JUnitAsserts.assertContentsInOrder(filtered);

      unfiltered.add("a");
      JUnitAsserts.assertContentsInOrder(unfiltered, "a");
      JUnitAsserts.assertContentsInOrder(filtered, "a");
      filtered.clear();
      JUnitAsserts.assertContentsInOrder(unfiltered);
      JUnitAsserts.assertContentsInOrder(filtered);

      unfiltered.clear();
      unfiltered.add("a");
      unfiltered.add("b");
      unfiltered.add("yyy");
      unfiltered.add("zzz");
      unfiltered.add("c");
      unfiltered.add("d");
      unfiltered.add("yyy");
      unfiltered.add("zzz");
      JUnitAsserts.assertContentsInOrder(unfiltered, "a", "b", "yyy", "zzz",
          "c", "d", "yyy", "zzz");
      JUnitAsserts.assertContentsInOrder(filtered, "a", "b", "c", "d");
      filtered.clear();
      JUnitAsserts.assertContentsInOrder(unfiltered, "yyy", "zzz", "yyy", 
          "zzz");
      JUnitAsserts.assertContentsInOrder(filtered);
    }

    public void testFilterChangeFiltered() {
      List<String> unfiltered = newList();
      Collection<String> filtered
          = Collections2.filter(unfiltered, NOT_YYY_ZZZ);

      unfiltered.add("a");
      unfiltered.add("yyy");
      filtered.add("b");
      JUnitAsserts.assertContentsInOrder(unfiltered, "a", "yyy", "b");
      JUnitAsserts.assertContentsInOrder(filtered, "a", "b");

      filtered.remove("a");
      JUnitAsserts.assertContentsInOrder(unfiltered, "yyy", "b");
      JUnitAsserts.assertContentsInOrder(filtered, "b");

      filtered.clear();
      JUnitAsserts.assertContentsInOrder(unfiltered, "yyy");
      JUnitAsserts.assertContentsInOrder(filtered);
    }

    public void testFilterFiltered() {
      List<String> unfiltered = newList();
      Collection<String> filtered = Collections2.filter(
          Collections2.filter(unfiltered, LENGTH_1), STARTS_WITH_VOWEL);
      unfiltered.add("a");
      unfiltered.add("b");
      unfiltered.add("apple");
      unfiltered.add("banana");
      unfiltered.add("e");
      JUnitAsserts.assertContentsInOrder(filtered, "a", "e");
      JUnitAsserts.assertContentsInOrder(unfiltered,
          "a", "b", "apple", "banana", "e");

      try {
        filtered.add("d");
        fail();
      } catch (IllegalArgumentException expected) {}
      try {
        filtered.add("egg");
        fail();
      } catch (IllegalArgumentException expected) {}
      JUnitAsserts.assertContentsInOrder(filtered, "a", "e");
      JUnitAsserts.assertContentsInOrder(unfiltered,
          "a", "b", "apple", "banana", "e");

      filtered.clear();
      assertTrue(filtered.isEmpty());
      JUnitAsserts.assertContentsInOrder(unfiltered, "b", "apple", "banana");
    }
  }
  
  public static class ArrayListFilterChangeTest extends FilterChangeTest {
    @Override protected <E> List<E> newList() {
      return Lists.newArrayList();
    }
  }  

  public static class LinkedListFilterChangeTest extends FilterChangeTest {
    @Override protected <E> List<E> newList() {
      return Lists.newLinkedList();
    }
  }  

  private static final Function<String, String> REMOVE_FIRST_CHAR
      = new Function<String, String>() {
        public String apply(String from) {
          return ((from == null) || "".equals(from))
              ? null : from.substring(1);
        }
      };

  private static Test testsForTransform() {
    return CollectionTestSuiteBuilder.using(
        new TestStringCollectionGenerator() {
          @Override public Collection<String> create(String[] elements) {
            List<String> list = newArrayList();
            for (String element : elements) {
              list.add((element == null) ? null : "q" + element);
            }
            return Collections2.transform(list, REMOVE_FIRST_CHAR);
          }
        })
        .named("Collections2.transform")
        .withFeatures(
            CollectionFeature.REMOVE_OPERATIONS,
            CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .createTestSuite();
  }

  public void testNullPointerExceptions() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.testAllPublicStaticMethods(Collections2.class);
  }
}
