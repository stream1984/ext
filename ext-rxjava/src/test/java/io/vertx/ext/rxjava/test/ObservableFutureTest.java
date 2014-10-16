package io.vertx.ext.rxjava.test;

import io.vertx.core.Future;
import io.vertx.ext.rxjava.ObservableFuture;
import io.vertx.ext.rxjava.RxHelper;
import org.junit.Test;
import rx.Subscription;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ObservableFutureTest {

  @Test
  public void testCompleteWithSuccessBeforeSubscribe() {
    ObservableFuture<String> o = RxHelper.observableFuture();
    o.handle(Future.completedFuture("abc"));
    MySubscriber<String> subscriber = new MySubscriber<>();
    o.subscribe(subscriber);
    subscriber.assertItem("abc").assertCompleted().assertEmpty();
  }

  @Test
  public void testCompleteWithSuccessAfterSubscribe() {
    ObservableFuture<String> o = RxHelper.observableFuture();
    MySubscriber<String> subscriber = new MySubscriber<>();
    o.subscribe(subscriber);
    subscriber.assertEmpty();
    o.handle(Future.completedFuture("abc"));
    subscriber.assertItem("abc").assertCompleted().assertEmpty();
  }

  @Test
  public void testCompleteWithFailureBeforeSubscribe() {
    ObservableFuture<String> o = RxHelper.observableFuture();
    Throwable failure = new Throwable();
    o.handle(Future.completedFuture(failure));
    MySubscriber<String> subscriber = new MySubscriber<>();
    o.subscribe(subscriber);
    subscriber.assertError(failure).assertEmpty();
  }

  @Test
  public void testCompleteWithFailureAfterSubscribe() {
    ObservableFuture<String> o = RxHelper.observableFuture();
    MySubscriber<String> subscriber = new MySubscriber<>();
    o.subscribe(subscriber);
    subscriber.assertEmpty();
    Throwable failure = new Throwable();
    o.handle(Future.completedFuture(failure));
    subscriber.assertError(failure).assertEmpty();
  }

  @Test
  public void testUnsubscribeBeforeResolve() {
    ObservableFuture<String> o = RxHelper.observableFuture();
    MySubscriber<String> subscriber = new MySubscriber<>();
    Subscription sub = o.subscribe(subscriber);
    sub.unsubscribe();
    assertTrue(sub.isUnsubscribed());
    subscriber.assertCompleted().assertEmpty();
  }

  @Test
  public void testCompleteTwice() {
    ObservableFuture<String> o = RxHelper.observableFuture();
    MySubscriber<String> subscriber = new MySubscriber<>();
    o.subscribe(subscriber);
    o.handle(Future.completedFuture("abc"));
    o.handle(Future.completedFuture("def"));
    subscriber.assertItem("abc").assertCompleted().assertEmpty();
  }

  @Test
  public void testFailTwice() {
    ObservableFuture<String> o = RxHelper.observableFuture();
    MySubscriber<String> subscriber = new MySubscriber<>();
    o.subscribe(subscriber);
    Throwable failure = new Throwable();
    o.handle(Future.completedFuture(failure));
    o.handle(Future.completedFuture(new Throwable()));
    subscriber.assertError(failure).assertEmpty();
  }
}
