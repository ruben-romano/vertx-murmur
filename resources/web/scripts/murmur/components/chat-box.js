var ChatBox = React.createClass({
  getInitialState: function() {
    MessageRepo.updateChatList = function(data) {
      this.setState(data);
    }.bind(this);

    return {data: MessageRepo.chatList};
  },

  render: function() {
    var chatbox;
    if (MessageRepo.chatList.length > 0) {
      chatbox =
          <div className="chatBox chat-box">
            <div className="ui one column grid">
              <div className="row">
                <div className="column">
                  <ChatList data={this.props.data} />
                </div>
              </div>
            </div>
          </div>;
    } else {
      chatbox = <div></div>;
    }

    return (
      <div>
          {chatbox}
      </div>
    );
  }
});

React.render(
  <ChatBox data={MessageRepo.chatList}/>,
  	document.getElementById('chat-box-content')
);