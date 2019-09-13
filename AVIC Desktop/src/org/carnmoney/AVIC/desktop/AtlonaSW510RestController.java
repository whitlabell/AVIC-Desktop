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
	
	public boolean switchToSource(int source) throws AtlonaSwitchingException {
		
    	
    	String host = programSettings.getProperty("host","192.168.3.26");
    	String port = programSettings.getProperty("port", "80");
    	
		String reply="";
		
		try {
			reply = Request.Get(AtlonaSW510RestProtocol.getSwitchToSource(host, Integer.parseInt(port), source))
					.connectTimeout(2000)
					.socketTimeout(8000)
					.execute().returnContent().asString();
			System.out.println(reply);
		} catch (NullPointerException e) {
			return false;
		} catch (ClientProtocolException e) {
			throw new AtlonaSwitchingException("System NPE.",e);
		
		} catch (ConnectTimeoutException e) {
			throw new AtlonaSwitchingException("Couldn't connect to Atlona, are you on the right network?", e);
		}  catch (IOException e) {
			throw new AtlonaSwitchingException("Atlona connection error", e);
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
		} catch (NullPointerException e) {
			return false;
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		} catch (ClientProtocolException e) {
			return "";
		} catch (IOException e) {
			return "";
		}
		System.out.println(reply);
		
		
		return reply;
	}
}
