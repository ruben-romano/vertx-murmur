var MessageRepo = (function(eb, global) {
	var repo = {};

	var _userChatList = {};	
	repo.chatList = [];

	var addMessage = function(message) {
		var userKey = message.from == global.currentUser ? message.to : message.from;
		if (typeof _userChatList[userKey] =='undefined') {				
			_userChatList[userKey] = [];
		}
		_userChatList[userKey].unshift(message);

		if (userKey == global.currentRecipient) { 
			repo.chatList.unshift(message);
	    	repo.updateChatList(repo.chatList);
		}
	}

	var handleMessage = function() {
		// Setup incoming message queue with address "msg.[name]"
	  	eb.registerHandler('msg.' + global.currentUser, 
		  	function(message) {
		  		addMessage(message);
		    	console.log('received a message: ' + JSON.stringify(message));
		  	}
	  	);
	}

	////////////////////////////////
	// React render callbacks
	////////////////////////////////
	repo.updateChatList; 

	////////////////////////////////	
	// Actions
	////////////////////////////////
	repo.loadMessages = function() {
		// retrieve list of messages for the current user
	 	eb.sendMsg('find-messages', { user: global.currentUser },
			function(reply) {
				_userChatList = reply;	
				handleMessage();

				// !!!! should be set off of user selection not initial load message
				repo.setChatList();			

			}
		);
	};	

	repo.setChatList = function() {
		var list = _userChatList[global.currentRecipient];
		if (typeof list == 'undefined') {
			return;
		}
		for (var i=0; i<list.length; i++) {
			repo.chatList.push(list[i]);
		}
		repo.updateChatList(repo.chatList);
	};

	repo.sendMessage = function(message) {
		var msg = { 
			from : global.currentUser, 
			to : global.currentRecipient,
			message : message
		}
		eb.sendMsg('persist-message', msg, 
			function(reply) {
				addMessage(reply);
			}
		);
	};

	return repo;
}(EventBus, Global));