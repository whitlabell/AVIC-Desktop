package org.carnmoney.AVIC.desktop;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

public class NetworkChecker {
	
	
	static boolean addressIsReachable(String address, int timeoutMs) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(address);
			return inetAddress.isReachable(timeoutMs);
		
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + address);
			return false;
		} catch (IOException e) {
			System.err.println("addressIsReachable: Failed to connect - " + e.getMessage());
			return false;
		}
		
	}
	
	static boolean deviceIsReady(AtlonaSW510RestController restController) {
		String reply = restController.getTemperatureInCelsius();
		return reply.contains("\"temperature\":");
		
	}
	
	
}
