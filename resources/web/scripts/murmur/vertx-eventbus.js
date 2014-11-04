

var data = [{from: "murmur", datetime: "now", message: "send a message ..."}];
var messageStore;
var currentUser;
var currentRecipient = 'chat server';
var updateChatList;
var eb;

$('.small.modal')
  .modal('setting', 'closable', false)
  .modal('show')
;
