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

import static com.google.common.collect.Iterables.unmodifiableIterable;
import static com.google.common.collect.Sets.newEnumSet;
import com.google.common.collect.testing.AnEnum;
import com.google.common.collect.testing.MinimalIterable;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestEnumSetGenerator;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;
import static com.google.common.collect.testing.testers.CollectionIteratorTester.getIteratorKnownOrderRemoveSupportedMethod;
import com.google.common.testing.junit3.JUnitAsserts;
import static com.google.common.testing.junit3.JUnitAsserts.assertContentsInOrder;
import com.google.common.testutils.NullPointerTester;
import com.google.common.testutils.SerializableTester;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.io.ObjectStreamConstants.TC_REFERENCE;
import static java.io.ObjectStreamConstants.baseWireHandle;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Nullable;

/**
 * Unit test for {@code Sets}.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 */
public class SetsTest extends TestCase {

  private static final Collection<Integer> EMPTY_COLLECTION
      = Arrays.<Integer>asList();

  private static final Collection<Integer> SOME_COLLECTION
      = Arrays.asList(0, 1, 1);

  private static final Iterable<Integer> SOME_ITERABLE
      = new Iterable<Integer>() {
        public Iterator<Integer> iterator() {
          return SOME_COLLECTION.iterator();
        }
      };

  private static final List<Integer> LONGER_LIST
      = Arrays.asList(8, 6, 7, 5, 3, 0, 9);

  private static final Comparator<Integer> SOME_COMPARATOR
      = Collections.reverseOrder();

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(SetsTest.class);

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override protected Set<String> create(String[] elements) {
            int size = elements.length;
            // Remove last element, if size > 1
            Set<String> set1 = (size > 1)
                ? Sets.newHashSet(
                    Arrays.asList(elements).subList(0, size - 1))
                : Sets.newHashSet(elements);
            // Remove first element, if size > 0
            Set<String> set2 = (size > 0)
                ? Sets.newHashSet(
                    Arrays.asList(elements).subList(1, size))
                : Sets.<String>newHashSet();
            return Sets.union(set1, set2);
          }
        })
        .named("Sets.union")
        .withFeatures(CollectionSize.ANY, CollectionFeature.ALLOWS_NULL_VALUES)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override protected Set<String> create(String[] elements) {
            Set<String> set1 = Sets.newHashSet(elements);
            set1.add(samples().e3);
            Set<String> set2 = Sets.newHashSet(elements);
            set2.add(samples().e4);
            return Sets.intersection(set1, set2);
          }
        })
        .named("Sets.intersection")
        .withFeatures(CollectionSize.ANY, CollectionFeature.ALLOWS_NULL_VALUES)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override protected Set<String> create(String[] elements) {
            Set<String> set1 = Sets.newHashSet(elements);
            set1.add(samples().e3);
            Set<String> set2 = Sets.newHashSet(samples().e3);
            return Sets.difference(set1, set2);
          }
        })
        .named("Sets.difference")
        .withFeatures(CollectionSize.ANY, CollectionFeature.ALLOWS_NULL_VALUES)
        .createTestSuite());

    suite.addTest(SetTestSuiteBuilder.using(new TestEnumSetGenerator() {
          @Override protected Set<AnEnum> create(AnEnum[] elements) {
            AnEnum[] otherElements = new AnEnum[elements.length - 1];
            System.arraycopy(
                elements, 1, otherElements, 0, otherElements.length);
            return Sets.immutableEnumSet(elements[0], otherElements);
          }
        })
        .named("Sets.immutableEnumSet")
        .withFeatures(CollectionSize.ONE, CollectionSize.SEVERAL)
        .createTestSuite());

    suite.addTest(testsForFilter());
    suite.addTest(testsForFilterNoNulls());
    suite.addTest(testsForFilterFiltered());

    return suite;
  }

  private static Test testsForFilter() {
    return SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override public Set<String> create(String[] elements) {
            Set<String> unfiltered = Sets.newLinkedHashSet();
            unfiltered.add("yyy");
            unfiltered.addAll(Arrays.asList(elements));
            unfiltered.add("zzz");
            return Sets.filter(unfiltered, Collections2Test.NOT_YYY_ZZZ);
          }
        })
        .named("Sets.filter")
        .withFeatures(
            SetFeature.GENERAL_PURPOSE,
            CollectionFeature.ALLOWS_NULL_VALUES,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .suppressing(getIteratorKnownOrderRemoveSupportedMethod())
        .createTestSuite();
  }

  private static Test testsForFilterNoNulls() {
    return SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override public Set<String> create(String[] elements) {
            Set<String> unfiltered = Sets.newLinkedHashSet();
            unfiltered.add("yyy");
            unfiltered.addAll(ImmutableList.of(elements));
            unfiltered.add("zzz");
            return Sets.filter(unfiltered, Collections2Test.LENGTH_1);
          }
        })
        .named("Sets.filter, no nulls")
        .withFeatures(
            SetFeature.GENERAL_PURPOSE,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .suppressing(getIteratorKnownOrderRemoveSupportedMethod())
        .createTestSuite();
  }

  private static Test testsForFilterFiltered() {
    return SetTestSuiteBuilder.using(new TestStringSetGenerator() {
          @Override public Set<String> create(String[] elements) {
            Set<String> unfiltered = Sets.newLinkedHashSet();
            unfiltered.add("yyy");
            unfiltered.addAll(ImmutableList.of(elements));
            unfiltered.add("zzz");
            unfiltered.add("abc");
            return Sets.filter(
                Sets.filter(unfiltered, Collections2Test.LENGTH_1),
                Collections2Test.NOT_YYY_ZZZ);
          }
        })
        .named("Sets.filter, filtered input")
        .withFeatures(
            SetFeature.GENERAL_PURPOSE,
            CollectionFeature.KNOWN_ORDER,
            CollectionSize.ANY)
        .suppressing(getIteratorKnownOrderRemoveSupportedMethod())
        .createTestSuite();
  }

  private enum SomeEnum { A, B, C, D }

  public void testImmutableEnumSet() {
    Set<SomeEnum> units = Sets.immutableEnumSet(SomeEnum.D, SomeEnum.B);

    assertContentsInOrder(units, SomeEnum.B, SomeEnum.D);

    Set<SomeEnum> copy = SerializableTester.reserializeAndAssert(units);
    assertTrue(copy instanceof ImmutableEnumSet);

    try {
      units.remove(SomeEnum.B);
      fail("ImmutableEnumSet should throw an exception on remove()");
    } catch (UnsupportedOperationException expected) {}
    try {
      units.add(SomeEnum.C);
      fail("ImmutableEnumSet should throw an exception on add()");
    } catch (UnsupportedOperationException expected) {}
  }

  public void testImmutableEnumSet_fromIterable() {
    ImmutableSet<SomeEnum> none
        = Sets.immutableEnumSet(MinimalIterable.<SomeEnum>of());
    assertContentsInOrder(none);

    ImmutableSet<SomeEnum> one
        = Sets.immutableEnumSet(MinimalIterable.of(SomeEnum.B));
    assertContentsInOrder(one, SomeEnum.B);

    ImmutableSet<SomeEnum> two
        = Sets.immutableEnumSet(MinimalIterable.of(SomeEnum.D, SomeEnum.B));
    assertContentsInOrder(two, SomeEnum.B, SomeEnum.D);
  }

  public void testImmutableEnumSet_deserializationMakesDefensiveCopy()
      throws Exception {
    ImmutableSet<SomeEnum> original =
        Sets.immutableEnumSet(SomeEnum.A, SomeEnum.B);
    int handleOffset = 6;
    byte[] serializedForm = serializeWithBackReference(original, handleOffset);
    ObjectInputStream in =
        new ObjectInputStream(new ByteArrayInputStream(serializedForm));

    ImmutableSet<?> deserialized = (ImmutableSet<?>) in.readObject();
    EnumSet<?> delegate = (EnumSet<?>) in.readObject();

    assertEquals(original, deserialized);
    assertTrue(delegate.remove(SomeEnum.A));
    assertTrue(deserialized.contains(SomeEnum.A));
  }

  private static byte[] serializeWithBackReference(
      Object original, int handleOffset) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(bos);

    out.writeObject(original);

    byte[] handle = toByteArray(baseWireHandle + handleOffset);
    byte[] ref = prepended(TC_REFERENCE, handle);
    bos.write(ref);

    return bos.toByteArray();
  }

  private static byte[] prepended(byte b, byte[] array) {
    byte[] out = new byte[array.length + 1];
    out[0] = b;
    System.arraycopy(array, 0, out, 1, array.length);
    return out;
  }

  private static byte[] toByteArray(int h) {
    return ByteBuffer.allocate(4).putInt(h).array();
  }

  public void testNewEnumSet_empty() {
    EnumSet<SomeEnum> copy =
        newEnumSet(Collections.<SomeEnum>emptySet(), SomeEnum.class);
    assertEquals(EnumSet.noneOf(SomeEnum.class), copy);
  }

  public void testNewEnumSet_enumSet() {
    EnumSet<SomeEnum> set = EnumSet.of(SomeEnum.A, SomeEnum.D);
    assertEquals(set, newEnumSet(set, SomeEnum.class));
  }

  public void testNewEnumSet_collection() {
    Set<SomeEnum> set = ImmutableSet.of(SomeEnum.B, SomeEnum.C);
    assertEquals(set, newEnumSet(set, SomeEnum.class));
  }

  public void testNewEnumSet_iterable() {
    Set<SomeEnum> set = ImmutableSet.of(SomeEnum.A, SomeEnum.B, SomeEnum.C);
    assertEquals(set, newEnumSet(unmodifiableIterable(set), SomeEnum.class));
  }

  public void testNewHashSetEmpty() {
    HashSet<Integer> set = Sets.newHashSet();
    verifySetContents(set, EMPTY_COLLECTION);
  }

  public void testNewHashSetVarArgs() {
    HashSet<Integer> set = Sets.newHashSet(0, 1, 1);
    verifySetContents(set, Arrays.asList(0, 1));
  }

  public void testNewHashSetFromCollection() {
    HashSet<Integer> set = Sets.newHashSet(SOME_COLLECTION);
    verifySetContents(set, SOME_COLLECTION);
  }

  public void testNewHashSetFromIterable() {
    HashSet<Integer> set = Sets.newHashSet(SOME_ITERABLE);
    verifySetContents(set, SOME_ITERABLE);
  }

  public void testNewHashSetWithExpectedSizeSmall() {
    HashSet<Integer> set = Sets.newHashSetWithExpectedSize(0);
    verifySetContents(set, EMPTY_COLLECTION);
  }

  public void testNewHashSetWithExpectedSizeLarge() {
    HashSet<Integer> set = Sets.newHashSetWithExpectedSize(1000);
    verifySetContents(set, EMPTY_COLLECTION);
  }

  public void testNewHashSetFromIterator() {
    HashSet<Integer> set = Sets.newHashSet(SOME_COLLECTION.iterator());
    verifySetContents(set, SOME_COLLECTION);
  }

  public void testNewLinkedHashSetEmpty() {
    LinkedHashSet<Integer> set = Sets.newLinkedHashSet();
    verifyLinkedHashSetContents(set, EMPTY_COLLECTION);
  }

  public void testNewLinkedHashSetFromCollection() {
    LinkedHashSet<Integer> set = Sets.newLinkedHashSet(LONGER_LIST);
    verifyLinkedHashSetContents(set, LONGER_LIST);
  }

  public void testNewLinkedHashSetFromIterable() {
    LinkedHashSet<Integer> set = Sets.newLinkedHashSet(new Iterable<Integer>()
    {
      public Iterator<Integer> iterator() {
        return LONGER_LIST.iterator();
      }
    });
    verifyLinkedHashSetContents(set, LONGER_LIST);
  }

  public void testNewTreeSetEmpty() {
    TreeSet<Integer> set = Sets.newTreeSet();
    verifySortedSetContents(set, EMPTY_COLLECTION, null);
  }

  public void testNewTreeSetEmptyDerived() {
    TreeSet<Derived> set = Sets.newTreeSet();
    assertTrue(set.isEmpty());
    set.add(new Derived("foo"));
    set.add(new Derived("bar"));
    JUnitAsserts.assertContentsInOrder(set,
        new Derived("bar"), new Derived("foo"));
  }

  public void testNewTreeSetEmptyNonGeneric() {
    TreeSet<LegacyComparable> set = Sets.newTreeSet();
    assertTrue(set.isEmpty());
    set.add(new LegacyComparable("foo"));
    set.add(new LegacyComparable("bar"));
    JUnitAsserts.assertContentsInOrder(set,
        new LegacyComparable("bar"), new LegacyComparable("foo"));
  }

  public void testNewTreeSetFromCollection() {
    TreeSet<Integer> set = Sets.newTreeSet(SOME_COLLECTION);
    verifySortedSetContents(set, SOME_COLLECTION, null);
  }

  public void testNewTreeSetFromIterable() {
    TreeSet<Integer> set = Sets.newTreeSet(SOME_ITERABLE);
    verifySortedSetContents(set, SOME_ITERABLE, null);
  }

  public void testNewTreeSetFromIterableDerived() {
    Iterable<Derived> iterable =
        Arrays.asList(new Derived("foo"), new Derived("bar"));
    TreeSet<Derived> set = Sets.newTreeSet(iterable);
    JUnitAsserts.assertContentsInOrder(set,
        new Derived("bar"), new Derived("foo"));
  }

  public void testNewTreeSetFromIterableNonGeneric() {
    Iterable<LegacyComparable> iterable =
        Arrays.asList(new LegacyComparable("foo"), new LegacyComparable("bar"));
    TreeSet<LegacyComparable> set = Sets.newTreeSet(iterable);
    JUnitAsserts.assertContentsInOrder(set,
        new LegacyComparable("bar"), new LegacyComparable("foo"));
  }

  public void testNewTreeSetEmptyWithComparator() {
    TreeSet<Integer> set = Sets.newTreeSet(SOME_COMPARATOR);
    verifySortedSetContents(set, EMPTY_COLLECTION, SOME_COMPARATOR);
  }

  public void testComplementOfEnumSet() {
    Set<SomeEnum> units = EnumSet.of(SomeEnum.B, SomeEnum.D);
    EnumSet<SomeEnum> otherUnits = Sets.complementOf(units);
    verifySetContents(otherUnits, EnumSet.of(SomeEnum.A, SomeEnum.C));
  }

  public void testComplementOfEnumSetWithType() {
    Set<SomeEnum> units = EnumSet.of(SomeEnum.B, SomeEnum.D);
    EnumSet<SomeEnum> otherUnits = Sets.complementOf(units, SomeEnum.class);
    verifySetContents(otherUnits, EnumSet.of(SomeEnum.A, SomeEnum.C));
  }

  public void testComplementOfRegularSet() {
    Set<SomeEnum> units = Sets.newHashSet(SomeEnum.B, SomeEnum.D);
    EnumSet<SomeEnum> otherUnits = Sets.complementOf(units);
    verifySetContents(otherUnits, EnumSet.of(SomeEnum.A, SomeEnum.C));
  }

  public void testComplementOfRegularSetWithType() {
    Set<SomeEnum> units = Sets.newHashSet(SomeEnum.B, SomeEnum.D);
    EnumSet<SomeEnum> otherUnits = Sets.complementOf(units, SomeEnum.class);
    verifySetContents(otherUnits, EnumSet.of(SomeEnum.A, SomeEnum.C));
  }

  public void testComplementOfEmptySet() {
    Set<SomeEnum> noUnits = Collections.emptySet();
    EnumSet<SomeEnum> allUnits = Sets.complementOf(noUnits, SomeEnum.class);
    verifySetContents(EnumSet.allOf(SomeEnum.class), allUnits);
  }

  public void testComplementOfFullSet() {
    Set<SomeEnum> allUnits = Sets.newHashSet(SomeEnum.values());
    EnumSet<SomeEnum> noUnits = Sets.complementOf(allUnits, SomeEnum.class);
    verifySetContents(noUnits, EnumSet.noneOf(SomeEnum.class));
  }

  public void testComplementOfEmptyEnumSetWithoutType() {
    Set<SomeEnum> noUnits = EnumSet.noneOf(SomeEnum.class);
    EnumSet<SomeEnum> allUnits = Sets.complementOf(noUnits);
    verifySetContents(allUnits, EnumSet.allOf(SomeEnum.class));
  }

  public void testComplementOfEmptySetWithoutTypeDoesntWork() {
    Set<SomeEnum> set = Collections.emptySet();
    try {
      Sets.complementOf(set);
      fail();
    } catch (IllegalArgumentException expected) {}
  }

  public void testNullPointerExceptions() throws Exception {
    NullPointerTester tester = new NullPointerTester();
    tester.setDefault(Enum.class, SomeEnum.A);

    // TODO: make NPT create empty arrays for defaults automatically
    tester.setDefault(Collection[].class, new Collection[0]);
    tester.setDefault(Enum[].class, new Enum[0]);
    tester.setDefault(Set[].class, new Set[0]);
    tester.testAllPublicStaticMethods(Sets.class);
  }

  public void testNewSetFromMap() {
    Set<Integer> set = Sets.newSetFromMap(new HashMap<Integer, Boolean>());
    set.addAll(SOME_COLLECTION);
    verifySetContents(set, SOME_COLLECTION);
  }

  public void testNewSetFromMapSerialization() {
    Set<Integer> set =
        Sets.newSetFromMap(new LinkedHashMap<Integer, Boolean>());
    set.addAll(SOME_COLLECTION);
    Set<Integer> copy = SerializableTester.reserializeAndAssert(set);
    JUnitAsserts.assertContentsInOrder(copy, 0, 1);
  }

  public void testNewSetFromMapIllegal() {
    Map<Integer, Boolean> map = new LinkedHashMap<Integer, Boolean>();
    map.put(2, true);
    try {
      Sets.newSetFromMap(map);
      fail();
    } catch (IllegalArgumentException expected) {}
  }

  /**
   * Utility method to verify that the given LinkedHashSet is equal to and
   * hashes identically to a set constructed with the elements in the given
   * collection.  Also verifies that the ordering in the set is the same
   * as the ordering of the given contents.
   */
  private static <E> void verifyLinkedHashSetContents(
      LinkedHashSet<E> set, Collection<E> contents) {
    assertEquals("LinkedHashSet should have preserved order for iteration",
        new ArrayList<E>(set), new ArrayList<E>(contents));
    verifySetContents(set, contents);
  }

  /**
   * Utility method to verify that the given SortedSet is equal to and
   * hashes identically to a set constructed with the elements in the
   * given iterable.  Also verifies that the comparator is the same as the
   * given comparator.
   */
  private static <E> void verifySortedSetContents(
      SortedSet<E> set, Iterable<E> iterable,
      @Nullable Comparator<E> comparator) {
    assertSame(comparator, set.comparator());
    verifySetContents(set, iterable);
  }

  /**
   * Utility method that verifies that the given set is equal to and hashes
   * identically to a set constructed with the elements in the given iterable.
   */
  private static <E> void verifySetContents(Set<E> set, Iterable<E> contents) {
    Set<E> expected = null;
    if (contents instanceof Set) {
      expected = (Set<E>) contents;
    } else {
      expected = new HashSet<E>();
      for (E element : contents) {
        expected.add(element);
      }
    }
    assertEquals(expected, set);
  }

  /**
   * Simple base class to verify that we handle generics correctly.
   */
  static class Base implements Comparable<Base>, Serializable {
    private final String s;

    public Base(String s) {
      this.s = s;
    }

    @Override public int hashCode() { // delegate to 's'
      return s.hashCode();
    }

    @Override public boolean equals(Object other) {
      if (other == null) {
        return false;
      } else if (other instanceof Base) {
        return s.equals(((Base) other).s);
      } else {
        return false;
      }
    }

    public int compareTo(Base o) {
      return s.compareTo(o.s);
    }

    private static final long serialVersionUID = 0;
  }

  /**
   * Simple derived class to verify that we handle generics correctly.
   */
  static class Derived extends Base {
    public Derived(String s) {
      super(s);
    }

    private static final long serialVersionUID = 0;
  }

  public void testFilterFiltered() {
    Set<String> unfiltered = Sets.newHashSet();
    Set<String> filtered = Sets.filter(
        Sets.filter(unfiltered, Collections2Test.LENGTH_1),
        Collections2Test.STARTS_WITH_VOWEL);
    unfiltered.add("a");
    unfiltered.add("b");
    unfiltered.add("apple");
    unfiltered.add("banana");
    unfiltered.add("e");
    assertEquals(ImmutableSet.of("a", "e"), filtered);
    assertEquals(ImmutableSet.of("a", "b", "apple", "banana", "e"), unfiltered);

    try {
      filtered.add("d");
      fail();
    } catch (IllegalArgumentException expected) {}
    try {
      filtered.add("egg");
      fail();
    } catch (IllegalArgumentException expected) {}
    assertEquals(ImmutableSet.of("a", "e"), filtered);
    assertEquals(ImmutableSet.of("a", "b", "apple", "banana", "e"), unfiltered);

    filtered.clear();
    assertTrue(filtered.isEmpty());
    assertEquals(ImmutableSet.of("b", "apple", "banana"), unfiltered);
  }
}
