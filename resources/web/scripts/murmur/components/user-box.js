var UserBox = React.createClass({
  getInitialState: function() {
    UserRepo.updateUserList = function(data) {
      this.setState(data);
    }.bind(this);

    return {data: MessageRepo.userList};
  },

  render: function() {
    return (
		<div className="userBox user-box">
			<div className="ui one column grid">
				<div className="row">
					<div className="column">
						<UserList data={this.props.data} />
					</div>
				</div>
			</div>
		</div>
    );
  }
});

React.render(
  <UserBox data={UserRepo.userList}/>,
  	document.getElementById('user-box-content')
);