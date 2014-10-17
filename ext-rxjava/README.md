# RxJava extension for Vert.x

Vert.x module which uses RxJava to add support for Reactive Extensions (RX) using the RxJava library.
This allows VertX developers to use the RxJava type-safe composable API to build VertX verticles.

# Usage

This module provides helpers for adpating Vert.x stream and future constructs to RxJava observables.

## Dealing with a future

In Vert.x future objects are modelled as `Handler<AsyncResult<T>>` construct and occur as last
parameters of asynchronous methods:

```
HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(1234).setHost("localhost"));
ObservableFuture<HttpServer> onListen = RxHelper.observableFuture();
onListen.subscribe(
   server -> {
     // Server is listening
   },
   failure -> {
     // Server could not start
   }
);
server.listen(onListen);
```

The `ObservableFuture<Server>` will get a single `HttpServer` object, if the `listen` operation fails,
the subscriber will be notified with the failure.

## Dealing with read streams

Vert.x provides `ReadStream` objects, the `RxHelper` class provides a static util method for convering
such stream to the equivalent `Observable`.

It can be used wherever ReadStream are used:

### `HttpServer` provides a `ReadStream<WebSocket>` for incoming connections

```
Observable<ServerWebSocket> socketObs = RxHelper.toObservable(server.websocketStream());
socketObs.subscribe(
  socket -> System.out.println("Web socket connect"),
  failure -> System.out.println("Should never be called"),
  () -> { System.out.println("Subscription ended or server closed"); }
```

### `WebSocket` buffer stream:

```
ServerWebSocket ws = ...;
Observable<Buffer> dataObs = RxHelper.toObservable(o);
```

### `EventBus` message stream:

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


### `EventBus` body stream:

```
EventBus eb = vertx.eventBus();
MessageConsumer<String> consumer = eb.<String>consumer("the-address");
Observable<String> obs = RxHelper.toObservable(consumer.bodyStream());
```

