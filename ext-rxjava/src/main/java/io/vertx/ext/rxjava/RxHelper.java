package io.vertx.ext.rxjava;

import io.vertx.core.Handler;
import io.vertx.core.streams.ReadStream;
import rx.Observable;

/**
 * A set of helpers for RxJava {@link Observable} with Vert.x {@link ReadStream} and
 * {@link io.vertx.core.AsyncResult} handlers.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class RxHelper {


  /**
   * Adapts a Vert.x {@link io.vertx.core.streams.ReadStream} to an RxJava {@link Observable}. After
   * the stream is adapted to an observable, the original stream handlers should not be used anymore
   * as they will be used by the observable adapter.<p>
   *
   * @param stream the stream to adapt
   * @return the adapted observable
   */
  public static <T> Observable<T> toObservable(ReadStream<T> stream) {
    class Foo extends SingleOnSubscribeAdapter<T> implements Handler<T> {
      /** Handle response */
      public void handle(T msg) {
        // Assume stream
        fireNext(msg);
      }

      @Override
      public void execute() {
        stream.handler(this);
        stream.exceptionHandler(this::fireError);
        stream.endHandler(v -> fireComplete());
      }

      @Override
      public void onUnsubscribed() {
        try {
          stream.handler(null);
          stream.exceptionHandler(null);
          stream.endHandler(null);
        }
        catch(Exception e) {
          // Clearing handlers after stream closed causes issues for some (eg AsyncFile) so silently drop errors
        }
      }
    }
    SingleOnSubscribeAdapter<T> rh = new Foo();
    return Observable.create(rh);
  }

  /**
   * Create a new {@link io.vertx.ext.rxjava.ObservableFuture} object: an {@link rx.Observable} implementation
   * implementing {@code Handler&lt;AsyncResult&gt;}. When the async result handler completes, the observable
   * will produce the result and complete immediatly after, when it fails it will signal the error.
   *
   * @return the observable future.
   */
  public static <T> ObservableFuture<T> observableFuture() {
    return new ObservableFuture<T>();
  }
}
