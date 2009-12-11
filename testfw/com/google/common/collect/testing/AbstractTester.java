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

package com.google.common.collect.testing;

import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * This abstract base class for testers allows the framework to inject needed
 * information after JUnit constructs the instances.
 *
 * @param <G> the type of the test generator required by this tester. An
 * instance of G should somehow provide an instance of the class under test,
 * plus any other information required to parameterize the test.
 *
 * @author George van den Driessche
 */
public class AbstractTester<G> extends TestCase {
  private G subjectGenerator;
  private String suiteName;

  protected final void init(G subjectGenerator, String suiteName) {
    this.subjectGenerator = subjectGenerator;
    this.suiteName = suiteName;
  }

  public G getSubjectGenerator() {
    return subjectGenerator;
  }

  /**
   * @return the method to be invoked by this test instance
   * @throws NoSuchMethodException if this test's name does not
   * correspond to a method
   */
  public Method getTestMethod() throws NoSuchMethodException {
    return getClass().getMethod(super.getName());
  }

  @Override public String getName() {
    return String.format("%s[%s]", super.getName(), suiteName);
  }
}
