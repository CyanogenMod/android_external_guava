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

import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestCollidingSetGenerator;
import com.google.common.collect.testing.TestSetGenerator;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.TestStringSortedSetGenerator;
import com.google.common.collect.testing.TestUnhashableCollectionGenerator;
import com.google.common.collect.testing.UnhashableObject;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.testers.SetHashCodeTester;
import com.google.common.testutils.SerializableTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Collection tests for {@link ImmutableSet} and {@link ImmutableSortedSet}.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 */
public class ImmutableSetCollectionTest extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override protected Set<String> create(String[] elements) {
            return ImmutableSet.of(elements);
          }
        })
        .named(ImmutableSetTest.class.getName())
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override protected Set<String> create(String[] elements) {
            Set<String> set = ImmutableSet.of(elements);
            return SerializableTester.reserialize(set);
          }
        })
        .named(ImmutableSetTest.class.getName() + ", reserialized")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(new TestCollidingSetGenerator() {
          public Set<Object> create(Object... elements) {
            return ImmutableSet.of(elements);
          }
        })
        .named(ImmutableSetTest.class.getName() + ", with bad hashes")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          // Make sure we get what we think we're getting, or else this test
          // is pointless
          @SuppressWarnings("cast")
          @Override protected Set<String> create(String[] elements) {
            return (ImmutableSet<String>)
                ImmutableSet.of(elements[0], elements[0]);
          }
        })
        .named(ImmutableSetTest.class.getName() + ", degenerate")
        .withFeatures(CollectionSize.ONE, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        new TestStringSortedSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            return ImmutableSortedSet.of(elements);
          }
        })
        .named(ImmutableSortedSetTest.class.getName())
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        new TestStringSortedSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            SortedSet<String> set = ImmutableSortedSet.of(elements);
            return SerializableTester.reserialize(set);
          }
        })
        .named(ImmutableSortedSetTest.class.getName() + ", reserialized")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        new TestStringSortedSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            List<String> list = Lists.newArrayList(elements);
            list.add("zzz");
            return ImmutableSortedSet.copyOf(list)
                .headSet("zzy");
          }
        })
        .named(ImmutableSortedSetTest.class.getName() + ", headset")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        new TestStringSortedSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            List<String> list = Lists.newArrayList(elements);
            list.add("\0");
            return ImmutableSortedSet.copyOf(list)
                .tailSet("\0\0");
          }
        })
        .named(ImmutableSortedSetTest.class.getName() + ", tailset")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        new TestStringSortedSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            List<String> list = Lists.newArrayList(elements);
            list.add("\0");
            list.add("zzz");
            return ImmutableSortedSet.copyOf(list)
                .subSet("\0\0", "zzy");
          }
        })
        .named(ImmutableSortedSetTest.class.getName() + ", subset")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        new TestStringSortedSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            List<String> list = Lists.newArrayList(elements);
            list.add("zzz");
            return SerializableTester.reserialize(
                ImmutableSortedSet.copyOf(list).headSet("zzy"));
          }
        })
        .named(
            ImmutableSortedSetTest.class.getName() + ", headset, reserialized")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        new TestStringSortedSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            List<String> list = Lists.newArrayList(elements);
            list.add("\0");
            return SerializableTester.reserialize(
                ImmutableSortedSet.copyOf(list).tailSet("\0\0"));
          }
        })
        .named(
            ImmutableSortedSetTest.class.getName() + ", tailset, reserialized")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(
        new TestStringSortedSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            List<String> list = Lists.newArrayList(elements);
            list.add("\0");
            list.add("zzz");
            return SerializableTester.reserialize(
                ImmutableSortedSet.copyOf(list).subSet("\0\0", "zzy"));
          }
        })
        .named(
            ImmutableSortedSetTest.class.getName() + ", subset, reserialized")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    final Comparator<String> stringReversed = Collections.reverseOrder();

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            return ImmutableSortedSet.orderedBy(stringReversed)
                .add(elements)
                .build();
          }

          @Override public List<String> order(List<String> insertionOrder) {
            Collections.sort(insertionOrder, Collections.reverseOrder());
            return insertionOrder;
          }
        })
        .named(ImmutableSortedSetTest.class.getName()
            + ", explicit comparator, vararg")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    final Comparator<Comparable<?>> comparableReversed
        = Collections.reverseOrder();

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            return new ImmutableSortedSet.Builder<String>(comparableReversed)
                .add(elements)
                .build();
          }

          @Override public List<String> order(List<String> insertionOrder) {
            Collections.sort(insertionOrder, Collections.reverseOrder());
            return insertionOrder;
          }
        })
        .named(ImmutableSortedSetTest.class.getName()
            + ", explicit superclass comparator, iterable")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override protected SortedSet<String> create(String[] elements) {
            return ImmutableSortedSet.<String>reverseOrder()
                .addAll(
                    Arrays.asList(elements).iterator())
                .build();
          }

          @Override public List<String> order(List<String> insertionOrder) {
            Collections.sort(insertionOrder, Collections.reverseOrder());
            return insertionOrder;
          }
        })
        .named(ImmutableSortedSetTest.class.getName()
            + ", reverseOrder, iterator")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(new TestUnhashableSetGenerator() {
          @Override public Set<UnhashableObject> create(
              UnhashableObject[] elements) {
            return ImmutableSortedSet.of(elements);
          }
        })
        .suppressing(SetHashCodeTester.getHashCodeMethods())
        .named(ImmutableSortedSetTest.class.getName() + ", unhashable")
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .createTestSuite());

    return suite;
  }

  private abstract static class TestUnhashableSetGenerator
      extends TestUnhashableCollectionGenerator<Set<UnhashableObject>>
      implements TestSetGenerator<UnhashableObject> {
  }
}
