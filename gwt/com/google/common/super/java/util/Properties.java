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

package java.util;

/**
 * Minimal emulation of {@link java.util.Properties}.
 *
 * @author Hayward Chan
 */
public class Properties extends AbstractMap<Object, Object> {
  /*
   * Q: Why use a backing map instead of having the class extends from
   *    HashMap directly?
   * A: This will make the class serializable.  While it works in web mode,
   *    it doesn't work in hosted mode because this implementation conflicts
   *    with the JDK version, and cause VerifyError.  It can be worked around
   *    by providing custom field serializer, but it probably isn't worth
   *    the effort.
   */
  private final Map<Object, Object> values = new HashMap<Object, Object>();

  public Properties() {
  }

  public Enumeration<?> propertyNames() {
    return Collections.enumeration(keySet());
  }

  public String getProperty(String name) {
    return (String) get(name);
  }

  @Override public Object put(Object key, Object value) {
    return values.put(key, value);
  }

  @Override public Set<Entry<Object, Object>> entrySet() {
    return values.entrySet();
  }
}
