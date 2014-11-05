var UserRepo = (function(eb, global) {
	var repo = {};
	repo.userList = [
		{icon:"flag", name:"magic eight ball"}, 
		{icon:"anchor", name:"chat server"}, 
		{icon:"cloud", name:"insult"}, 
		{icon:"unhide", name:"fortune"},
		{icon:"user", name:"sally"},
		{icon:"user", name:"ruben"}
	];

	var addUser = function(user) {
		repo.userList.unshift(user);
	}

	var handleMessage = function() {
		// Setup incoming user queue with address "user.[name]"
	  	eb.registerHandler('user.' + global.currentUser, 
		  	function(user) {
		  		addMessage(user);
		    	console.log('received a user: ' + JSON.stringify(user));
		  	}
	  	);
	}

	////////////////////////////////
	// React render callbacks
	////////////////////////////////
	repo.updateUserList; 

	////////////////////////////////	
	// Actions
	////////////////////////////////
	repo.loadUsers = function() {
		// retrieve list of messages for the current user
	 	eb.sendMsg('find-users', {},
			function(reply) {
				repo.userList = reply;	
				handleMessage();
			}
		);
	};		

	return repo;
}(EventBus, Global));