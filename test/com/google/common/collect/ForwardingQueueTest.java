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
import java.util.Queue;

/**
 * Tests for {@code ForwardingQueue}.
 *
 * @author Robert Konigsberg
 */
public class ForwardingQueueTest extends ForwardingTestCase {

  private Queue<String> forward;
  private Queue<String> queue;

  /*
   * Class parameters must be raw, so we can't create a proxy with generic
   * type arguments. The created proxy only records calls and returns null, so
   * the type is irrelevant at runtime.
   */
  @SuppressWarnings("unchecked")
  @Override protected void setUp() throws Exception {
    super.setUp();
    queue = createProxyInstance(Queue.class);
    forward = new ForwardingQueue<String>() {
      @Override protected Queue<String> delegate() {
        return queue;
      }
    };
  }

  public void testAdd_T() {
    forward.add("asdf");
    assertEquals("[add(Object)]", getCalls());
  }

  public void testAddAll_Collection() {
    forward.addAll(Collections.singleton("asdf"));
    assertEquals("[addAll(Collection)]", getCalls());
  }

  public void testClear() {
    forward.clear();
    assertEquals("[clear]", getCalls());
  }

  public void testContains_T() {
    forward.contains("asdf");
    assertEquals("[contains(Object)]", getCalls());
  }

  public void testContainsAll_Collection() {
    forward.containsAll(Collections.singleton("asdf"));
    assertEquals("[containsAll(Collection)]", getCalls());
  }

  public void testElement() {
    forward.element();
    assertEquals("[element]", getCalls());
  }

  public void testIterator() {
    forward.iterator();
    assertEquals("[iterator]", getCalls());
  }

  public void testIsEmpty() {
    forward.isEmpty();
    assertEquals("[isEmpty]", getCalls());
  }

  public void testOffer_T() {
    forward.offer("asdf");
    assertEquals("[offer(Object)]", getCalls());
  }

  public void testPeek() {
    forward.peek();
    assertEquals("[peek]", getCalls());
  }

  public void testPoll() {
    forward.poll();
    assertEquals("[poll]", getCalls());
  }

  public void testRemove() {
    forward.remove();
    assertEquals("[remove]", getCalls());
  }

  public void testRemove_Object() {
    forward.remove(Object.class);
    assertEquals("[remove(Object)]", getCalls());
  }

  public void testRemoveAll_Collection() {
    forward.removeAll(Collections.singleton("asdf"));
    assertEquals("[removeAll(Collection)]", getCalls());
  }

  public void testRetainAll_Collection() {
    forward.retainAll(Collections.singleton("asdf"));
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
}
