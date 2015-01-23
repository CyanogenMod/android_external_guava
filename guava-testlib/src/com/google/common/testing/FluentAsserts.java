/*
 * Copyright (C) 2012 The Guava Authors
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

package com.google.common.testing;

import static org.truth0.Truth.ASSERT;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.truth0.subjects.CollectionSubject;
import org.truth0.subjects.DefaultSubject;
import org.truth0.subjects.IntegerSubject;
import org.truth0.subjects.IterableSubject;
import org.truth0.subjects.ListSubject;
import org.truth0.subjects.MapSubject;
import org.truth0.subjects.StringSubject;
import org.truth0.subjects.Subject;

/**
 * JDK5 hack for Truth that reduces generics and eases type inference.
 * 
 * @author Louis Wasserman
 */
public class FluentAsserts {
  private FluentAsserts() {}

  // Hack for JDK5 type inference.
  public static <T> CollectionSubject<? extends CollectionSubject<?, T, Collection<T>>, T, Collection<T>> assertThat(
      Collection<T> collection) {
    return ASSERT.<T, Collection<T>>that(collection);
  }

  // Hack for JDK5 type inference.
  public static <T> IterableSubject<? extends IterableSubject<?, T, Iterable<T>>, T, Iterable<T>> assertThat(
      Iterable<T> collection) {
    return ASSERT.<T, Iterable<T>>that(collection);
  }

  // Hack for JDK5 type inference.
  public static <K, V> MapSubject<? extends MapSubject<?, K, V, Map<K, V>>, K, V, Map<K, V>> assertThat(
      Map<K, V> collection) {
    return ASSERT.<K, V, Map<K, V>>that(collection);
  }

  public static <T> ListSubject<? extends ListSubject<?, T, List<T>>, T, List<T>> assertThat(
      T[] array) {
    return ASSERT.<T, List<T>>that(array);
  }

  public static StringSubject assertThat(String string) {
    return ASSERT.that(string);
  }

  public static IntegerSubject assertThat(int x) {
    return ASSERT.that(x);
  }

  public static Subject<DefaultSubject, Object> assertThat(Object object) {
    return ASSERT.that(object);
  }
}
