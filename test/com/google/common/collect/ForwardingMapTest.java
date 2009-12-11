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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for {@link ForwardingMap}.
 *
 * @author Hayward Chan
 */
public class ForwardingMapTest extends ForwardingTestCase {

  private static final Collection<String> EMPTY_COLLECTION =
      Collections.emptyList();

  private Map<String, Boolean> forward;

  @Override public void setUp() throws Exception {
    super.setUp();
    /*
     * Class parameters must be raw, so we can't create a proxy with generic
     * type arguments. The created proxy only records calls and returns null, so
     * the type is irrelevant at runtime.
     */
    @SuppressWarnings("unchecked")
    final Map<String, Boolean> map = createProxyInstance(Map.class);
    forward = new ForwardingMap<String, Boolean>() {
      @Override protected Map<String, Boolean> delegate() {
        return map;
      }
    };
  }

  public void testSize() {
    forward.size();
    assertEquals("[size]", getCalls());
  }

  public void testIsEmpty() {
    forward.isEmpty();
    assertEquals("[isEmpty]", getCalls());
  }

  public void testRemove() {
    forward.remove(null);
    assertEquals("[remove(Object)]", getCalls());
  }

  public void testClear() {
    forward.clear();
    assertEquals("[clear]", getCalls());
  }

  public void testContainsKey() {
    forward.containsKey("asdf");
    assertEquals("[containsKey(Object)]", getCalls());
  }

  public void testContainsValue() {
    forward.containsValue(false);
    assertEquals("[containsValue(Object)]", getCalls());
  }

  public void testGet_Object() {
    forward.get("asdf");
    assertEquals("[get(Object)]", getCalls());
  }

  public void testPut_Key_Value() {
    forward.put("key", false);
    assertEquals("[put(Object,Object)]", getCalls());
  }

  public void testPutAll_Map() {
    forward.putAll(new HashMap<String, Boolean>());
    assertEquals("[putAll(Map)]", getCalls());
  }

  public void testKeySet() {
    forward.keySet();
    assertEquals("[keySet]", getCalls());
  }

  public void testValues() {
    forward.values();
    assertEquals("[values]", getCalls());
  }

  public void testEntrySet() {
    forward.entrySet();
    assertEquals("[entrySet]", getCalls());
  }
  
  public void testToString() {
    forward.toString();
    assertEquals("[toString]", getCalls());
  }

  public void testEquals_Object() {
    forward.equals("asdf");
    assertEquals("[equals(Object)]", getCalls());
  }

  public void testHashCode() {
    forward.hashCode();
    assertEquals("[hashCode]", getCalls());
  }
}
