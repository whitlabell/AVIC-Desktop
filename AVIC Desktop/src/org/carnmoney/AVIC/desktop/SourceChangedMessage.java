package org.carnmoney.AVIC.desktop;

public class SourceChangedMessage extends Message {

	private int newInputSource;
	
	public int getNewInputSource() {
		return newInputSource;
	}

	public SourceChangedMessage( int newInputSource) {
		super(MessageType.SOURCE_CHANGED,"Input source changed.");
		this.newInputSource = newInputSource;
	}
	
	
}
