package io.vertx.ext.rxjava.test;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.rxjava.ObservableFuture;
import io.vertx.ext.rxjava.RxHelper;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

import java.util.LinkedList;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class IntegrationTest extends VertxTestBase {

  @Test
  public void testConsumeBodyStream() {
    EventBus eb = vertx.eventBus();
    MessageConsumer<String> consumer = eb.<String>consumer("the-address");
    Observable<String> obs = RxHelper.toObservable(consumer.bodyStream());
    MySubscriber<String> s = new MySubscriber<>();
    obs.subscribe(s);
    eb.send("the-address", "msg1");
    eb.send("the-address", "msg2");
    eb.send("the-address", "msg3");
    s.assertItem("msg1");
    s.assertItem("msg2");
    s.assertItem("msg3");
    s.assertEmpty();
    s.unsubscribe();
    s.assertCompleted();
    s.assertEmpty();
    assertFalse(consumer.isRegistered());
  }

  @Test
  public void testRegisterAgain() {
    EventBus eb = vertx.eventBus();
    MessageConsumer<String> consumer = eb.<String>consumer("the-address");
    Observable<String> obs = RxHelper.toObservable(consumer.bodyStream());
    obs.subscribe(new MySubscriber<>()).unsubscribe();
    MySubscriber<String> s = new MySubscriber<>();
    obs.subscribe(s);
    eb.send("the-address", "msg1");
    s.assertItem("msg1");
    s.assertEmpty();
    s.unsubscribe();
    s.assertCompleted();
    s.assertEmpty();
    assertFalse(consumer.isRegistered());
  }

  @Test
  public void testObservableNetSocket() {
    ObservableFuture<NetServer> onListen = RxHelper.observableFuture();
    onListen.subscribe(
        server -> vertx.createNetClient(new NetClientOptions()).connect(1234, "localhost", ar -> {
          assertTrue(ar.succeeded());
          NetSocket so = ar.result();
          so.write("foo");
          so.close();
        }),
        error -> fail(error.getMessage())
    );
    NetServer server = vertx.createNetServer(new NetServerOptions().setPort(1234).setHost("localhost"));
    Observable<NetSocket> socketObs = RxHelper.toObservable(server.connectStream());
    socketObs.subscribe(new Subscriber<NetSocket>() {
      @Override
      public void onNext(NetSocket o) {
        Observable<Buffer> dataObs = RxHelper.toObservable(o);
        dataObs.subscribe(new Observer<Buffer>() {

          LinkedList<Buffer> buffers = new LinkedList<>();

          @Override
          public void onNext(Buffer buffer) {
            buffers.add(buffer);
          }

          @Override
          public void onError(Throwable e) {
            fail(e.getMessage());
          }

          @Override
          public void onCompleted() {
            assertEquals(1, buffers.size());
            assertEquals("foo", buffers.get(0).toString("UTF-8"));
            server.close();
          }
        });
      }

      @Override
      public void onError(Throwable e) {
        fail(e.getMessage());
      }

      @Override
      public void onCompleted() {
        testComplete();
      }
    });
    server.listen(onListen);
    await();
  }

  @Test
  public void testObservableWebSocket() {
    ObservableFuture<HttpServer> onListen = RxHelper.observableFuture();
    onListen.subscribe(
        server -> vertx.createHttpClient(new HttpClientOptions()).connectWebsocket(1234, "localhost", "/some/path", ws -> {
          ws.write(Buffer.buffer("foo"));
          ws.close();
        }),
        error -> fail(error.getMessage())
    );
    HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(1234).setHost("localhost"));
    Observable<ServerWebSocket> socketObs = RxHelper.toObservable(server.websocketStream());
    socketObs.subscribe(new Subscriber<ServerWebSocket>() {
      @Override
      public void onNext(ServerWebSocket o) {
        Observable<Buffer> dataObs = RxHelper.toObservable(o);
        dataObs.subscribe(new Observer<Buffer>() {

          LinkedList<Buffer> buffers = new LinkedList<>();

          @Override
          public void onNext(Buffer buffer) {
            buffers.add(buffer);
          }

          @Override
          public void onError(Throwable e) {
            fail(e.getMessage());
          }

          @Override
          public void onCompleted() {
            assertEquals(1, buffers.size());
            assertEquals("foo", buffers.get(0).toString("UTF-8"));
            server.close();
          }
        });
      }

      @Override
      public void onError(Throwable e) {
        fail(e.getMessage());
      }

      @Override
      public void onCompleted() {
        testComplete();
      }
    });
    server.listen(onListen);
    await();
  }

  @Test
  public void testObservableHttpRequest() {
    ObservableFuture<HttpServer> onListen = RxHelper.observableFuture();
    onListen.subscribe(
        server -> {
          HttpClientRequest req = vertx.createHttpClient(new HttpClientOptions()).request(HttpMethod.PUT, 1234, "localhost", "/some/path", resp -> {
          });
          req.putHeader("Content-Length", "3");
          req.write("foo");
        },
        error -> fail(error.getMessage())
    );
    HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(1234).setHost("localhost"));
    Observable<HttpServerRequest> socketObs = RxHelper.toObservable(server.requestStream());
    socketObs.subscribe(new Subscriber<HttpServerRequest>() {
      @Override
      public void onNext(HttpServerRequest o) {
        Observable<Buffer> dataObs = RxHelper.toObservable(o);
        dataObs.subscribe(new Observer<Buffer>() {

          LinkedList<Buffer> buffers = new LinkedList<>();

          @Override
          public void onNext(Buffer buffer) {
            buffers.add(buffer);
          }

          @Override
          public void onError(Throwable e) {
            fail(e.getMessage());
          }

          @Override
          public void onCompleted() {
            assertEquals(1, buffers.size());
            assertEquals("foo", buffers.get(0).toString("UTF-8"));
            server.close();
          }
        });
      }

      @Override
      public void onError(Throwable e) {
        fail(e.getMessage());
      }

      @Override
      public void onCompleted() {
        testComplete();
      }
    });
    server.listen(onListen);
    await();
  }
}
