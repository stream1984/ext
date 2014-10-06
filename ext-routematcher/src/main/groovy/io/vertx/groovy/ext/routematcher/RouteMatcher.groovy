/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.groovy.ext.routematcher;
import groovy.transform.CompileStatic
import io.vertx.lang.groovy.InternalHelper
import io.vertx.groovy.core.http.HttpServerRequest
import io.vertx.core.Handler
/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
@CompileStatic
public class RouteMatcher {
  final def io.vertx.ext.routematcher.RouteMatcher delegate;
  public RouteMatcher(io.vertx.ext.routematcher.RouteMatcher delegate) {
    this.delegate = delegate;
  }
  public Object getDelegate() {
    return delegate;
  }
  public static RouteMatcher routeMatcher() {
    def ret= RouteMatcher.FACTORY.apply(io.vertx.ext.routematcher.RouteMatcher.routeMatcher());
    return ret;
  }
  public RouteMatcher accept(HttpServerRequest request) {
    this.delegate.accept((io.vertx.core.http.HttpServerRequest)request.getDelegate());
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP GET
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher get(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.get(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP PUT
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher put(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.put(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP POST
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher post(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.post(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP DELETE
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher delete(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.delete(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP OPTIONS
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher options(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.options(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP HEAD
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher head(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.head(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP TRACE
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher trace(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.trace(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP CONNECT
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher connect(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.connect(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP PATCH
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher patch(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.patch(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for all HTTP methods
   * @param pattern The simple pattern
   * @param handler The handler to call
   */
  public RouteMatcher all(String pattern, Handler<HttpServerRequest> handler) {
    this.delegate.all(pattern, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP GET
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher getWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.getWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP PUT
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher putWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.putWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP POST
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher postWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.postWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP DELETE
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher deleteWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.deleteWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP OPTIONS
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher optionsWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.optionsWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP HEAD
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher headWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.headWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP TRACE
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher traceWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.traceWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP CONNECT
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher connectWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.connectWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for a matching HTTP PATCH
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher patchWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.patchWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called for all HTTP methods
   * @param regex A regular expression
   * @param handler The handler to call
   */
  public RouteMatcher allWithRegEx(String regex, Handler<HttpServerRequest> handler) {
    this.delegate.allWithRegEx(regex, new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }
  /**
   * Specify a handler that will be called when no other handlers match.
   * If this handler is not specified default behaviour is to return a 404
   */
  public RouteMatcher noMatch(Handler<HttpServerRequest> handler) {
    this.delegate.noMatch(new Handler<io.vertx.core.http.HttpServerRequest>() {
      public void handle(io.vertx.core.http.HttpServerRequest event) {
        handler.handle(HttpServerRequest.FACTORY.apply(event));
      }
    });
    return this;
  }

  static final java.util.function.Function<io.vertx.ext.routematcher.RouteMatcher, RouteMatcher> FACTORY = io.vertx.lang.groovy.Factories.createFactory() {
    io.vertx.ext.routematcher.RouteMatcher arg -> new RouteMatcher(arg);
  };
}
