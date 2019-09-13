
/*
 * AtlonaControl - Control an Atlona AT-UHD-SW-510 switcher
 * Written by Gary Bell, March 2019
 * 
 * Based heavily on the TrayIconDemo source code from Oracle.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package org.carnmoney.AVIC.desktop;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ConnectTimeoutException;




public class AvicDesktop implements MessageListener {

	private static Properties programSettings = new Properties();
	private static File programSettingsFile =  new File(System.getProperty("user.home"),"AVIC.properties");
	private static Menu displayMenu = null;
	
	private static AtlonaSW510RestController restController = new AtlonaSW510RestController(programSettings);
	private static MessageBroker messageBroker = new MessageBroker();
	private static ControlPanel panel = null;
	private static SettingsPanel settingsPanel = null;
	
    public static void main(String[] args) {
        
        try {
            
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            programSettings.load(new FileReader(programSettingsFile));
           
        } catch (UnsupportedLookAndFeelException ex) {
        	try {
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				UIManager.put("swing.boldMetal", Boolean.FALSE);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException  | NullPointerException e) {
			System.err.println("Error reading AVIC.properties file.");
			
			
		} 
        
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
                if (! NetworkChecker.addressIsReachable(programSettings.getProperty("host"),5000)) {
                	
                }
            }
        });
        
       
        
    }


	private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(createImage(programSettings.getProperty("app.logo"), "Program logo"));
        trayIcon.setImageAutoSize(true);
        final SystemTray tray = SystemTray.getSystemTray();
        
        // Create a popup menu components
        MenuItem aboutItem = new MenuItem(programSettings.getProperty("app.name","Atlona Control"));
        MenuItem settingsItem = new MenuItem("Settings...");
        
        displayMenu = new Menu("Switch to...");
        MenuItem hdmiItem = new MenuItem(programSettings.getProperty("input.labels.hdmi.1","HDMI 1 Source"));
        MenuItem displayPortItem = new MenuItem(programSettings.getProperty("input.labels.displayport","DisplayPort Source"));
        MenuItem usbcItem = new MenuItem(programSettings.getProperty("input.labels.usbc","USB-C Source"));
        MenuItem dvdItem = new MenuItem(programSettings.getProperty("input.labels.hdmi.2","HDMI 2 Souce"));
        MenuItem wirelessItem = new MenuItem(programSettings.getProperty("input.labels.byod","Wireless Source"));
        MenuItem exitItem = new MenuItem("Exit");
        
        hdmiItem.setActionCommand("SWITCH_HDMI1");
        displayPortItem.setActionCommand("SWITCH_DISPLAYPORT");
        usbcItem.setActionCommand("SWITCH_USBC");
        dvdItem.setActionCommand("SWITCH_HDMI2");
        wirelessItem.setActionCommand("SWITCH_BYOD");
        
        //Add components to popup menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(settingsItem);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(hdmiItem);
        displayMenu.add(displayPortItem);
        displayMenu.add(usbcItem);
        displayMenu.add(dvdItem);
        displayMenu.add(wirelessItem);
        popup.add(exitItem);
        
        trayIcon.setPopupMenu(popup);
        displayMenu.setEnabled(programSettings.containsKey("host"));
        
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            JOptionPane.showMessageDialog(null, programSettings.getProperty("app.name","AVIC Desktop") + 
            																"not started, could not show tray icon.", 
            																programSettings.getProperty("app.name","AVIC Desktop"), 
            																JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return;
        }
        
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "AVIC Desktop Switch Controller v1.0\nWritten by\n The Tech Services Team, Carnmoney Church.");
            }
        });
        
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //trayIcon.displayMessage("About","AVIC Desktop Switch Controller v1.0\nWritten by\n The Tech Services Team, Carnmoney Church.", TrayIcon.MessageType.INFO);
            	if (panel == null) panel = new ControlPanel(programSettings,restController);
            	panel.setVisible(true);
            }
        });
        
        settingsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (settingsPanel == null) {
					settingsPanel = new SettingsPanel(programSettings,programSettingsFile,restController);
					
				}
				settingsPanel.setVisible(true);
				
			}
		});
        
        
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MenuItem item = (MenuItem)e.getSource();
                
                String resultMessage = "";
                TrayIcon.MessageType resultIcon = TrayIcon.MessageType.INFO; 
                System.out.println(item.getLabel());
                SourceChangedMessage message = null;
                
                try {
                	switch (item.getActionCommand()) {
		                	case "SWITCH_HDMI1":
		                		if (restController.switchToSource(AtlonaSW510Input.HDMI1)) {
		                			resultMessage = "Switched to HDMI";
		                			resultIcon = TrayIcon.MessageType.INFO;
		                			message = new SourceChangedMessage(AtlonaSW510Input.HDMI1);
		                		} else {
		                			resultMessage = "Failed to switch to HDMI - is it connected?";
		                			resultIcon = TrayIcon.MessageType.ERROR;
		                		}
		                		break;
		                		
		                	case "SWITCH_DISPLAYPORT":
		                		if (restController.switchToSource(AtlonaSW510Input.DISPLAYPORT)) {
		                			resultMessage = "Switched to DisplayPort";
		                			resultIcon = TrayIcon.MessageType.INFO;
		                			message = new SourceChangedMessage(AtlonaSW510Input.DISPLAYPORT);
		                		} else {
		                			resultMessage = "Failed to switch to DisplayPort - is it connected?"; 
		                			resultIcon = TrayIcon.MessageType.ERROR;
		                		}
		                		break;
		                		
		                	case "SWITCH_USBC":
		                		if (restController.switchToSource(AtlonaSW510Input.USBC)) {
		                			resultMessage = "Switched to USB-C";
		                			resultIcon = TrayIcon.MessageType.INFO;
		                			message = new SourceChangedMessage(AtlonaSW510Input.USBC);
		                		} else {
		                			resultMessage =  "Failed to switch to USB-C - is it connected?";
		                			resultIcon = TrayIcon.MessageType.ERROR;
		                		}
		                		
		                		break;
		                	case "SWITCH_HDMI2":
		                		if (restController.switchToSource(AtlonaSW510Input.HDMI2)) {
		                			resultMessage = "Switched to DVD";
		                			resultIcon = TrayIcon.MessageType.INFO;
		                			message = new SourceChangedMessage(AtlonaSW510Input.HDMI2);
		                		} else {
		                			resultMessage = "Failed to switch to HDMI - is it connected?"; 
		                			resultIcon = TrayIcon.MessageType.ERROR;
		                		}
		                		
		                		break;
		                	case "SWITCH_BYOD":
		                		if (restController.switchToSource(AtlonaSW510Input.BYOD)) {
		                			resultMessage = "Switched to Wifi Source";
		                			resultIcon = TrayIcon.MessageType.INFO;
		                			message = new SourceChangedMessage(AtlonaSW510Input.BYOD);
		                		} else {
		                			resultMessage = "Failed to switch to Wifi Source - is it connected?"; 
		                			resultIcon = TrayIcon.MessageType.ERROR;
		                		}
		                		
		                		break;
		                	default:
		                		resultMessage = "Switching failed, Invalid Source \"" + item.getActionCommand() + "\"";
                	}
                	
                	if (message != null) MessageBroker.broadcastMessage(message);
                	trayIcon.displayMessage("Atlona Control",resultMessage,resultIcon);
                	
                } catch (AtlonaSwitchingException e1) {
                	trayIcon.displayMessage("Atlona Control",e1.getCause().toString(),TrayIcon.MessageType.ERROR);
                	System.err.println(e1.getCause());
                } 
            }
        };
        
        hdmiItem.addActionListener(listener);
        displayPortItem.addActionListener(listener);
        usbcItem.addActionListener(listener);
        dvdItem.addActionListener(listener);
        wirelessItem.addActionListener(listener);
        
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
        
        
    }
    
    

	//Obtain the image URL
    protected static Image createImage(String path, String description) {
    	
    	URL defaultImage = AvicDesktop.class.getResource("/cclogo.png");
    	URL imageURL = null;
    	try {
    		File f = new File(path);
    		if (f.exists()) {
    			imageURL = f.toURI().toURL();
    		} else {
    			imageURL = defaultImage;
    		}
    	  
        } catch(MalformedURLException e) {
        	imageURL = defaultImage;
            
        }
    	return (new ImageIcon(imageURL, description)).getImage();
    }


	
	@Override
	public void receiveMessage(Message msg) {
		
		switch(msg.getMessageType()) {
		case SHOW_SETTINGS:
			settingsPanel.setVisible(true);
			break;
		default:
			break;
		
		}
		
	}
  
}
