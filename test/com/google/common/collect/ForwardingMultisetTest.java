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

/**
 * Tests for {@link ForwardingMultiset}.
 *
 * @author hhchan@google.com (Hayward Chan)
 */
public class ForwardingMultisetTest extends ForwardingTestCase {

  private static final Collection<String> EMPTY_COLLECTION =
      Collections.emptyList();

  private Multiset<String> forward;

  @Override public void setUp() throws Exception {
    super.setUp();
    /*
     * Class parameters must be raw, so we can't create a proxy with generic
     * type arguments. The created proxy only records calls and returns null, so
     * the type is irrelevant at runtime.
     */
    @SuppressWarnings("unchecked")
    final Multiset<String> multiset = createProxyInstance(Multiset.class);
    forward = new ForwardingMultiset<String>() {
      @Override protected Multiset<String> delegate() {
        return multiset;
      }
    };
  }

  public void testAdd_T() {
    forward.add("asdf");
    assertEquals("[add(Object)]", getCalls());
  }

  public void testAddAll_Collection() {
    forward.addAll(EMPTY_COLLECTION);
    assertEquals("[addAll(Collection)]", getCalls());
  }

  public void testClear() {
    forward.clear();
    assertEquals("[clear]", getCalls());
  }

  public void testContains_Object() {
    forward.contains(null);
    assertEquals("[contains(Object)]", getCalls());
  }

  public void testContainsAll_Collection() {
    forward.containsAll(EMPTY_COLLECTION);
    assertEquals("[containsAll(Collection)]", getCalls());
  }

  public void testIsEmpty() {
    forward.isEmpty();
    assertEquals("[isEmpty]", getCalls());
  }

  public void testIterator() {
    forward.iterator();
    assertEquals("[iterator]", getCalls());
  }

  public void testRemove_Object() {
    forward.remove(null);
    assertEquals("[remove(Object)]", getCalls());
  }

  public void testRemoveAll_Collection() {
    forward.removeAll(EMPTY_COLLECTION);
    assertEquals("[removeAll(Collection)]", getCalls());
  }

  public void testRetainAll_Collection() {
    forward.retainAll(EMPTY_COLLECTION);
    assertEquals("[retainAll(Collection)]", getCalls());
  }

  public void testSize() {
    forward.size();
    assertEquals("[size]", getCalls());
  }

  public void testToArray() {
    forward.toArray();
    assertEquals("[toArray]", getCalls());
  }

  public void testToArray_TArray() {
    forward.toArray(new String[0]);
    assertEquals("[toArray(Object[])]", getCalls());
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

  public void testCount_Object() {
    forward.count(null);
    assertEquals("[count(Object)]", getCalls());
  }

  public void testAdd_Object_int() {
    forward.add("asd", 23);
    assertEquals("[add(Object,int)]", getCalls());
  }

  public void testRemove_Object_int() {
    forward.remove("asd", 23);
    assertEquals("[remove(Object,int)]", getCalls());
  }

  public void testSetCount_Object_int() {
    forward.setCount("asdf", 233);
    assertEquals("[setCount(Object,int)]", getCalls());
  }

  public void testSetCount_Object_oldCount_newCount() {
    forward.setCount("asdf", 4552, 1233);
    assertEquals("[setCount(Object,int,int)]", getCalls());
  }  

  public void testElementSet() {
    forward.elementSet();
    assertEquals("[elementSet]", getCalls());
  }

  public void testEntrySet() {
    forward.entrySet();
    assertEquals("[entrySet]", getCalls());
  }
}
