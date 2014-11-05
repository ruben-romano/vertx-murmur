var User = React.createClass({
  handleClick: function() {
    Global.currentRecipient = this.props.name;
    MessageRepo.setChatList(); 
    hideUserSelectModal();   
  },

  render: function() {
    return (
      <div className="item user" onClick={this.handleClick} >        
        <i className="circular inverted black large user icon"></i>
        <div className="content">
          <div className="ui large header">{this.props.name}</div>
        </div>
      </div>     
    );
  }
});