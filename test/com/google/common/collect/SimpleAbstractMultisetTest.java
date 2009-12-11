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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Unit test for {@link AbstractMultiset}.
 *
 * @author Kevin Bourrillion
 */
@SuppressWarnings("serial") // No serialization is used in this test
public class SimpleAbstractMultisetTest extends AbstractMultisetTest {

  @Override protected <E> Multiset<E> create() {
    return new SimpleAbstractMultiset<E>();
  }

  public void testRemoveUnsupported() {
    Multiset<String> multiset = new NoRemoveMultiset<String>();
    multiset.add("a");
    try {
      multiset.remove("a");
      fail();
    } catch (UnsupportedOperationException expected) {}
    assertTrue(multiset.contains("a"));
  }

  private static class NoRemoveMultiset<E> extends AbstractMultiset<E>
      implements Serializable {
    final Map<E, Integer> backingMap = Maps.newHashMap();

    @Override public int add(E element, int occurrences) {
      checkArgument(occurrences >= 0);
      Integer frequency = backingMap.get(element);
      if (frequency == null) {
        frequency = 0;
      }
      if (occurrences == 0) {
        return frequency;
      }
      checkArgument(occurrences <= Integer.MAX_VALUE - frequency);
      backingMap.put(element, frequency + occurrences);
      return frequency;
    }

    @Override public Set<Entry<E>> entrySet() {
      return new AbstractSet<Entry<E>>() {
        @Override public int size() {
          return backingMap.size();
        }

        @Override public Iterator<Multiset.Entry<E>> iterator() {
          final Iterator<Map.Entry<E, Integer>> backingEntries
              = backingMap.entrySet().iterator();
          return new Iterator<Multiset.Entry<E>>() {
            public boolean hasNext() {
              return backingEntries.hasNext();
            }
            public Multiset.Entry<E> next() {
              final Map.Entry<E, Integer> mapEntry = backingEntries.next();
              return new Multisets.AbstractEntry<E>() {
                public E getElement() {
                  return mapEntry.getKey();
                }
                public int getCount() {
                  Integer frequency = backingMap.get(getElement());
                  return (frequency == null) ? 0 : frequency;
                }
              };
            }
            public void remove() {
              backingEntries.remove();
            }
          };
        }
      };
    }
  }

  private static class SimpleAbstractMultiset<E> extends NoRemoveMultiset<E> {
    @SuppressWarnings("unchecked")
    @Override public int remove(Object element, int occurrences) {
      checkArgument(occurrences >= 0);
      Integer count = backingMap.get(element);
      if (count == null) {
        return 0;
      } else if (count > occurrences) {
        backingMap.put((E) element, count - occurrences);
        return count;
      } else {
        return backingMap.remove(element);
      }
    }
  }
}
