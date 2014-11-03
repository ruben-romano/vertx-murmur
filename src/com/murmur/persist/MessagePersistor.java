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

public class MessagePersistor extends Verticle { 

	private final String EB_PERSIST_MESSAGE_ADDRESS = "persist-message";
	private final String EB_PERSIST_USER_ADDRESS = "persist-user";
	private final String EB_FIND_MESSAGES_ADDRESS = "murmur-find-messages";
	private final String EB_FIND_UERS_ADDRESS = "murmur-find-users";

	private final String MONGO_PERSISTOR_ADDRESS = "murmur.persistor";

	EventBus eb;

	public void start() {
		eb = vertx.eventBus();
		initMongo();

		// register event message receiver to listen to address persist-message
		eb.registerHandler(EB_PERSIST_MESSAGE_ADDRESS, new Handler<Message<JsonObject>>() {
      		@Override
      		public void handle(Message<JsonObject> vertxmessage) {
      			JsonObject message = vertxmessage.body();
        		persistMessage(message);
        		vertxmessage.reply(message);
      		}      		
    	});		


		// register event message receiver to listen to address persist-message
		eb.registerHandler(EB_PERSIST_USER_ADDRESS, new Handler<Message<JsonObject>>() {
      		@Override
      		public void handle(Message<JsonObject> vertxmessage) {
      			JsonObject message = vertxmessage.body();
        		persistUser(message);
        		vertxmessage.reply(message);
      		}      		
    	});	

    	// register event message receiver to listen to address murmur-find-messages
		eb.registerHandler(EB_FIND_MESSAGES_ADDRESS, new Handler<Message<JsonObject>>() {
      		@Override
      		public void handle(Message<JsonObject> vertxmessage) {
      			JsonObject message = vertxmessage.body();
        		vertxmessage.reply(message);
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

	private void persistMessage(JsonObject message) {
		// add time stamp to message
		message.putString("datetime", DateParser.now());

		JsonObject saveToMongo = new JsonObject();
		saveToMongo.putString("action", "save");
		saveToMongo.putString("collection", "messages");
		saveToMongo.putObject("document", message);

		eb.send(MONGO_PERSISTOR_ADDRESS, saveToMongo, new Handler<Message<JsonObject>>() {
	        public void handle(Message<JsonObject> reply) {
	        	System.out.println("PERSIST MESSAGE: " + reply.body());
	        	message.putString("key", reply.body().getString("_id"));
	        	forwardMessage(message);
	        }
	    });
	}

	private void persistUser(JsonObject message) {
		// add time stamp to message
		message.putString("datetime", DateParser.now());

		JsonObject saveToMongo = new JsonObject();
		saveToMongo.putString("action", "save");
		saveToMongo.putString("collection", "users");
		saveToMongo.putObject("document", message);

		eb.send(MONGO_PERSISTOR_ADDRESS, saveToMongo, new Handler<Message<JsonObject>>() {
	        public void handle(Message<JsonObject> reply) {
	        	System.out.println("PERSIST USER: " + reply.body());
	        	forwardMessage(message);
	        }
	    });
	}	

	private void forwardMessage(JsonObject message) {
		// send both to sender and recipient
		String forwardAddress = "user." + message.getString("to").replaceAll(" " , "-");
		eb.publish(forwardAddress, message);	 
		System.out.println("Sent to user. " + forwardAddress);
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
