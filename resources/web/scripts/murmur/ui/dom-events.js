/////////////////////
// Modal Initialilze
////////////////////

$('#login-modal')
  .modal('setting', 'closable', false)
  .modal('show');

$('#user-modal') 
	.modal('setting', 'closable', true)
	.modal('hide');

////////////////////
// login modal 
////////////////////

var loginButton = $("#login-button");
var usernameInput = $("#username-input");
var loginUser = function() {
	Global.currentUser = usernameInput.val();
	MessageRepo.loadMessages();				
	$('#login-modal')
		.modal('hide');
	highlightUserButton();
}

usernameInput.keypress(function(e) {
	if(e.which == 13) {	
		loginUser();
	}
});

loginButton.click(function() {
	loginUser();
}); 

////////////////////
// chat input
////////////////////

var chatInput = $('#chat-input');
chatInput.keypress(function(e) {
    if(e.which == 13) {			
    	var message = chatInput.val();    	
        chatInput.val("");					
		if (message.trim() == "") {
			return;
		}		
		MessageRepo.sendMessage(message);
    }
});		

////////////////////
// chat box
////////////////////

var bounceChatBox = function() {
	$('#chat-box-content') 
		.transition('bounce');
}

////////////////////
// user select button and modal
////////////////////

var highlightUserButton = function() {
	for (i=0; i<2; i++) {
		$('#user-button')
  			.transition('shake');
  	}
};

var userButton = $('#user-button');
userButton.click(function() {	
	$('#user-modal') 
		.modal('show');
});

var hideUserSelectModal = function() {
	$('#user-modal') 
      	.modal('hide');
};


