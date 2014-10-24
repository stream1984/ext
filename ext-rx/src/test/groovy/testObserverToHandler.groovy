import io.vertx.groovy.core.Vertx
import io.vertx.groovy.core.http.HttpServer
import rx.Observer;

Vertx vertx = Vertx.vertx();
Observer<HttpServer> observer = new Observer<HttpServer>() {
  @Override
  void onCompleted() {
    test.testComplete();
  }

  @Override
  void onError(Throwable e) {
    test.fail(e.message);
  }

  @Override
  void onNext(HttpServer httpServer) {
    // Expected
  }
}
vertx.createHttpServer(port: 8080).requestHandler({ req -> }).listen(observer.toHandler());
test.await();