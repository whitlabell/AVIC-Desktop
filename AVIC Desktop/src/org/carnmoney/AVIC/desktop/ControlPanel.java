package org.carnmoney.AVIC.desktop;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ControlPanel extends JFrame implements ActionListener, ChangeListener, MessageListener {

	
	
	private JLabel connectionStatus = new JLabel("");
	private JLabel deviceLocation = new JLabel("");
	private LabelledButton hdmi1Button = null; 
	private LabelledButton hdmi2Button = null;
	private LabelledButton displayportButton = null;
	private LabelledButton usbcButton = null; 
	private LabelledButton byodButton = null;
	
	private JToggleButton muteButton = null;
	private JSlider audioLevel = null;

	private AtlonaSW510RestController restController = null;
	private Properties programSettings = null;
	
	
	public ControlPanel(Properties programSettings, AtlonaSW510RestController restController) {
		this.programSettings = programSettings;
		this.restController = restController;
		
		MessageBroker.addClient(this);
		
		this.setTitle(programSettings.getProperty("app.name","AtlonaControl"));
		buildGUI();
		populateGUI();
		pack();
		
	}

	private void buildGUI() {
		
		//top network & info panel
		JPanel networkPanel = new JPanel();
		networkPanel.setLayout(new BoxLayout(networkPanel, BoxLayout.LINE_AXIS));
		networkPanel.add(new JLabel("Device Location: "));
		networkPanel.add(deviceLocation);
		networkPanel.add(new JPanel());
		networkPanel.add(connectionStatus);
		connectionStatus.setOpaque(true);
		networkPanel.add(new JPanel());
		
		JButton refreshButton = new JButton("Settings...");
		refreshButton.setActionCommand("SETTINGS");
		refreshButton.addActionListener(this);
		refreshButton.setDefaultCapable(true);
		networkPanel.add(refreshButton);
		
		// Input button panel
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(BorderFactory.createTitledBorder("Input Selection: "));
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		hdmi1Button = new LabelledButton(programSettings.getProperty("input.labels.hdmi.1","HDMI 1"), "",this);
		hdmi2Button = new LabelledButton(programSettings.getProperty("input.labels.hdmi.2","HDMI 2"), "",this);
		displayportButton = new LabelledButton(programSettings.getProperty("input.labels.displayport","DisplayPort"), "",this);
		usbcButton = new LabelledButton(programSettings.getProperty("input.labels.usbc","USB-C"), "",this);
		byodButton = new LabelledButton(programSettings.getProperty("input.labels.byod","Wireless Source"), "",this);
		
		inputPanel.add(hdmi1Button);
		inputPanel.add(displayportButton);
		inputPanel.add(hdmi2Button);
		inputPanel.add(usbcButton);
		inputPanel.add(byodButton);
		
		//Audio panel
		JPanel audioPanel = new JPanel();
		audioPanel.setBorder(BorderFactory.createTitledBorder("Audio Settings: "));
		audioPanel.setLayout(new BoxLayout(audioPanel, BoxLayout.X_AXIS));
		
		muteButton = new JToggleButton("Mute");
		muteButton.setActionCommand("MUTE");
		muteButton.addActionListener(this);
		audioLevel = new JSlider(-80,0);
		audioLevel.addChangeListener(this);
		audioLevel.setMajorTickSpacing(10);
		audioLevel.setPaintTicks(true);
		audioLevel.setPaintLabels(true);

		audioPanel.add(muteButton);
		audioPanel.add(audioLevel);
		
		//Cancel button frame
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder());
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		
		JButton cancelButton = new JButton("Close");
		cancelButton.setActionCommand("CLOSE");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);
		
		// put it all together
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		topPanel.add(networkPanel);
		topPanel.add(new JPanel());
		topPanel.add(inputPanel);
		topPanel.add(new JPanel());
		topPanel.add(audioPanel);
		topPanel.add(new JPanel());
		topPanel.add(buttonPanel);
		
		getContentPane().add(topPanel);
	}

	

	private void populateGUI() {
		deviceLocation.setText(getDeviceLocation());
		if (NetworkChecker.deviceIsReady(restController)) {
			setConnectionStatus(true);
			enableButtons(true);
		} else {
			setConnectionStatus(false);
			enableButtons(false);
		}
		
	}
	
	private void enableButtons(boolean state) {
		
		hdmi1Button.setEnabled(state);
		hdmi2Button.setEnabled(state);
		displayportButton.setEnabled(state);
		usbcButton.setEnabled(state);
		byodButton.setEnabled(state);
		muteButton.setEnabled(state);
		audioLevel.setEnabled(state);
	}
	
	private void deselectAllSources() {
		hdmi1Button.setSelected(false);
		hdmi2Button.setSelected(false);
		displayportButton.setSelected(false);
		usbcButton.setSelected(false);
		byodButton.setSelected(false);
		
	}
	
	private void selectButtonForSource (int source) {
		deselectAllSources();
		switch (source) {
		case AtlonaSW510Input.HDMI1:
			hdmi1Button.setSelected(true);
			break;
		case AtlonaSW510Input.HDMI2:
			hdmi2Button.setSelected(true);
			break;
		case AtlonaSW510Input.DISPLAYPORT:
			displayportButton.setSelected(true);
			break;
		case AtlonaSW510Input.USBC:
			usbcButton.setSelected(true);
			break;
		case AtlonaSW510Input.BYOD:
			byodButton.setSelected(true);
			break;
		default: //Should never reach here!
			System.err.println("Unknown source [" + source + "] sent to Control Panel. Cannot update GUI.");
		}
	}

	private void setConnectionStatus(boolean connected) {
		
		if (connected) {
			connectionStatus.setBackground(Color.GREEN);
			connectionStatus.setForeground(Color.WHITE);
			connectionStatus.setText("Connected");
		} else {
			connectionStatus.setBackground(Color.RED);
			connectionStatus.setForeground(Color.WHITE);
			connectionStatus.setText("Not Connected!");
		}
		
	}

	private String getDeviceLocation() {
		return programSettings.getProperty("host","") + ":" + programSettings.getProperty("port","80");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "MUTE":
			if (muteButton.isSelected()) {
				if (restController.setMute(false)) {
					muteButton.setSelected(false);
					MessageBroker.broadcastMessage(new Message(MessageType.AUDIO_UNMUTED, "Audio Unmuted"));
				}
				
			} else {
				if (restController.setMute(true)) {
					muteButton.setSelected(true);
					MessageBroker.broadcastMessage(new Message(MessageType.AUDIO_MUTED, "Audio Muted"));
				}
			}
			break;
		
		case "SETTINGS":
			MessageBroker.broadcastMessage(new Message(MessageType.SHOW_SETTINGS,"Control panel has requested the Settings GUI be shown."));
			break;
		case "CLOSE":
			this.setVisible(false);
			break;
		
		}
			
			
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void receiveMessage(Message msg) {
		switch (msg.getMessageType()) {
			case SETTINGS_CHANGED:
				populateGUI();
				break;
			case AUDIO_MUTED:
				muteButton.setSelected(true);
				break;
			case AUDIO_UNMUTED:
				muteButton.setSelected(false);
				break;
			case AUDIO_LEVEL_CHANGED:
				int audiodB = ((AudioLevelChangedMessage)msg).getNewAudioLevel();
				if (audiodB < -80) audiodB = -80;
				if (audiodB > 0) audiodB = 0;
				audioLevel.setValue(audiodB);
				break;
			case SOURCE_CHANGED:
				selectButtonForSource( ((SourceChangedMessage) msg).getNewInputSource());
				break;
			case SHOW_SETTINGS:
				//ignore
				break;
		}
		
	}
	
}
