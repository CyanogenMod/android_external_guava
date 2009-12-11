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

import com.google.common.collect.testing.MapInterfaceTest;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Tests for {@link ForwardingSortedMap} using {@link MapInterfaceTest}.
 *
 * @author George van den Driessche
 */
public class ForwardingSortedMapImplementsMapTest
    extends MapInterfaceTest<String, Integer> {

  private static class SimpleForwardingSortedMap<K, V>
      extends ForwardingSortedMap<K, V> {
    final SortedMap<K, V> delegate;
    SimpleForwardingSortedMap(SortedMap<K, V> delegate) {
      this.delegate = delegate;
    }
    @Override protected SortedMap<K, V> delegate() {
      return delegate;
    }
  }

  public ForwardingSortedMapImplementsMapTest() {
    super(true, true, true, true, true);
  }

  @Override protected Map<String, Integer> makeEmptyMap() {
    return new SimpleForwardingSortedMap<String, Integer>(
        new TreeMap<String, Integer>(Ordering.natural().nullsFirst()));
  }

  @Override protected Map<String, Integer> makePopulatedMap() {
    final Map<String, Integer> sortedMap = makeEmptyMap();
    sortedMap.put("one", 1);
    sortedMap.put("two", 2);
    sortedMap.put("three", 3);
    return sortedMap;
  }

  @Override protected String getKeyNotInPopulatedMap()
      throws UnsupportedOperationException {
    return "minus one";
  }

  @Override protected Integer getValueNotInPopulatedMap()
      throws UnsupportedOperationException {
    return -1;
  }
}
