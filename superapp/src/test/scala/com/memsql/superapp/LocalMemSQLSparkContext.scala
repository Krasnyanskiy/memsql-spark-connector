/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//NOTE: modified to work with MemSQLSparkContext
package com.memsql.superapp

import com.memsql.spark.context.MemSQLSparkContext
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Suite

/** Manages a local `sc` {@link MemSQLSparkContext} variable, correctly stopping it after each test. */
trait LocalMemSQLSparkContext extends BeforeAndAfterEach with BeforeAndAfterAll { self: Suite =>

  @transient var sc: MemSQLSparkContext = _

  override def beforeAll() {
    super.beforeAll()
  }

  override def afterEach() {
    resetMemSQLSparkContext()
    super.afterEach()
  }

  def resetMemSQLSparkContext(): Unit = {
    LocalMemSQLSparkContext.stop(sc)
    sc = null
  }

}

object LocalMemSQLSparkContext {
  def stop(sc: MemSQLSparkContext) {
    if (sc != null) {
      sc.stop()
    }
    // To avoid Akka rebinding to the same port, since it doesn't unbind immediately on shutdown
    System.clearProperty("spark.driver.port")
  }

  /** Runs `f` by passing in `sc` and ensures that `sc` is stopped. */
  def withSpark[T](sc: MemSQLSparkContext)(f: MemSQLSparkContext => T): T = {
    try {
      f(sc)
    } finally {
      stop(sc)
    }
  }

}

