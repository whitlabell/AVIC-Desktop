package org.carnmoney.AVIC.desktop;

import java.util.LinkedList;
import java.util.List;

public class MessageBroker {
	
	private static List<MessageListener> clients = new LinkedList<MessageListener>();
	
	public static void addClient(MessageListener client) {
		if (! clients.contains(client)) {
			clients.add(client);
		}
	}
	
	public static void removeClient(MessageListener client) {
		clients.remove(client);
	}
	
	public static void broadcastMessage(Message msg) {
		
			for (MessageListener client : clients) {
				try {
					client.receiveMessage(msg);
				} catch (Exception e) {
					System.err.println("MessageBroker:broadcastMessage failed to client object - " + e.getMessage());
				}
				
			}
		
	}
}
