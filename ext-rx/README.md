# Rx extension for Vert.x

Vert.x module adding support for Reactive Extensions (Rx) using the Rx libraries.
This allows Vert.x developers to use the Rx type-safe composable API to build Vert.x verticles.

# Usage

This module provides helpers for adpating Vert.x stream and future constructs to Rx observables.

## Dealing with read streams

Vert.x provides readstream objects, the Rx extension for Vert.x provides ways for converting such stream into the equivalent Observable.

### RxJava

Vert.x provides `io.vertx.core.streams.ReadStream` objects, the `RxHelper` class provides a static util method for convering such stream to a `rx.Observable`.

```
ReadStream<T> stream = ...;
Observable<T> observable = RxHelper.toObservable(stream);
```

### RxJS

The Rx module provides a `toObservable` function for converting a Vert.x read stream to an observable:

```
var stream = ...;
var Rx = require("vertx-js/rx");
var observable = Rx.toObservable(stream);
```

## Dealing with a future

In Vert.x future objects are modelled as async result handlers and occur as last parameter of asynchronous methods.

### RxJava

The `io.vertx.ext.rx.java.RxHelper` can create an `io.vertx.ext.rx.java.ObservableHandler`: an `Observable` with a
`asHandler` method returning a `Handler<AsyncResult<T>>` implementation:

```
HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(1234).setHost("localhost"));
ObservableHandler<HttpServer> observable = RxHelper.observableHandler();
observable.subscribe(
  server -> {
    // Server is listening
  },
  failure -> {
    // Server could not start
  }
);
server.listen(observable.asHandler());
```

The `ObservableHandler<Server>` will get a single `HttpServer` object, if the `listen` operation fails,
the subscriber will be notified with the failure.

The helper can also turn an existing `Observer` into an handler:

```
Observer<Server> observer = ...;
Handler<AsyncResult<Server>> o = RxHelper.toHandler(observer);
```

It also works with just actions:

```
Handler<AsyncResult<Server>> o = RxHelper.toHandler(
  server -> {}, // onNext
  cause -> {},  // onError
  () -> {}      // onCompleted
);
```

### RxJS

The `vertx-rx` module provides an augmented Rx module with the `observableHandler` function:

```
var server = vertx.createHttpServer({ "port":1234, "host":"localhost" });
var Rx = require("vertx-js/rx");
var observable = Rx.observableHandler();
observable.subscribe(
  function(server) {
    // Server is listening
  },
  function(err) {
    // Server could not start
  }
);
server.listen(observable.asHandler());
```

Rx can also turn an existing Observer into an handler:

```
var observer = Rx.Observer.create(
  function(item) { ... }, // onNext
  function(err) { ... },  // onError
  function() { ... }      // onCompleted
);
var handler = Rx.toHandler(observer);
```

# Examples

## `HttpServer` provides a `ReadStream<WebSocket>` for incoming connections

```
Observable<ServerWebSocket> socketObs = RxHelper.toObservable(server.websocketStream());
socketObs.subscribe(
  socket -> System.out.println("Web socket connect"),
  failure -> System.out.println("Should never be called"),
  () -> { System.out.println("Subscription ended or server closed"); }
```

## `WebSocket` buffer stream:

```
ServerWebSocket ws = ...;
Observable<Buffer> dataObs = RxHelper.toObservable(o);
```

## `EventBus` message stream:

```
EventBus eb = vertx.eventBus();
MessageConsumer<String> consumer = eb.<String>consumer("the-address");
Observable<Message<String>> obs = RxHelper.toObservable(consumer);
Subscription sub = obs.subscriber( msg -> { // Got message });
```

When the subscriber, unsubcribes, the message consumer will be unregistered automatically:

```
sub.unsucribe(); // Unregisters the stream
```

## `EventBus` body stream:

```
EventBus eb = vertx.eventBus();
MessageConsumer<String> consumer = eb.<String>consumer("the-address");
Observable<String> obs = RxHelper.toObservable(consumer.bodyStream());
```

