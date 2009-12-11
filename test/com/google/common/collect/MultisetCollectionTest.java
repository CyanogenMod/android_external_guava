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

import com.google.common.collect.testing.AnEnum;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.google.AbstractMultisetSetCountTester.getSetCountDuplicateInitializingMethods;
import static com.google.common.collect.testing.google.MultisetReadsTester.getReadsDuplicateInitializingMethods;
import com.google.common.collect.testing.google.MultisetTestSuiteBuilder;
import com.google.common.collect.testing.google.TestEnumMultisetGenerator;
import com.google.common.collect.testing.google.TestStringMultisetGenerator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;

/**
 * Collection tests for {@link Multiset} implementations.
 *
 * @author Jared Levy
 */
public class MultisetCollectionTest extends TestCase {

  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(MultisetTestSuiteBuilder.using(hashMultisetGenerator())
        .withFeatures(CollectionSize.ANY,
            CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionFeature.GENERAL_PURPOSE)
        .named("HashMultiset")
        .createTestSuite());

    suite.addTest(MultisetTestSuiteBuilder.using(
        unmodifiableMultisetGenerator())
        .withFeatures(CollectionSize.ANY, CollectionFeature.KNOWN_ORDER)
        .named("UnmodifiableTreeMultiset")
        .createTestSuite());

    suite.addTest(MultisetTestSuiteBuilder.using(forSetGenerator())
        .withFeatures(CollectionSize.ANY, CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionFeature.REMOVE_OPERATIONS)
        .suppressing(getReadsDuplicateInitializingMethods())
        .suppressing(getSetCountDuplicateInitializingMethods())
        .named("ForSetMultiset")
        .createTestSuite());

    suite.addTest(MultisetTestSuiteBuilder.using(
        concurrentMultisetGenerator())
        .withFeatures(CollectionSize.ANY,
            CollectionFeature.GENERAL_PURPOSE)
        .named("ConcurrentHashMultiset")
        .createTestSuite());

    suite.addTest(MultisetTestSuiteBuilder.using(enumMultisetGenerator())
        .withFeatures(CollectionSize.ANY,
            CollectionFeature.KNOWN_ORDER,
            CollectionFeature.GENERAL_PURPOSE)
        .named("EnumMultiset")
        .createTestSuite());

    return suite;
  }

  private static TestStringMultisetGenerator hashMultisetGenerator() {
    return new TestStringMultisetGenerator() {
      @Override protected Multiset<String> create(String[] elements) {
        return HashMultiset.create(asList(elements));
      }
    };
  }

  private static TestStringMultisetGenerator unmodifiableMultisetGenerator() {
    return new TestStringMultisetGenerator() {
      @Override protected Multiset<String> create(String[] elements) {
        return Multisets.unmodifiableMultiset(
            TreeMultiset.create(asList(elements)));
      }
      @Override public List<String> order(List<String> insertionOrder) {
        Collections.sort(insertionOrder);
        return insertionOrder;
      }
    };
  }

  private static TestStringMultisetGenerator forSetGenerator() {
    return new TestStringMultisetGenerator() {
      @Override protected Multiset<String> create(String[] elements) {
        return Multisets.forSet(Sets.newHashSet(elements));
      }
    };
  }

  private static TestStringMultisetGenerator concurrentMultisetGenerator() {
    return new TestStringMultisetGenerator() {
      @Override protected Multiset<String> create(String[] elements) {
        return ConcurrentHashMultiset.create(asList(elements));
      }
    };
  }

  private static TestEnumMultisetGenerator enumMultisetGenerator() {
    return new TestEnumMultisetGenerator() {
      @Override protected Multiset<AnEnum> create(AnEnum[] elements) {
        return (elements.length == 0)
            ? EnumMultiset.create(AnEnum.class)
            : EnumMultiset.create(asList(elements));
      }
    };
  }
}

