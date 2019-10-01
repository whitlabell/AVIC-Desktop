package org.carnmoney.AVIC.desktop;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class LabelledButton extends JPanel {
	
	private JTextField label = null;
	private JToggleButton button = null;
	
	public LabelledButton(String buttonName,String labelText, ActionListener listener) {
		buildGUI(buttonName,labelText);
		button.addActionListener(listener);
	}
	
	

	private void buildGUI(String buttonName, String labelText) {
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		button = new JToggleButton(buttonName);
		button.setAlignmentX(CENTER_ALIGNMENT);
		label = new JTextField(labelText,10);
		label.setBorder(BorderFactory.createDashedBorder(null));
		label.setBackground(Color.WHITE);
		label.setAlignmentX(CENTER_ALIGNMENT);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		
		add(button);
		add(label);
	}

	public void setEnabled(boolean state) {
		button.setEnabled(state);
	}
	
	public void setSelected(boolean selected) {
		button.setSelected(selected);
	}
	
	public JToggleButton getButton() {
		return button;
	}
}
