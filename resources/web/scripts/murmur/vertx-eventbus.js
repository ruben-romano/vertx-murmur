var currentUser;
var eb;

var handleLogin = function() {
	eb = new vertx.EventBus('http://localhost/eventbus');
	currentUser = $("#username-input").val();
	// Setup publish queue to listen to message and user notification events
	eb.onopen = function() {
	  eb.registerHandler('user.' + currentUser, function(message) {
	    var receiveTxt = $("#receiveTextArea");		        
	    receiveTxt.val(message.message + "\n" + receiveTxt.val());
	    data.unshift(message);
	    updateChatList(data);
	    console.log('received a message: ' + JSON.stringify(message));
	  });
	}

	$('.small.modal')
		.modal('hide');
}

$('.small.modal')
  .modal('setting', 'closable', false)
  .modal('show')
;
