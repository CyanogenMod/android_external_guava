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

package com.google.common.testutils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Helper class for testing whether a class is serializable.
 *
 * @see java.io.Serializable
 * @author Mike Bostock
 */
public final class SerializableTester {
  private SerializableTester() {}

  /**
   * Serializes and deserializes the specified object.
   *
   * <p>Note that the specified object may not be known by the compiler to be a
   * {@link java.io.Serializable} instance, and is thus declared an
   * {@code Object}. For example, it might be declared as a {@code List}.
   *
   * @return the re-serialized object
   * @throws SerializationException if the specified object was not successfully
   *     serialized or deserialized
   */
  @SuppressWarnings("unchecked")
  public static <T> T reserialize(T object) {
    checkNotNull(object);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try {
      ObjectOutputStream out = new ObjectOutputStream(bytes);
      out.writeObject(object);
      ObjectInputStream in = new ObjectInputStream(
          new ByteArrayInputStream(bytes.toByteArray()));
      return (T) in.readObject();
    } catch (RuntimeException e) {
      throw new SerializationException(e);
    } catch (IOException e) {
      throw new SerializationException(e);
    } catch (ClassNotFoundException e) {
      throw new SerializationException(e);
    }
  }

  /**
   * Serializes and deserializes the specified object and verifies that the
   * re-serialized object is equal to the provided object.
   *
   * <p>Note that the specified object may not be known by the compiler to be a
   * {@link java.io.Serializable} instance, and is thus declared an
   * {@code Object}. For example, it might be declared as a {@code List}.
   *
   * @return the re-serialized object
   * @throws SerializationException if the specified object was not successfully
   *     serialized or deserialized
   * @throws AssertionError if the re-serialized object is not equal to the
   *     original object
   */
  public static <T> T reserializeAndAssert(T object) {
    T copy = reserialize(object);
    if (!copy.equals(object)) {
      throw new AssertionError("The re-serialized object " + copy +
          " does not equal the original object " + object);
    }
    return copy;
  }
}
