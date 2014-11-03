var ChatList = React.createClass({
  render: function() {
    var chatNodes = this.props.data.map(function (chat) {
      return (
        <Chat from={chat.from} datetime={chat.datetime} message={chat.message}>
        </Chat>
      );
    });
    return (
		<div className="ui inverted segment chatList">
			<div className="ui inverted relaxed divided list">      
	    		{chatNodes}
	    	</div>
	    </div>
    );
  }
});