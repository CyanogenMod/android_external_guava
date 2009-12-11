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

package com.google.common.collect;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.lang.reflect.Method;

/**
 * Test suite for Google Collections.
 * 
 * @author Chris Povirk
 */
public class CollectTestSuite {
  public static TestSuite suite() throws Exception {
    TestSuite suite = new TestSuite(CollectTestSuite.class.getName());

    for (String className : CLASS_NAMES) {
      Class<?> testClass = Class.forName(className);

      try {
        Method suiteMethod = testClass.getMethod("suite");
        suite.addTest((Test) suiteMethod.invoke(null));
      } catch (NoSuchMethodException e) {
        suite.addTestSuite(testClass);
      }
    }

    return suite;
  }

  private static final String[] CLASS_NAMES = new String[] {
      "com.google.common.base.FinalizableReferenceQueueTest",
      "com.google.common.base.FunctionsTest",
      "com.google.common.base.JoinerTest",
      "com.google.common.base.ObjectsTest",
      "com.google.common.base.PreconditionsTest",
      "com.google.common.base.PredicatesTest",
      "com.google.common.base.SuppliersTest",
      "com.google.common.collect.AbstractIteratorTest",
      "com.google.common.collect.AbstractMapEntryTest",
      "com.google.common.collect.ArrayListMultimapTest",
      "com.google.common.collect.BiMapCollectionTest",
      "com.google.common.collect.BiMapMapInterfaceTest$HashBiMapInterfaceTest",
      "com.google.common.collect.BiMapMapInterfaceTest$InverseBiMapInterfaceTest",
      "com.google.common.collect.BiMapMapInterfaceTest$SynchronizedBiMapInterfaceTest",
      "com.google.common.collect.BiMapMapInterfaceTest$UnmodifiableBiMapInterfaceTest",
      "com.google.common.collect.Collections2Test",
      "com.google.common.collect.Collections2Test$ArrayListFilterChangeTest",
      "com.google.common.collect.Collections2Test$LinkedListFilterChangeTest",
      "com.google.common.collect.ConcurrentHashMultisetTest",
      "com.google.common.collect.ConcurrentHashMultisetWithChmTest",
      "com.google.common.collect.EnumBiMapTest",
      "com.google.common.collect.EnumHashBiMapTest",
      "com.google.common.collect.EnumMultisetTest",
      "com.google.common.collect.FauxveridesTest",
      "com.google.common.collect.ForMapMultimapAsMapImplementsMapTest",
      "com.google.common.collect.ForwardingCollectionTest",
      "com.google.common.collect.ForwardingConcurrentMapTest",
      "com.google.common.collect.ForwardingListIteratorTest",
      "com.google.common.collect.ForwardingListTest",
      "com.google.common.collect.ForwardingMapTest",
      "com.google.common.collect.ForwardingMultimapTest",
      "com.google.common.collect.ForwardingMultisetTest",
      "com.google.common.collect.ForwardingObjectTest",
      "com.google.common.collect.ForwardingQueueTest",
      "com.google.common.collect.ForwardingSetTest",
      "com.google.common.collect.ForwardingSortedMapImplementsMapTest",
      "com.google.common.collect.ForwardingSortedMapTest",
      "com.google.common.collect.HashBiMapTest",
      "com.google.common.collect.HashMultimapTest",
      "com.google.common.collect.HashMultisetTest",
      "com.google.common.collect.ImmutableBiMapTest",
      "com.google.common.collect.ImmutableBiMapTest$BiMapSpecificTests",
      "com.google.common.collect.ImmutableBiMapTest$CreationTests",
      "com.google.common.collect.ImmutableBiMapTest$InverseMapTests",
      "com.google.common.collect.ImmutableBiMapTest$MapTests",
      "com.google.common.collect.ImmutableClassToInstanceMapTest",
      "com.google.common.collect.ImmutableListMultimapTest",
      "com.google.common.collect.ImmutableListTest",
      "com.google.common.collect.ImmutableListTest$CreationTests",
      "com.google.common.collect.ImmutableMapTest",
      "com.google.common.collect.ImmutableMapTest$CreationTests",
      "com.google.common.collect.ImmutableMapTest$MapTests",
      "com.google.common.collect.ImmutableMapTest$MapTestsWithBadHashes",
      "com.google.common.collect.ImmutableMapTest$MapTestsWithSingletonUnhashableValue",
      "com.google.common.collect.ImmutableMapTest$MapTestsWithUnhashableValues",
      "com.google.common.collect.ImmutableMapTest$ReserializedMapTests",
      "com.google.common.collect.ImmutableMapTest$SingletonMapTests",
      "com.google.common.collect.ImmutableMultimapAsMapImplementsMapTest",
      "com.google.common.collect.ImmutableMultimapTest",
      "com.google.common.collect.ImmutableMultisetTest",
      "com.google.common.collect.ImmutableSetCollectionTest",
      "com.google.common.collect.ImmutableSetMultimapAsMapImplementsMapTest",
      "com.google.common.collect.ImmutableSetMultimapTest",
      "com.google.common.collect.ImmutableSetTest",
      "com.google.common.collect.ImmutableSortedMapTest",
      "com.google.common.collect.ImmutableSortedMapTest$CreationTests",
      "com.google.common.collect.ImmutableSortedMapTest$HeadMapTests",
      "com.google.common.collect.ImmutableSortedMapTest$MapTests",
      "com.google.common.collect.ImmutableSortedMapTest$ReserializedMapTests",
      "com.google.common.collect.ImmutableSortedMapTest$SingletonMapTests",
      "com.google.common.collect.ImmutableSortedMapTest$SubMapTests",
      "com.google.common.collect.ImmutableSortedMapTest$TailMapTests",
      "com.google.common.collect.ImmutableSortedSetTest",
      "com.google.common.collect.InverseBiMapTest",
      "com.google.common.collect.IterablesTest",
      "com.google.common.collect.IteratorsTest",
      "com.google.common.collect.Jsr166HashMapTest",
      "com.google.common.collect.LinkedHashMultimapTest",
      "com.google.common.collect.LinkedHashMultisetTest",
      "com.google.common.collect.LinkedListMultimapTest",
      "com.google.common.collect.ListsTest",
      "com.google.common.collect.MapMakerTestSuite$ComputingTest",
      "com.google.common.collect.MapMakerTestSuite$ExpiringComputingReferenceMapTest",
      "com.google.common.collect.MapMakerTestSuite$ExpiringReferenceMapTest",
      "com.google.common.collect.MapMakerTestSuite$MakerTest",
      "com.google.common.collect.MapMakerTestSuite$RecursiveComputationTest",
      "com.google.common.collect.MapMakerTestSuite$ReferenceCombinationTestSuite",
      "com.google.common.collect.MapMakerTestSuite$ReferenceMapTest",
      "com.google.common.collect.MapsTest",
      "com.google.common.collect.MapsTest$FilteredMapTests",
      "com.google.common.collect.MapsTransformValuesTest",
      "com.google.common.collect.MultimapCollectionTest",
      "com.google.common.collect.MultimapsTest",
      "com.google.common.collect.MultisetCollectionTest",
      "com.google.common.collect.MultisetsImmutableEntryTest",
      "com.google.common.collect.MultisetsTest",
      "com.google.common.collect.MutableClassToInstanceMapTest",
      "com.google.common.collect.ObjectArraysTest",
      "com.google.common.collect.OrderingTest",
      "com.google.common.collect.PeekingIteratorTest",
      "com.google.common.collect.SetOperationsTest",
      "com.google.common.collect.SetOperationsTest$MoreTests",
      "com.google.common.collect.SetsTest",
      "com.google.common.collect.SimpleAbstractMultisetTest",
      "com.google.common.collect.SubMapMultimapAsMapImplementsMapTest",
      "com.google.common.collect.SynchronizedBiMapTest",
      "com.google.common.collect.SynchronizedBiMapTest$AbstractBiMapTests",
      "com.google.common.collect.SynchronizedMapTest",
      "com.google.common.collect.SynchronizedMultimapTest",
      "com.google.common.collect.SynchronizedSetTest",
      "com.google.common.collect.TreeMultimapExplicitTest",
      "com.google.common.collect.TreeMultimapNaturalTest",
      "com.google.common.collect.TreeMultisetTest",
      "com.google.common.collect.UnmodifiableIteratorTest",
      "com.google.common.collect.UnmodifiableMultimapAsMapImplementsMapTest",
      "com.google.common.collect.testing.IteratorTesterTest",
      "com.google.common.collect.testing.MapTestSuiteBuilderTests",
      "com.google.common.collect.testing.MinimalCollectionTest",
      "com.google.common.collect.testing.MinimalIterableTest",
      "com.google.common.collect.testing.MinimalSetTest",
      "com.google.common.collect.testing.OpenJdk6Tests",
      "com.google.common.collect.testing.features.FeatureEnumTest",
      "com.google.common.collect.testing.features.FeatureUtilTest",
  };
}
