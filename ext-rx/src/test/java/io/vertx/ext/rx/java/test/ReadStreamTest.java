package io.vertx.ext.rx.java.test;

import io.vertx.ext.rx.java.RxHelper;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ReadStreamTest {

  @Test
  public void testReact() {
    SimpleReadStream<String> rs = new SimpleReadStream<>();
    Observable<String> o = RxHelper.toObservable(rs);
    MySubscriber<String> subscriber = new MySubscriber<>();
    Subscription sub = o.subscribe(subscriber);
    assertNotNull(rs.endHandler);
    assertNotNull(rs.endHandler);
    assertNotNull(rs.handler);
    rs.handler.handle("foo");
    subscriber.assertItem("foo").assertEmpty();
    rs.handler.handle("bar");
    subscriber.assertItem("bar").assertEmpty();
    rs.endHandler.handle(null);
    subscriber.assertCompleted().assertEmpty();
    assertTrue(sub.isUnsubscribed());
  }

}
