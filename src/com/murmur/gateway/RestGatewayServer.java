package com.murmur.gateway;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;
import org.vertx.java.core.json.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestGatewayServer extends Verticle {

	private EventBus eb;

	private static final Map<String, String> SERVICES_MAP;
	static {
		Map<String, String> servicesMap = new HashMap<String, String>();
		servicesMap.put("chat-server", "chat");
		servicesMap.put("fortune", "fortune");
		servicesMap.put("insult", "insult");
		servicesMap.put("magic-eight-ball", "magiceight");
		SERVICES_MAP = Collections.unmodifiableMap(servicesMap);
	}

	private final String EB_RECEIVE_ADDRESS = "send-message";
	private final String EB_SEND_ADDRESS = "persist-message";

	public void start() {
		eb = vertx.eventBus();

		Handler<Message<JsonObject>> handler = new Handler<Message<JsonObject>>() {
      		@Override
      		public void handle(Message<JsonObject> message) {

      			JsonObject messageJson = message.body();
        		System.out.println("Received " + messageJson);
        		String messageTo = messageJson.getString("to").replaceAll(" " , "-");
        		String serviceName = SERVICES_MAP.get(messageTo);
        		String messageFrom = messageJson.getString("from");
        		String messageStr = messageJson.getString("message");

        		// make REST service call
    			JsonObject response = sendRestReqest(serviceName, messageFrom, messageStr);

    			// persist service message
    			vertx.eventBus().send(EB_SEND_ADDRESS, response, new Handler<Message<JsonObject>>() {
				    public void handle(Message<JsonObject> message) {
				        System.out.println("Log persistor reply: " + message.body());
				    }
				});
      		}
    	};

		// register event message receiver to listen for each service
		for (Map.Entry<String, String> entry : SERVICES_MAP.entrySet()) {		
			String serviceAddress = "msg." + entry.getKey();
			eb.registerHandler(serviceAddress, handler);
		}
	}

	private JsonObject sendRestReqest(String restService, String messageFrom, String message) {
		JsonObject response = null;
		try {
			response = new JsonObject(HttpUtil.sendPost(restService, messageFrom, message));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return response;
	}
}
