package org.carnmoney.AVIC.desktop;

public class Message {

	private MessageType msgType = null;
	private String text = "";
	
	public MessageType getMessageType() {
		return msgType;
	}
	
	public String getText() {
		return text;
		
	}
	public Message(MessageType type, String messageText) {
		this.msgType = type;
		this.text = messageText;
		
	}
		
}
