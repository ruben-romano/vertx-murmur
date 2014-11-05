var EventBus = (function(vertx) {
	var eventBus = {};

	eb = new vertx.EventBus('http://localhost/eventbus');			
	eb.onopen = function() {
		console.log('Vertx Event Bus is open');
	}

  	eventBus.sendMsg = function(address, msg, handler) {
		eb.send(address, msg, handler);	
	}

	eventBus.registerHandler = function(address, msg, handler) {
		eb.registerHandler(address, msg, handler);
	};

	return eventBus;
}(vertx));