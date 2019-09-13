package org.carnmoney.AVIC.desktop;

public class AtlonaSW510RestProtocol {

	static String getTemperatureInCelsius(String address, String port) {
		return "http://" + address + ":" + port +"/API?method=Instruments:Temperature:Get&scale=celsius";
	}
	
	static String getSwitchToSource(String address, int port, int source) {
		return "http://" + address + ":" + port + "/API?method=Display:Input:Set&input=" + Integer.toString(source);
	}
	
	static String getAllInputStatus(String address, int port) {
		return "http://" + address + ":" + port + "/API?method=Display:Input:Status:All:Get";
	}

	public static String getSetMute(String address, String port, String audioSourceName, boolean selected) {
		return "http://" + address + ":" + port +"/API?method=Audio:Mute:Set&" + audioSourceName + "=" + selected;
	}
}
