/*
 * Copyright (C) 2008 Google Inc.
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

import com.google.gwt.lang.Array;

import java.util.List;
import java.util.Set;

/**
 * Minimal GWT emulation of {@code com.google.common.collect.ObjectArrays}.
 *
 * <p><strong>This .java file should never be consumed by javac.</strong>
 *
 * @author Hayward Chan
 */
class Platform {

  static <T> List<T> subList(List<T> from, int fromIndex, int toIndex) {
    throw new UnsupportedOperationException(
        "List.subList is not supported yet.");
  }
  
  static boolean isInstance(Class<?> clazz, Object obj) {
    throw new UnsupportedOperationException(
        "Class.isInstance is not supported in GWT yet.");
  }

  static <T> T[] clone(T[] array) {
    return (T[]) Array.clone(array);
  }

  static <T> T[] newArray(Class<T> type, int length) {
    throw new UnsupportedOperationException(
        "Platform.newArray is not supported in GWT yet.");
  }

  static <T> T[] newArray(T[] reference, int length) {
    return Array.createFrom(reference, length);
  }
}
