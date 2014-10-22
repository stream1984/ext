var test = require("test");
var Rx = require("vertx-js/rx");
var eb = vertx.eventBus();
var consumer = eb.localConsumer("the-address");
var observer = Rx.Observer.create(
  function (evt) {
    test.fail(err);
  },
  function (err) {
    test.fail(err);
  },
  function () {
    test.fail(err);
  }
);

var observable = Rx.toObservable(consumer);
var subscription = observable.subscribe(observer);
test.assertTrue(consumer.isRegistered());
subscription.dispose();
test.assertFalse(consumer.isRegistered());
test.testComplete();