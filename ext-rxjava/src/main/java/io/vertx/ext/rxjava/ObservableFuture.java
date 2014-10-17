package io.vertx.ext.rxjava;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import rx.Observable;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ObservableFuture<T> extends Observable<T> implements Handler<AsyncResult<T>> {

  private static class Bilto<T> extends SingleOnSubscribeAdapter<T> {

    ObservableFuture<T> owner;

    @Override
    public void execute() {
      AsyncResult<T> result = owner.event;
      if (result != null) {
        owner.event = null;
        dispatch(result, this);
      } else {
        owner.bilto = this;
      }
    }

    @Override
    protected void onUnsubscribed() {
      owner.bilto = null;
    }
  }

  public ObservableFuture() {
    this(new Bilto<>());
  }

  private Bilto<T> bilto;
  private AsyncResult<T> event;

  private ObservableFuture(Bilto<T> bilto) {
    super(bilto);
    bilto.owner = this;
  }

  @Override
  public void handle(AsyncResult<T> event) {
    if (bilto != null) {
      Bilto<T> bilto = this.bilto;
      this.bilto = null;
      dispatch(event, bilto);
    } else {
      this.event = event;
    }
  }

  private static <T> void dispatch(AsyncResult<T> ar, Bilto<T> bilto) {
    if (ar != null && bilto != null) {
      if (ar.succeeded()) {
        bilto.fireNext(ar.result());
        bilto.fireComplete();
      } else {
        bilto.fireError(ar.cause());
      }
    }
  }
}
