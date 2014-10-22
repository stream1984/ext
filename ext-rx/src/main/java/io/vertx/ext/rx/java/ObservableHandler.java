package io.vertx.ext.rx.java;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ObservableHandler<T> extends Observable<T> {

  private static class Adapter<T> extends SingleOnSubscribeAdapter<T> implements Handler<AsyncResult<T>> {

    private AsyncResult<T> buffered;
    private boolean subscribed;

    @Override
    public void execute() {
      AsyncResult<T> result = buffered;
      if (result != null) {
        buffered = null;
        dispatch(result, this);
      } else {
        subscribed = true;
      }
    }

    @Override
    public void handle(AsyncResult<T> event) {
      if (subscribed) {
        subscribed = false;
        dispatch(event, this);
      } else {
        this.buffered = event;
      }
    }

    @Override
    protected void onUnsubscribed() {
      subscribed = false;
    }

    private static <T> void dispatch(AsyncResult<T> ar, Adapter<T> adapter) {
      if (ar.succeeded()) {
        adapter.fireNext(ar.result());
        adapter.fireComplete();
      } else {
        adapter.fireError(ar.cause());
      }
    }
  }

  public ObservableHandler() {
    this(new Adapter<>());
  }

  private Adapter<T> adapter;

  private ObservableHandler(Adapter<T> adapter) {
    super(adapter);
    this.adapter = adapter;
  }

  public Handler<AsyncResult<T>> asHandler() {
    return adapter;
  }
}
