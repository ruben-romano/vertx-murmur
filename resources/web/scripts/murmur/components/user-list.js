var UserList = React.createClass({
  render: function() {    
    var userNodes = this.props.data.map(function (user, i) {
      return (
        <User icon={user.icon} name={user.name} id={i}>
        </User>
      );
    });

    return (
		  <div className="ui selection list">     
	    		{userNodes}
	    </div>
    );
  }
});