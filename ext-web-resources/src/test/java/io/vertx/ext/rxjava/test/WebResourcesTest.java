/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.rxjava.test;

import io.vertx.ext.webres.WebResources;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import java.io.File;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class WebResourcesTest extends VertxTestBase {

  @Test
  public void testResources() {
    WebResources webres = WebResources.create(vertx, "testroot");
    webres.initialise(res -> {
      assertTrue(res.succeeded());
      String fileName = webres.resolveResource("otherdir/blah.html");
      System.out.println(fileName);
      assertTrue(new File(fileName).exists());
      testComplete();
    });
    await();
  }
}
