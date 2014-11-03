var Chat = React.createClass({
  render: function() {
    return (
      <div className="item chat">
        <div className="content">
          <div className="header chat-detail">{this.props.from} @ {this.props.datetime}</div>
          {this.props.message}
        </div>
      </div>
    );
  }
});