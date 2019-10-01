package org.carnmoney.AVIC.desktop;

public class AtlonaSW510Input {
	public static final int USBC = 0;
	public static final int DISPLAYPORT = 1;
	public static final int HDMI1 = 2;
	public static final int HDMI2 = 3;
	public static final int BYOD = 4;
	
	private static String inputNames[] =  {"USBC","DISPLAYPORT","HDMI1","HDMI2","BYOD"};
	
	public static String getNameFor(int inputNumber) {
		if (inputNumber < 0 || inputNumber > 4) return "UNKNOWN";
		return inputNames[inputNumber];
	}
}
