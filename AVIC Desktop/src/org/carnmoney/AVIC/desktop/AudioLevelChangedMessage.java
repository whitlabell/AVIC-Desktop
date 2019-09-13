package org.carnmoney.AVIC.desktop;

public class AudioLevelChangedMessage extends Message {
	
	private int newAudioLevel = -80;

	public AudioLevelChangedMessage(int newAudioLevel) {
		super(MessageType.AUDIO_LEVEL_CHANGED, "Audio level changed to " + newAudioLevel + " dB");
		this.newAudioLevel = newAudioLevel;
		
	}
	
	public int getNewAudioLevel() {
		return newAudioLevel;
	}

}
