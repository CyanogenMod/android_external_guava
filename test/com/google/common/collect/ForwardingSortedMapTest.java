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

import java.util.Collections;
import java.util.SortedMap;

/**
 * Tests for {@code ForwardingSortedMap}.
 *
 * @author Robert Konigsberg
 */
public class ForwardingSortedMapTest extends ForwardingTestCase {

  private SortedMap<String, String> forward;

  @Override public void setUp() throws Exception {
    super.setUp();
    /*
     * Class parameters must be raw, so we can't create a proxy with generic
     * type arguments. The created proxy only records calls and returns null, so
     * the type is irrelevant at runtime.
     */
    @SuppressWarnings("unchecked")
    final SortedMap<String, String> sortedMap
        = createProxyInstance(SortedMap.class);
    forward = new ForwardingSortedMap<String, String>() {
      @Override protected SortedMap<String, String> delegate() {
        return sortedMap;
      }
    };
  }

  public void testComparator() {
    forward.comparator();
    assertEquals("[comparator]", getCalls());
  }

  public void testFirstKey() {
    forward.firstKey();
    assertEquals("[firstKey]", getCalls());
  }

  public void testHeadMap_K() {
    forward.headMap("asdf");
    assertEquals("[headMap(Object)]", getCalls());
  }

  public void testLastKey() {
    forward.lastKey();
    assertEquals("[lastKey]", getCalls());
  }

  public void testSubMap_K_K() {
    forward.subMap("first", "last");
    assertEquals("[subMap(Object,Object)]", getCalls());
  }

  public void testTailMap_K() {
    forward.tailMap("last");
    assertEquals("[tailMap(Object)]", getCalls());
  }

  public void testClear() {
    forward.clear();
    assertEquals("[clear]", getCalls());
  }

  public void testContainsKey_Object() {
    forward.containsKey(Object.class);
    assertEquals("[containsKey(Object)]", getCalls());
  }

  public void testContainsValue_Object() {
    forward.containsValue(Object.class);
    assertEquals("[containsValue(Object)]", getCalls());
  }

  public void testEntrySet() {
    forward.entrySet();
    assertEquals("[entrySet]", getCalls());
  }

  public void testGet_Object() {
    forward.get(Object.class);
    assertEquals("[get(Object)]", getCalls());
  }

  public void testIsEmpty() {
    forward.isEmpty();
    assertEquals("[isEmpty]", getCalls());
  }

  public void testKeySet() {
    forward.keySet();
    assertEquals("[keySet]", getCalls());
  }

  public void testPut_K_V() {
    forward.put("foo", "bar");
    assertEquals("[put(Object,Object)]", getCalls());
  }

  public void testPutAll_Map() {
    forward.putAll(Collections.<String, String>emptyMap());
    assertEquals("[putAll(Map)]", getCalls());
  }

  public void testRemove_Object() {
    forward.remove(Object.class);
    assertEquals("[remove(Object)]", getCalls());
  }

  public void testSize() {
    forward.size();
    assertEquals("[size]", getCalls());
  }

  public void testValues() {
    forward.values();
    assertEquals("[values]", getCalls());
  }
      
  public void testToString() {
    forward.toString();
    assertEquals("[toString]", getCalls());
  }
}
