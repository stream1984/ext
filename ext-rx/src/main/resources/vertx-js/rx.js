
// Needed by rx.js to work, need go figure out how it is needed and implement it there or in vertx-js
if (typeof setTimeout === 'undefined') {
  setTimeout = function() { throw 'not yet implemented' };
}
var Rx = require("rx");
var utils = require('vertx-js/util/utils');

Rx.toObservable = function() {
  if (arguments.length === 1 && typeof arguments[0] === 'object') {
    var readStream = arguments[0];
      var subscribed = false;
      return Rx.Observable.create(function(observer) {
        if (subscribed) {
          throw new Error('ReadStream observable support only a single subscription');
        }
        subscribed = true;
        readStream.exceptionHandler(function(err) { observer.onError(err); });
        readStream.endHandler(function() { observer.onCompleted(); });
        readStream.handler(function(event) { observer.onNext(event); });
        return function() {
          readStream._jdel().handler(null); // This may call the endHandler
          readStream._jdel().endHandler(null);
          readStream._jdel().exceptionHandler(null);
        }
      })
  } else {
    utils.invalidArgs();
  }
};

Rx.toHandler = function() {
  if (arguments.length === 1 && typeof arguments[0] === 'object') {
    var observer = arguments[0];
    return function(result, cause) {
      if (result != null) {
        observer.onNext(result);
        observer.onCompleted();
      } else {
        observer.onError(cause);
      }
    };
  } else {
    utils.invalidArgs();
  }
};

Rx.observableHandler = function() {
  var subject = new Rx.Subject();
  subject.asHandler = function() {
    return Rx.toHandler(subject);
  };
  return subject;
};

module.exports = Rx;