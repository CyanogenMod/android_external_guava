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

import com.google.common.collect.testing.google.UnmodifiableCollectionTests;
import com.google.common.testutils.EqualsTester;
import com.google.common.testutils.SerializableTester;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;

/**
 * Tests for {@link ImmutableSetMultimap}.
 *
 * @author Mike Ward
 */
public class ImmutableSetMultimapTest extends TestCase {

  public void testBuilderPutAllIterable() {
    ImmutableSetMultimap.Builder<String, Integer> builder
        = ImmutableSetMultimap.builder();
    builder.putAll("foo", Arrays.asList(1, 2, 3));
    builder.putAll("bar", Arrays.asList(4, 5));
    builder.putAll("foo", Arrays.asList(6, 7));
    Multimap<String, Integer> multimap = builder.build();
    assertEquals(ImmutableSet.of(1, 2, 3, 6, 7), multimap.get("foo"));
    assertEquals(ImmutableSet.of(4, 5), multimap.get("bar"));
    assertEquals(7, multimap.size());
  }

  public void testBuilderPutAllVarargs() {
    ImmutableSetMultimap.Builder<String, Integer> builder
        = ImmutableSetMultimap.builder();
    builder.putAll("foo", 1, 2, 3);
    builder.putAll("bar", 4, 5);
    builder.putAll("foo", 6, 7);
    Multimap<String, Integer> multimap = builder.build();
    assertEquals(ImmutableSet.of(1, 2, 3, 6, 7), multimap.get("foo"));
    assertEquals(ImmutableSet.of(4, 5), multimap.get("bar"));
    assertEquals(7, multimap.size());
  }

  public void testBuilderPutAllMultimap() {
    Multimap<String, Integer> toPut = LinkedListMultimap.create();
    toPut.put("foo", 1);
    toPut.put("bar", 4);
    toPut.put("foo", 2);
    toPut.put("foo", 3);
    Multimap<String, Integer> moreToPut = LinkedListMultimap.create();
    moreToPut.put("foo", 6);
    moreToPut.put("bar", 5);
    moreToPut.put("foo", 7);
    ImmutableSetMultimap.Builder<String, Integer> builder
        = ImmutableSetMultimap.builder();
    builder.putAll(toPut);
    builder.putAll(moreToPut);
    Multimap<String, Integer> multimap = builder.build();
    assertEquals(ImmutableSet.of(1, 2, 3, 6, 7), multimap.get("foo"));
    assertEquals(ImmutableSet.of(4, 5), multimap.get("bar"));
    assertEquals(7, multimap.size());
  }

  public void testBuilderPutAllWithDuplicates() {
    ImmutableSetMultimap.Builder<String, Integer> builder
        = ImmutableSetMultimap.builder();
    builder.putAll("foo", 1, 2, 3);
    builder.putAll("bar", 4, 5);
    builder.putAll("foo", 1, 6, 7);
    ImmutableSetMultimap<String, Integer> multimap = builder.build();
    assertEquals(7, multimap.size());
  }

  public void testBuilderPutWithDuplicates() {
    ImmutableSetMultimap.Builder<String, Integer> builder
        = ImmutableSetMultimap.builder();
    builder.putAll("foo", 1, 2, 3);
    builder.putAll("bar", 4, 5);
    builder.put("foo", 1);
    ImmutableSetMultimap<String, Integer> multimap = builder.build();
    assertEquals(5, multimap.size());
  }

  public void testBuilderPutAllMultimapWithDuplicates() {
    Multimap<String, Integer> toPut = LinkedListMultimap.create();
    toPut.put("foo", 1);
    toPut.put("bar", 4);
    toPut.put("foo", 2);
    toPut.put("foo", 1);
    toPut.put("bar", 5);
    ImmutableSetMultimap.Builder<String, Integer> builder
        = ImmutableSetMultimap.builder();
    builder.putAll(toPut);
    ImmutableSetMultimap<String, Integer> multimap = builder.build();
    assertEquals(4, multimap.size());
  }

  public void testBuilderPutNullKey() {
    Multimap<String, Integer> toPut = LinkedListMultimap.create();
    toPut.put("foo", null);
    ImmutableSetMultimap.Builder<String, Integer> builder
        = ImmutableSetMultimap.builder();
    try {
      builder.put(null, 1);
      fail();
    } catch (NullPointerException expected) {}
    try {
      builder.putAll(null, Arrays.asList(1, 2, 3));
      fail();
    } catch (NullPointerException expected) {}
    try {
      builder.putAll(null, 1, 2, 3);
      fail();
    } catch (NullPointerException expected) {}
    try {
      builder.putAll(toPut);
      fail();
    } catch (NullPointerException expected) {}
  }

  public void testBuilderPutNullValue() {
    Multimap<String, Integer> toPut = LinkedListMultimap.create();
    toPut.put(null, 1);
    ImmutableSetMultimap.Builder<String, Integer> builder
        = ImmutableSetMultimap.builder();
    try {
      builder.put("foo", null);
      fail();
    } catch (NullPointerException expected) {}
    try {
      builder.putAll("foo", Arrays.asList(1, null, 3));
      fail();
    } catch (NullPointerException expected) {}
    try {
      builder.putAll("foo", 4, null, 6);
      fail();
    } catch (NullPointerException expected) {}
    try {
      builder.putAll(toPut);
      fail();
    } catch (NullPointerException expected) {}
  }

  public void testCopyOf() {
    HashMultimap<String, Integer> input = HashMultimap.create();
    input.put("foo", 1);
    input.put("bar", 2);
    input.put("foo", 3);
    Multimap<String, Integer> multimap = ImmutableSetMultimap.copyOf(input);
    assertEquals(multimap, input);
    assertEquals(input, multimap);
  }

  public void testCopyOfWithDuplicates() {
    ArrayListMultimap<Object, Object> input = ArrayListMultimap.create();
    input.put("foo", 1);
    input.put("bar", 2);
    input.put("foo", 3);
    input.put("foo", 1);
    ImmutableSetMultimap<Object, Object> copy
        = ImmutableSetMultimap.copyOf(input);
    assertEquals(3, copy.size());
  }

  public void testCopyOfEmpty() {
    HashMultimap<String, Integer> input = HashMultimap.create();
    Multimap<String, Integer> multimap = ImmutableSetMultimap.copyOf(input);
    assertEquals(multimap, input);
    assertEquals(input, multimap);
  }

  public void testCopyOfImmutableSetMultimap() {
    Multimap<String, Integer> multimap = createMultimap();
    assertSame(multimap, ImmutableSetMultimap.copyOf(multimap));
  }

  public void testCopyOfNullKey() {
    HashMultimap<String, Integer> input = HashMultimap.create();
    input.put(null, 1);
    try {
      ImmutableSetMultimap.copyOf(input);
      fail();
    } catch (NullPointerException expected) {}
  }

  public void testCopyOfNullValue() {
    HashMultimap<String, Integer> input = HashMultimap.create();
    input.putAll("foo", Arrays.asList(1, null, 3));
    try {
      ImmutableSetMultimap.copyOf(input);
      fail();
    } catch (NullPointerException expected) {}
  }

  public void testEmptyMultimapReads() {
    Multimap<String, Integer> multimap = ImmutableSetMultimap.of();
    assertFalse(multimap.containsKey("foo"));
    assertFalse(multimap.containsValue(1));
    assertFalse(multimap.containsEntry("foo", 1));
    assertTrue(multimap.entries().isEmpty());
    assertTrue(multimap.equals(HashMultimap.create()));
    assertEquals(Collections.emptySet(), multimap.get("foo"));
    assertEquals(0, multimap.hashCode());
    assertTrue(multimap.isEmpty());
    assertEquals(HashMultiset.create(), multimap.keys());
    assertEquals(Collections.emptySet(), multimap.keySet());
    assertEquals(0, multimap.size());
    assertTrue(multimap.values().isEmpty());
    assertEquals("{}", multimap.toString());
  }

  public void testEmptyMultimapWrites() {
    Multimap<String, Integer> multimap = ImmutableSetMultimap.of();
    UnmodifiableCollectionTests.assertMultimapIsUnmodifiable(
        multimap, "foo", 1);
  }

  public void testMultimapReads() {
    Multimap<String, Integer> multimap = createMultimap();
    assertTrue(multimap.containsKey("foo"));
    assertFalse(multimap.containsKey("cat"));
    assertTrue(multimap.containsValue(1));
    assertFalse(multimap.containsValue(5));
    assertTrue(multimap.containsEntry("foo", 1));
    assertFalse(multimap.containsEntry("cat", 1));
    assertFalse(multimap.containsEntry("foo", 5));
    assertFalse(multimap.entries().isEmpty());
    assertEquals(3, multimap.size());
    assertFalse(multimap.isEmpty());
    assertEquals("{foo=[1, 3], bar=[2]}", multimap.toString());
  }

  public void testMultimapWrites() {
    Multimap<String, Integer> multimap = createMultimap();
    UnmodifiableCollectionTests.assertMultimapIsUnmodifiable(
        multimap, "bar", 2);
  }

  public void testMultimapEquals() {
    Multimap<String, Integer> multimap = createMultimap();
    Multimap<String, Integer> hashMultimap = HashMultimap.create();
    hashMultimap.putAll("foo", Arrays.asList(1, 3));
    hashMultimap.put("bar", 2);

    new EqualsTester(multimap)
        .addEqualObject(createMultimap())
        .addEqualObject(hashMultimap)
        .addEqualObject(ImmutableSetMultimap.<String, Integer>builder()
            .put("bar", 2).put("foo", 1).put("foo", 3).build())
        .addEqualObject(ImmutableSetMultimap.<String, Integer>builder()
            .put("bar", 2).put("foo", 3).put("foo", 1).build())
        .addNotEqualObject(ImmutableSetMultimap.<String, Integer>builder()
            .put("foo", 2).put("foo", 3).put("foo", 1).build())
        .addNotEqualObject(ImmutableSetMultimap.<String, Integer>builder()
            .put("bar", 2).put("foo", 3).build())
        .testEquals();
  }

  public void testOf() {
    assertMultimapEquals(
        ImmutableSetMultimap.of("one", 1),
        "one", 1);
    assertMultimapEquals(
        ImmutableSetMultimap.of("one", 1, "two", 2),
        "one", 1, "two", 2);
    assertMultimapEquals(
        ImmutableSetMultimap.of("one", 1, "two", 2, "three", 3),
        "one", 1, "two", 2, "three", 3);
    assertMultimapEquals(
        ImmutableSetMultimap.of("one", 1, "two", 2, "three", 3, "four", 4),
        "one", 1, "two", 2, "three", 3, "four", 4);
    assertMultimapEquals(
        ImmutableSetMultimap.of(
            "one", 1, "two", 2, "three", 3, "four", 4, "five", 5),
        "one", 1, "two", 2, "three", 3, "four", 4, "five", 5);
  }

  private static <K, V> void assertMultimapEquals(Multimap<K, V> multimap,
      Object... alternatingKeysAndValues) {
    assertEquals(multimap.size(), alternatingKeysAndValues.length / 2);
    int i = 0;
    for (Entry<K, V> entry : multimap.entries()) {
      assertEquals(alternatingKeysAndValues[i++], entry.getKey());
      assertEquals(alternatingKeysAndValues[i++], entry.getValue());
    }
  }  

  public void testSerialization() {
    Multimap<String, Integer> multimap = createMultimap();
    SerializableTester.reserializeAndAssert(multimap);
    assertEquals(multimap.size(),
        SerializableTester.reserialize(multimap).size());
    SerializableTester.reserializeAndAssert(multimap.get("foo"));
    SerializableTester.reserializeAndAssert(multimap.keySet());
    SerializableTester.reserializeAndAssert(multimap.keys());
    SerializableTester.reserializeAndAssert(multimap.asMap());
    Collection<Integer> valuesCopy
        = SerializableTester.reserialize(multimap.values());
    assertEquals(HashMultiset.create(multimap.values()),
        HashMultiset.create(valuesCopy));
  }

  public void testEmptySerialization() {
    Multimap<String, Integer> multimap = ImmutableSetMultimap.of();
    assertSame(multimap, SerializableTester.reserialize(multimap));
  }

  private ImmutableSetMultimap<String, Integer> createMultimap() {
    return ImmutableSetMultimap.<String, Integer>builder()
        .put("foo", 1).put("bar", 2).put("foo", 3).build();
  }
}
