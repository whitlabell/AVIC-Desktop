package org.carnmoney.AVIC.desktop;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ConnectTimeoutException;

public class AtlonaSW510RestController {

	private  Properties programSettings;

	public AtlonaSW510RestController(Properties programSettings) {
		this.programSettings = programSettings;
	}
	
	public boolean switchToSource(int source)  {
		
    	
    	String host = programSettings.getProperty("host","192.168.3.26");
    	String port = programSettings.getProperty("port", "80");
    	
		String reply="";
		
		try {
			reply = Request.Get(AtlonaSW510RestProtocol.getSwitchToSource(host, Integer.parseInt(port), source))
					.connectTimeout(2000)
					.socketTimeout(8000)
					.execute().returnContent().asString();
			System.out.println(reply);
		} catch (NullPointerException | IOException e) {
			System.err.println("Switching Exception: " + e.getMessage());
			return false;
		} 
		
		boolean rc = reply.contains("\"success\":true");
		return rc;

	}

	public boolean setMute(boolean selected) {
		String host = programSettings.getProperty("host","192.168.3.26");
    	String port = programSettings.getProperty("port", "80");
    	
		String reply1 ="";
		String reply2 = "";
		
		try {
			reply1 = Request.Get(AtlonaSW510RestProtocol.getSetMute(host, port,AtlonaSW510Output.AUDIO_HDMI, selected))
					.connectTimeout(2000)
					.socketTimeout(8000)
					.execute().returnContent().asString();
			reply2 = Request.Get(AtlonaSW510RestProtocol.getSetMute(host, port,AtlonaSW510Output.AUDIO_ANALOG, selected))
					.connectTimeout(2000)
					.socketTimeout(8000)
					.execute().returnContent().asString();
		} catch (NullPointerException | IOException e) {
			System.err.println("Mute Exception: " + e.getMessage());
			return false;
		
		} 
		
		boolean rc = reply1.contains("\"success\":true") && reply2.contains("\"success\":true");
		return rc;
	}
	
	public String getTemperatureInCelsius() {
		
		String host = programSettings.getProperty("host","192.168.3.26");
    	String port = programSettings.getProperty("port", "80");
    	
		String restURL = AtlonaSW510RestProtocol.getTemperatureInCelsius(host,port);
		String reply = "";
		
		try {
			reply = Request.Get(restURL)
					.connectTimeout(2000)
					.socketTimeout(8000)
					.execute().returnContent().asString();
		} catch (IOException e) {
			System.err.println("Exception getting temperature: " + e.getMessage());
			return "";
		}
		
		return reply;
	}

	public boolean setAudioLevel(int value) {
		// TODO Auto-generated method stub
		return false;
	}
}
