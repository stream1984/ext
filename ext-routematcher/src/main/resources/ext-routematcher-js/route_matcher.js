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

var utils = require('vertx-js/util/utils');
var HttpServerRequest = require('vertx-js/http_server_request');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JRouteMatcher = io.vertx.ext.routematcher.RouteMatcher;

/**

  @class
*/
var RouteMatcher = function(j_val) {

  var j_routeMatcher = j_val;
  var that = this;

  this.accept = function(request) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'object' && __args[0]._vertxgen) {
      j_routeMatcher.accept(request._jdel());
      return that;
    } else utils.invalidArgs();
  };

  /*
   Specify a handler that will be called for a matching request
  */
  this.matchMethod = function(method, pattern, handler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'string' && typeof __args[2] === 'function') {
      j_routeMatcher.matchMethod(io.vertx.core.http.HttpMethod.valueOf(__args[0]), pattern, function(jVal) {
      handler(new HttpServerRequest(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  /*
   Specify a handler that will be called for all HTTP methods
  */
  this.all = function(pattern, handler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_routeMatcher.all(pattern, function(jVal) {
      handler(new HttpServerRequest(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  /*
   Specify a handler that will be called for a matching request
  */
  this.matchMethodWithRegEx = function(method, pattern, handler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'string' && typeof __args[2] === 'function') {
      j_routeMatcher.matchMethodWithRegEx(io.vertx.core.http.HttpMethod.valueOf(__args[0]), pattern, function(jVal) {
      handler(new HttpServerRequest(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  /*
   Specify a handler that will be called for all HTTP methods
  */
  this.allWithRegEx = function(regex, handler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_routeMatcher.allWithRegEx(regex, function(jVal) {
      handler(new HttpServerRequest(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  /*
   Specify a handler that will be called when no other handlers match.
   If this handler is not specified default behaviour is to return a 404
  
  */
  this.noMatch = function(handler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_routeMatcher.noMatch(function(jVal) {
      handler(new HttpServerRequest(jVal));
    });
      return that;
    } else utils.invalidArgs();
  };

  this._vertxgen = true;

  // Get a reference to the underlying Java delegate
  this._jdel = function() {
    return j_routeMatcher;
  }

};

RouteMatcher.routeMatcher = function() {
  var __args = arguments;
  if (__args.length === 0) {
    return new RouteMatcher(JRouteMatcher.routeMatcher());
  } else utils.invalidArgs();
};

// We export the Constructor function
module.exports = RouteMatcher;