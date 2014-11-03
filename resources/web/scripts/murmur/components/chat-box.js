// tutorial1.js
var ChatBox = React.createClass({
  getInitialState: function() {
    updateChatList = function(data) {
      this.setState(data);
    }.bind(this);

    return {data: data};
  },

  render: function() {
    return (
		<div className="chatBox chat-box">
			<div className="ui one column grid">
				<div className="row">
					<div className="column">
						<ChatList data={this.props.data} />
					</div>
				</div>
			</div>
		</div>
    );
  }
});

React.render(
  <ChatBox data={data}/>,
  	document.getElementById('chat-box-content')
);