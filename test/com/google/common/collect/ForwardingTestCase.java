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

import com.google.common.base.Function;
import com.google.common.base.Joiner;

import junit.framework.TestCase;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Base test case for testing the variety of forwarding classes.
 *
 * @author Robert Konigsberg
 */
public abstract class ForwardingTestCase extends TestCase {

  private List<String> calls = new ArrayList<String>();

  private void called(String id) {
    calls.add(id);
  }

  protected String getCalls() {
    return calls.toString();
  }

  protected boolean isCalled() {
    return !calls.isEmpty();
  }

  @SuppressWarnings("unchecked")
  protected <T> T createProxyInstance(Class<T> c) {
    /*
     * This invocation handler only registers that a method was called,
     * and then returns a bogus, but acceptable, value.
     */
    InvocationHandler handler = new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args)
          throws Throwable {
        called(asString(method));

        Class<?> returnType = method.getReturnType();

        if (returnType == boolean.class || returnType == Boolean.class) {
          return Boolean.FALSE;
        }

        if (returnType == int.class || returnType == Integer.class) {
          return -1;
        }

        if ((returnType == Set.class) || (returnType == Collection.class)) {
          return Collections.emptySet();
        }

        return null;
      }
    };

    return (T) Proxy.newProxyInstance(c.getClassLoader(),
        new Class[] { c }, handler);
  }

  private static final Joiner COMMA_JOINER = Joiner.on(",");

  /*
   * Returns string representation of a method.
   *
   * If the method takes no parameters, it returns the name (e.g.
   * "isEmpty". If the method takes parameters, it returns the simple names
   * of the parameters (e.g. "put(Object,Object)".)
   */
  private String asString(Method method) {
    String methodName = method.getName();
    Class<?>[] parameterTypes = method.getParameterTypes();

    if (parameterTypes.length == 0) {
      return methodName;
    }

    Iterable<String> parameterNames = Iterables.transform(
        Arrays.asList(parameterTypes),
        new Function<Class<?>, String>() {
          public String apply(Class<?> from) {
            return from.getSimpleName();
          }
    });
    return methodName + "(" + COMMA_JOINER.join(parameterNames) + ")";
  }
}
