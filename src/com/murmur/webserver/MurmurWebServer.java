package com.murmur.webserver;

import org.vertx.java.platform.Verticle;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.json.*;

public class MurmurWebServer extends Verticle {

	public void start() {
		initWebServer();
	}

	private void initWebServer() { 
		HttpServer httpServer = vertx.createHttpServer();

		httpServer.requestHandler(new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest req) {
			  String file = "";
			  if (req.path().equals("/")) {
			    file = "index.html";
			  } else if (!req.path().contains("..")) {
			    file = req.path();
			  }
			  req.response().sendFile("resources/web/" + file);
			}
		});

		JsonObject config = new JsonObject();
		config.putString("prefix", "/eventbus");

		JsonArray outboundAddresses = new JsonArray();
		outboundAddresses.addObject(new JsonObject().putString("address", "persist-message"));
		outboundAddresses.addObject(new JsonObject().putString("address", "persist-user"));
		outboundAddresses.addObject(new JsonObject().putString("address", "find-messages"));
		outboundAddresses.addObject(new JsonObject().putString("address", "find-users"));

		JsonArray inboundAddresses = new JsonArray();	
		inboundAddresses.addObject(new JsonObject().putString("address", "user.status.notification"));
		inboundAddresses.addObject(new JsonObject().putString("address_re", "user\\..+"));
		inboundAddresses.addObject(new JsonObject().putString("address_re", "msg\\..+"));

		SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);
		sockJSServer.bridge(config, outboundAddresses, inboundAddresses);

		httpServer.listen(80);
	}	
}