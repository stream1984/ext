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

package io.vertx.ext.webres.impl;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.ext.webres.WebResources;
import io.vertx.core.Vertx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class WebResourcesImpl implements WebResources {

  private final String webRoot;

  private File fileRoot;
  private File webFileRoot;

  private ClassLoader cl;

  private Vertx vertx;

  private boolean initialised;

  public WebResourcesImpl(Vertx vertx, String webRoot) {
    this.vertx = vertx;
    this.webRoot = webRoot;
    cl = Thread.currentThread().getContextClassLoader();
  }

  public void initialise(Handler<AsyncResult<Void>> resultHandler) {
    vertx.deployVerticle(new Worker(), new DeploymentOptions().setWorker(true), res -> {
      if (res.succeeded()) {
        vertx.undeployVerticle(res.result(), res2 -> {
          if (res2.succeeded()) {
            resultHandler.handle(Future.completedFuture());
          } else {
            resultHandler.handle(Future.completedFuture(res2.cause()));
          }
        });
      } else {
        resultHandler.handle(Future.completedFuture(res.cause()));
      }
    });
  }

  // FIXME - I am not happy with having to use a worker verticle for this
  // much better if we could run arbitrary blocking code with vert.x
  private class Worker extends AbstractVerticle {

    public void start() {
      fileRoot = new File(System.getProperty("user.home"), ".vertx.webres");
      fileRoot.mkdirs();
      traverseDir(webRoot);
      webFileRoot = new File(fileRoot, webRoot);
      initialised = true;
    }
  }

  public String resolveResource(String resourceName) {
    if (!initialised) {
      throw new IllegalStateException("Not initialised");
    }
    // TODO check that resolved file is not outside webroot (e.g. ..)
    return new File(webFileRoot, resourceName).getAbsolutePath();
  }

  private void traverseDir(String dirName) {
    File dir = new File(fileRoot, dirName);
    if (dir.exists()) {
      vertx.fileSystem().deleteSyncRecursive(dir.getAbsolutePath(), true);
    }
    dir.mkdirs();
    try (InputStream is = cl.getResourceAsStream(dirName)) {
      BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
      String line;
      while ((line = rdr.readLine()) != null) {
        if (isDirectory(line)) {
          traverseDir(dirName + "/" + line);
        } else {
          saveResourceToDisk(dirName + "/" + line);
        }
      }
    } catch (IOException e) {
      throw new VertxException(e);
    }
  }

  private void saveResourceToDisk(String resource) {
    try (InputStream is = cl.getResourceAsStream(resource)) {
      File file = new File(fileRoot, resource);
      Path target = file.toPath();
      Files.copy(is, target);
    } catch (IOException e) {
      throw new VertxException(e);
    }
  }

  private boolean isDirectory(String dirName) {
    // TODO could improve this
    return dirName.indexOf('.') == -1;
  }
}
