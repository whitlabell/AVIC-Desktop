package org.carnmoney.AVIC.desktop;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class SettingsPanel extends JFrame implements ActionListener{
	
	private NameValueField networkAddress = null;
	private NameValueField networkPort = null;
	private NameValueField appName = null;
	private NameValueField logoPath = null;
	private NameValueField hdmi1Label;
	private NameValueField hdmi2Label;
	private NameValueField usbcLabel;
	private NameValueField byodLabel;
	private NameValueField displayPortLabel;
	
	private Properties settings = null;
	private File settingsFile = null;
	private AtlonaSW510RestController restController = null;
	
	public SettingsPanel(Properties programSettings, File programSettingsFile, AtlonaSW510RestController restController) {
		this.settings = programSettings;
		this.settingsFile = programSettingsFile;
		this.restController = restController;
		
		this.setTitle("Settings");
		buildGUI();
		populateGUI();
		pack();
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
	
	private void populateGUI() {
		networkAddress.setValue(settings.getProperty("host",""));
		networkPort.setValue(settings.getProperty("port","80"));
		appName.setValue(settings.getProperty("app.name","AtlonaControl"));
		logoPath.setValue(settings.getProperty("app.logo","cclogo.png"));
		hdmi1Label.setValue(settings.getProperty("input.labels.hdmi1","HDMI"));
		hdmi2Label.setValue(settings.getProperty("input.labels.hdmi2","HDMI 2"));
		usbcLabel.setValue(settings.getProperty("input.labels.usbc", "USB-C"));
		byodLabel.setValue(settings.getProperty("input.labels.byod","Wireless source"));
		displayPortLabel.setValue(settings.getProperty("input.labels.displayport","DisplayPort"));
	}
	
	private void saveSettings() {
		settings.setProperty("host", networkAddress.getValueAsString());
		settings.setProperty("port", networkPort.getValueAsString());
		settings.setProperty("app.name", appName.getValueAsString());
		settings.setProperty("app.logo", logoPath.getValueAsString());
		settings.setProperty("input.labels.hdmi1", hdmi1Label.getValueAsString());
		settings.setProperty("input.labels.hdmi2", hdmi2Label.getValueAsString());
		settings.setProperty("input.labels.usbc", usbcLabel.getValueAsString());
		settings.setProperty("input.labels.byod", byodLabel.getValueAsString());
		settings.setProperty("input.labels.displayport", displayPortLabel.getValueAsString());
		
		restController = new AtlonaSW510RestController(settings);
		
		try {
			settings.store(new FileWriter(settingsFile), "Connection properties for Atlona AV Switcher");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to save settings!\n" + e.getMessage(), "Save Settings failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void buildGUI() {
		networkAddress = new NameValueField("Address: ", 10);
		networkPort = new NameValueField("Port: ", 5);
		appName = new NameValueField("App Name: ", 15);
		logoPath = new NameValueField("Logo Icon: ", 15);
		hdmi1Label = new NameValueField("HDMI 3 :", 10);
		hdmi2Label = new NameValueField("HDMI 4 :", 10);
		usbcLabel = new NameValueField("USB-C: ", 10);
		displayPortLabel = new NameValueField("DisplayPort: ", 10);
		byodLabel = new NameValueField("BYOD: ", 10);
		
		// network settings frame
		JPanel nwSettings = new JPanel();
		nwSettings.setBorder(BorderFactory.createTitledBorder(" Network Settings "));
		nwSettings.setLayout(new BoxLayout(nwSettings,BoxLayout.LINE_AXIS));
		nwSettings.add(networkAddress);
		nwSettings.add(networkPort);
		
		JButton testButton = new JButton("Test..");
		testButton.setActionCommand("test connection");
		testButton.addActionListener(this);
		nwSettings.add(testButton);
		
		//Personalisation panel
		JPanel customSettings = new JPanel();
		customSettings.setBorder(BorderFactory.createTitledBorder(" Personalisation "));
		customSettings.setLayout(new BoxLayout(customSettings, BoxLayout.LINE_AXIS));
		customSettings.add(appName);
		customSettings.add(logoPath);
		
		JButton logoSelect = new JButton("Browse...");
		logoSelect.setActionCommand("select logo");
		logoSelect.addActionListener(this);
		customSettings.add(logoSelect);
		
		//input labels
		JPanel inputLabels = new JPanel();
		inputLabels.setBorder(BorderFactory.createTitledBorder(" Input Labels "));
		inputLabels.setLayout(new GridLayout(3, 2, 5, 5));
		inputLabels.add(displayPortLabel);
		inputLabels.add(hdmi1Label);
		inputLabels.add(usbcLabel);
		inputLabels.add(hdmi2Label);
		inputLabels.add(byodLabel);
		
		JPanel personalisation = new JPanel();
		personalisation.setLayout(new BoxLayout(personalisation, BoxLayout.Y_AXIS));
		personalisation.add(customSettings);
		personalisation.add(new JPanel());
		personalisation.add(inputLabels);
		
		//control button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		JButton saveButton = new JButton("Save");
		saveButton.setActionCommand("save");
		saveButton.addActionListener(this);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		
		// put it all together
		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 3, 5));
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(nwSettings);
		topPanel.add(new JPanel());
		topPanel.add(personalisation);
		topPanel.add(new JPanel());
		topPanel.add(buttonPanel);
	
		add(topPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		switch(cmd) {
		case "cancel":
			this.setVisible(false);
			this.dispose();
			break;
		case "save":
			saveSettings();
			this.setVisible(false);
			MessageBroker.broadcastMessage(new Message(MessageType.SETTINGS_CHANGED,"Program preferences updated."));
			this.dispose();
			break;
		case "test connection":
			Properties tempProperties = new Properties(settings);
			tempProperties.setProperty("host", networkAddress.getValueAsString());
			tempProperties.setProperty("port", networkPort.getValueAsString());
			AtlonaSW510RestController tempRestController = new AtlonaSW510RestController(tempProperties);
			if (NetworkChecker.deviceIsReady(tempRestController)) {
				JOptionPane.showMessageDialog(this, "Successfully contacted Atlona switcher.","Test Successful",JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "No answer from switcher:\n"
												  + "  1. Check the network settings are correct.\n"
												  + "  2. If connecting via WiFi, check you are on the right network.",
												  "Test failed",JOptionPane.ERROR_MESSAGE);
			}
			break;
		case "select logo":
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Images", "png", "jpg", "gif");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		            logoPath.setValue(chooser.getSelectedFile().getAbsolutePath());
		    }
		}
		
	}

	

}
