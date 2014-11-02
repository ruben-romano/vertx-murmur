package com.murmur.gateway;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
import javax.net.ssl.HttpsURLConnection;

public class HttpUtil {
	private static final String SERVER_IP_ADDRESS = "192.168.80.128";
	private static final String PORT = "3000";
	private static final String USER_AGENT = "Mozilla/5.0";

	// HTTP POST request
	public static String sendPost(String restService, String messageFrom, String messageIn) throws Exception {

		String url = "http://" + SERVER_IP_ADDRESS + ":" + PORT + "/" + restService;
		URL obj = new URL(url);

		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "message=" + messageIn + "&" + "from=" + messageFrom;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		String jsonStr = response.toString();
		System.out.println(jsonStr);
		return jsonStr;
	}  
}