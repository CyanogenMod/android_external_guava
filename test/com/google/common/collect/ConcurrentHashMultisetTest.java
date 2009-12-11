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

import static com.google.common.testutils.SerializableTester.reserializeAndAssert;

import junit.framework.TestCase;

import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

/**
 * Test case for {@link ConcurrentHashMultiset}.
 *
 * @author Cliff L. Biffle
 */
public class ConcurrentHashMultisetTest extends TestCase {
  private static final String KEY = "puppies";

  ConcurrentMap<String, Integer> backingMap;
  ConcurrentHashMultiset<String> multiset;

  @SuppressWarnings("unchecked")
  @Override protected void setUp() {
    backingMap = EasyMock.createMock(ConcurrentMap.class);
    expect(backingMap.isEmpty()).andReturn(true);
    replay();

    multiset = new ConcurrentHashMultiset<String>(backingMap);
    verify();
    reset();
  }

  public void testCount_elementPresent() {
    final int COUNT = 12;
    expect(backingMap.get(KEY)).andReturn(COUNT);
    replay();

    assertEquals(COUNT, multiset.count(KEY));
    verify();
  }

  public void testCount_elementAbsent() {
    expect(backingMap.get(KEY)).andReturn(null);
    replay();

    assertEquals(0, multiset.count(KEY));
    verify();
  }

  public void testAdd_zero() {
    final int INITIAL_COUNT = 32;

    expect(backingMap.get(KEY)).andReturn(INITIAL_COUNT);
    replay();
    assertEquals(INITIAL_COUNT, multiset.add(KEY, 0));
    verify();
  }

  public void testAdd_firstFewWithSuccess() {
    final int COUNT = 400;

    expect(backingMap.get(KEY)).andReturn(null);
    expect(backingMap.putIfAbsent(KEY, COUNT)).andReturn(null);
    replay();

    assertEquals(0, multiset.add(KEY, COUNT));
    verify();
  }

  public void testAdd_laterFewWithSuccess() {
    final int INITIAL_COUNT = 32;
    final int COUNT_TO_ADD = 400;

    expect(backingMap.get(KEY)).andReturn(INITIAL_COUNT);
    expect(backingMap.replace(KEY, INITIAL_COUNT, INITIAL_COUNT + COUNT_TO_ADD))
        .andReturn(true);
    replay();

    assertEquals(INITIAL_COUNT, multiset.add(KEY, COUNT_TO_ADD));
    verify();
  }

  public void testAdd_laterFewWithOverflow() {
    final int INITIAL_COUNT = 92384930;
    final int COUNT_TO_ADD = Integer.MAX_VALUE - INITIAL_COUNT + 1;

    expect(backingMap.get(KEY)).andReturn(INITIAL_COUNT);
    replay();

    try {
      multiset.add(KEY, COUNT_TO_ADD);
      fail("Must reject arguments that would cause counter overflow.");
    } catch (IllegalArgumentException e) {
      // Expected.
    }
    verify();
  }

  /**
   * Simulates rapid concurrent writes to the multiset to test failure, retry,
   * and compare-and-set operations.
   *
   * Specifically, the multiset will initially see {@code null} for the element
   * count, giving it free reign to insert -- but the {@code putIfAbsent} will
   * fail due to a concurrent write.  The multiset will then fall back four
   * times as counts go up and down before succeeding.
   */
  public void testAdd_fewWithFailures() {
    final int DESIRED_COUNT = 400;
    final Integer[] FAILURE_COUNTS = { null, 12, 40, null, 80 };
    final int LAST_FAILURE_COUNT = FAILURE_COUNTS[FAILURE_COUNTS.length - 1];

    for (Integer failureCount : FAILURE_COUNTS) {
      // Check current contents...
      expect(backingMap.get(KEY)).andReturn(failureCount);

      if (failureCount == null) {
        /*
         * TODO: this only works because we know the multiset
         * doesn't use the result of putIfAbsent.
         */
        expect(backingMap.putIfAbsent(KEY, DESIRED_COUNT))
            .andReturn(12);
      } else {
        int nextCount = failureCount + DESIRED_COUNT;
        expect(backingMap.replace(KEY, failureCount, nextCount))
            .andReturn(false); // ...and lose!
      }
    }

    // Last time.
    expect(backingMap.get(KEY)).andReturn(LAST_FAILURE_COUNT);
    expect(backingMap.replace(KEY, LAST_FAILURE_COUNT,
        LAST_FAILURE_COUNT + DESIRED_COUNT))
        .andReturn(true); // Yay!
    replay();

    assertEquals(LAST_FAILURE_COUNT, multiset.add(KEY, DESIRED_COUNT));
    verify();
  }

  public void testRemove_zeroFromSome() {
    final int INITIAL_COUNT = 14;
    expect(backingMap.get(KEY)).andReturn(INITIAL_COUNT);
    replay();

    assertEquals(INITIAL_COUNT, multiset.remove(KEY, 0));
    verify();
  }

  public void testRemove_zeroFromNone() {
    expect(backingMap.get(KEY)).andReturn(null);
    replay();

    assertEquals(0, multiset.remove(KEY, 0));
    verify();
  }

  public void testRemove_nonePresent() {
    expect(backingMap.get(KEY)).andReturn(null);
    replay();

    assertEquals(0, multiset.remove(KEY, 400));
    verify();
  }

  public void testRemove_someRemaining() {
    final int COUNT_TO_REMOVE = 30;
    final int COUNT_REMAINING = 1;
    final int INITIAL_COUNT = COUNT_TO_REMOVE + COUNT_REMAINING;
    expect(backingMap.get(KEY)).andReturn(INITIAL_COUNT);
    expect(backingMap.replace(KEY, INITIAL_COUNT, COUNT_REMAINING))
        .andReturn(true);
    replay();

    assertEquals(INITIAL_COUNT, multiset.remove(KEY, COUNT_TO_REMOVE));
    verify();
  }

  public void testRemove_noneRemaining() {
    final int COUNT_TO_REMOVE = 30;
    final int INITIAL_COUNT = COUNT_TO_REMOVE;
    expect(backingMap.get(KEY)).andReturn(INITIAL_COUNT);
    expect(backingMap.remove(KEY, INITIAL_COUNT))
        .andReturn(true);
    replay();

    assertEquals(INITIAL_COUNT, multiset.remove(KEY, COUNT_TO_REMOVE));
    verify();
  }

  public void testRemove_someFailuresThenComplete() {
    final int COUNT_TO_REMOVE = 30;
    final int[] FAILURES = {
        COUNT_TO_REMOVE + 12,
        COUNT_TO_REMOVE - 8, // to test remove behavior
        COUNT_TO_REMOVE + 4,
        COUNT_TO_REMOVE
    };
    final int COUNT_AT_SUCCESS = COUNT_TO_REMOVE;

    simulateRemoveFailures(COUNT_TO_REMOVE, FAILURES, COUNT_AT_SUCCESS);
    replay();

    assertEquals(COUNT_AT_SUCCESS, multiset.remove(KEY, COUNT_TO_REMOVE));
    verify();
  }

  public void testRemove_someFailuresThenPartial() {
    final int COUNT_TO_REMOVE = 30;
    final int[] FAILURES = {
        COUNT_TO_REMOVE + 12,
    };
    final int COUNT_AT_SUCCESS = COUNT_TO_REMOVE - 8;

    simulateRemoveFailures(COUNT_TO_REMOVE, FAILURES, COUNT_AT_SUCCESS);
    replay();

    assertEquals(COUNT_AT_SUCCESS, multiset.remove(KEY, COUNT_TO_REMOVE));
    verify();
  }

  public void testRemove_someFailuresThenNull() {
    final int COUNT_TO_REMOVE = 30;
    final int[] FAILURES = {
        COUNT_TO_REMOVE + 12,
    };
    final Integer COUNT_AT_SUCCESS = null;

    simulateRemoveFailures(COUNT_TO_REMOVE, FAILURES, COUNT_AT_SUCCESS);
    replay();

    assertEquals(0, multiset.remove(KEY, COUNT_TO_REMOVE));
    verify();
  }

  private void simulateRemoveFailures(int countToRemove, int[] failures,
      @Nullable Integer finalCount) {
    for (int count : failures) {
      expect(backingMap.get(KEY)).andReturn(count);
      if (count > countToRemove) {
        expect(backingMap.replace(KEY, count, count - countToRemove))
            .andReturn(false);
      } else {
        expect(backingMap.remove(KEY, count)).andReturn(false);
      }
    }

    expect(backingMap.get(KEY)).andReturn(finalCount);
    if (finalCount == null) {
      return;
    } else if (finalCount > countToRemove) {
      expect(backingMap.replace(KEY, finalCount, finalCount - countToRemove))
          .andReturn(true);
    } else {
      expect(backingMap.remove(KEY, finalCount)).andReturn(true);
    }
  }

  public void testIteratorRemove_actualMap() {
    // Override to avoid using mocks.
    multiset = ConcurrentHashMultiset.create();

    multiset.add(KEY);
    multiset.add(KEY + "_2");
    multiset.add(KEY);

    int mutations = 0;
    for (Iterator<String> it = multiset.iterator(); it.hasNext(); ) {
      it.next();
      it.remove();
      mutations++;
    }
    assertTrue(multiset.isEmpty());
    assertEquals(3, mutations);
  }

  public void testSetCount_basic() {
    final int INITIAL_COUNT = 20;
    final int COUNT_TO_SET = 40;

    expect(backingMap.put(KEY, COUNT_TO_SET))
        .andReturn(INITIAL_COUNT);
    replay();

    assertEquals(INITIAL_COUNT, multiset.setCount(KEY, COUNT_TO_SET));
    verify();
  }

  public void testSetCount_asRemove() {
    final int COUNT_TO_REMOVE = 40;
    expect(backingMap.remove(KEY)).andReturn(COUNT_TO_REMOVE);
    replay();

    assertEquals(COUNT_TO_REMOVE, multiset.setCount(KEY, 0));
    verify();
  }

  public void testSetCount_0_nonePresent() {
    expect(backingMap.remove(KEY)).andReturn(null);
    replay();

    assertEquals(0, multiset.setCount(KEY, 0));
    verify();
  }

  public void testSetCount_0_success() {
    final int COUNT_TO_REMOVE = 12;
    expect(backingMap.remove(KEY)).andReturn(COUNT_TO_REMOVE);
    replay();

    assertEquals(COUNT_TO_REMOVE, multiset.setCount(KEY, 0));
    verify();
  }

  public void testCreate() {
    ConcurrentHashMultiset<Integer> multiset = ConcurrentHashMultiset.create();
    assertTrue(multiset.isEmpty());
    reserializeAndAssert(multiset);
  }

  public void testCreateFromIterable() {
    Iterable<Integer> iterable = Arrays.asList(1, 2, 2, 3, 4);
    ConcurrentHashMultiset<Integer> multiset
        = ConcurrentHashMultiset.create(iterable);
    assertEquals(2, multiset.count(2));
    reserializeAndAssert(multiset);
  }

  private void replay() {
    EasyMock.replay(backingMap);
  }

  private void verify() {
    EasyMock.verify(backingMap);
  }

  private void reset() {
    EasyMock.reset(backingMap);
  }
}
