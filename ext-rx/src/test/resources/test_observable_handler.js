var test = require("test");
var Rx = require("vertx-js/rx");
var eb = vertx.eventBus();
eb.consumer("the_address").handler(function(msg) {
  msg.reply("pong");
});
var observable = Rx.observableHandler();
var events = [];
observable.subscribe(
  function(evt) {
    events.push(evt.body());
  }, function(err) {
    test.fail();
  }, function() {
    test.assertEquals(1, events.length);
    test.assertEquals("pong", events[0]);
    test.testComplete();
  }
);
eb.send("the_address", {}, {}, observable.asHandler());
