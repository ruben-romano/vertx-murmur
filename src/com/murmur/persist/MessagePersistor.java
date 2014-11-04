package com.murmur.persist;

import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.JsonArray;

import java.util.Iterator;

public class MessagePersistor extends Verticle { 

	private final String EB_PERSIST_MESSAGE_ADDRESS = "persist-message";
	private final String EB_PERSIST_USER_ADDRESS = "persist-user";
	private final String EB_FIND_MESSAGES_ADDRESS = "find-messages";
	private final String EB_FIND_UERS_ADDRESS = "find-users";

	protected final String MONGO_PERSISTOR_ADDRESS = "murmur.persistor";
	protected int SEQUENCER = 0;
	protected EventBus eb;


	public void start() {
		eb = vertx.eventBus();
		initMongo();

		// register event message receiver to listen to address persist-message
		eb.registerHandler(EB_PERSIST_MESSAGE_ADDRESS, new Handler<Message<JsonObject>>() {
      		@Override
      		public void handle(Message<JsonObject> vertxmessage) {
      			JsonObject message = vertxmessage.body();
        		persistMessage(message, vertxmessage);
      		}      		
    	});		


		// register event message receiver to listen to address persist-user
		eb.registerHandler(EB_PERSIST_USER_ADDRESS, new Handler<Message<JsonObject>>() {
      		@Override
      		public void handle(Message<JsonObject> vertxmessage) {
      			JsonObject message = vertxmessage.body();
        		persistUser(message, vertxmessage);
      		}      		
    	});	

    	// register event message receiver to listen to address murmur-find-messages
		eb.registerHandler(EB_FIND_MESSAGES_ADDRESS, new Handler<Message<JsonObject>>() {
      		@Override
      		public void handle(Message<JsonObject> vertxmessage) {
      			JsonObject message = vertxmessage.body();
        		findMessages(message, vertxmessage);
      		}      		
    	});		

    	// register event message receiver to listen to address murmur-find-users
		eb.registerHandler(EB_FIND_UERS_ADDRESS, new Handler<Message<JsonObject>>() {
      		@Override
      		public void handle(Message<JsonObject> vertxmessage) {
      			JsonObject message = vertxmessage.body();
        		vertxmessage.reply(message);
      		}      		
    	});		
	}

	private void persistMessage(JsonObject message, Message<JsonObject> vertxmessage) {
		// add time stamp to message
		message.putString("datetime", DateParser.now());
		
		// increment sequencer and add to message
		SEQUENCER++;
		message.putNumber("sequence", SEQUENCER);		

		JsonObject saveToMongo = new JsonObject();
		saveToMongo.putString("action", "save");
		saveToMongo.putString("collection", "messages");
		saveToMongo.putObject("document", message);

		eb.send(MONGO_PERSISTOR_ADDRESS, saveToMongo, new Handler<Message<JsonObject>>() {
	        public void handle(Message<JsonObject> reply) {
	        	System.out.println("PERSIST MESSAGE: " + reply.body());
	        	message.putString("key", reply.body().getString("_id"));

	        	vertxmessage.reply(message);
	        	forwardMessage(message);
	        }
	    });
	}

	private void persistUser(JsonObject message, Message<JsonObject> vertxmessage) {
		// add time stamp to message
		message.putString("datetime", DateParser.now());

		JsonObject saveToMongo = new JsonObject();
		saveToMongo.putString("action", "save");
		saveToMongo.putString("collection", "users");
		saveToMongo.putObject("document", message);

		eb.send(MONGO_PERSISTOR_ADDRESS, saveToMongo, new Handler<Message<JsonObject>>() {
	        public void handle(Message<JsonObject> reply) {
	        	System.out.println("PERSIST USER: " + reply.body());
	        	message.putString("key", reply.body().getString("_id"));

	        	vertxmessage.reply(message);
	        	forwardMessage(message);
	        }
	    });
	}	

	private void findMessages(JsonObject message, Message<JsonObject> vertxmessage) {
		String userToFindMessages = message.getString("user");

		JsonObject findMessagesInMongo = new JsonObject();
		findMessagesInMongo.putString("action", "find");
		findMessagesInMongo.putString("collection", "messages");
		findMessagesInMongo.putObject("sort", new JsonObject().putNumber("sequence", -1));

		// create message matcher
		JsonArray clauseArray = new JsonArray();
		clauseArray.add(new JsonObject().putString("to", userToFindMessages));
		clauseArray.add(new JsonObject().putString("from", userToFindMessages));
		JsonObject orMatcher = new JsonObject();
		orMatcher.putArray("$or", clauseArray);
		findMessagesInMongo.putObject("matcher", orMatcher);
		System.out.println("matcher: " + findMessagesInMongo);

		eb.send(MONGO_PERSISTOR_ADDRESS, findMessagesInMongo, new Handler<Message<JsonObject>>() {
	        public void handle(Message<JsonObject> reply) {
	        	JsonArray results = reply.body().getArray("results");
	        	JsonObject messageArray = createMessageArray(userToFindMessages, results);	        
	        	System.out.println(messageArray);	
	        	vertxmessage.reply(messageArray);
	        }
	    });
	}	

	private JsonObject createMessageArray(String user, JsonArray results) {
		JsonObject messageArray = new JsonObject();
		Iterator<Object> it = results.iterator();		
		while (it.hasNext()) {
			JsonObject msg = (JsonObject) it.next();

			String from = msg.getString("from");
			String to = msg.getString("to");
			String userKey = from.equals(user) ? to : from;

			if (messageArray.getArray(userKey) != null) {
				messageArray.getArray(userKey).add(msg);
			} else {
				JsonArray msgArray = new JsonArray();
				msgArray.add(msg);
				messageArray.putArray(userKey, msgArray);
			}
		}
		return messageArray;
	}

	private void forwardMessage(JsonObject message) {
		// send both to sender and recipient
		String forwardAddress = "user." + message.getString("to").replaceAll(" " , "-");
		eb.publish(forwardAddress, message);	 
		System.out.println("Sent to: " + forwardAddress);
	}

	private void initMongo() {
		JsonObject config = new JsonObject();
	    config.putString("address", MONGO_PERSISTOR_ADDRESS);
	    config.putString("db_name", System.getProperty("vertx.mongo.database", "murmur_db"));
	    config.putString("host", System.getProperty("vertx.mongo.host", "localhost"));
	    config.putNumber("port", Integer.valueOf(System.getProperty("vertx.mongo.port", "27017")));
	    String username = System.getProperty("vertx.mongo.username");
	    String password = System.getProperty("vertx.mongo.password");
	    if (username != null) {
	      config.putString("username", username);
	      config.putString("password", password);
	    }
	    config.putBoolean("fake", false);
	    container.deployModule("io.vertx~mod-mongo-persistor~2.1.0", config, 1, new AsyncResultHandler<String>() {
	      public void handle(AsyncResult<String> ar) {
	        if (ar.succeeded()) {
	        	System.out.println("Mongo Module deployment succeeded.");
	        } else {
	          ar.cause().printStackTrace();
	        }
	      }
	    });
	}	
}
