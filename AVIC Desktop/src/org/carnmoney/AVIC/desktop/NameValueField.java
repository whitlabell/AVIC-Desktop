package org.carnmoney.AVIC.desktop;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NameValueField extends JPanel {

	private JTextField valueField = null;
	
	public NameValueField(String name, int fieldSize)  {
		buildGUI(name,fieldSize);
	}

	private void buildGUI(String name, int fieldSize) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		valueField = new JTextField(fieldSize);
		this.add(new JLabel(name));
		this.add(valueField);
		
		
	}
	public String getValueAsString() {
		return valueField.getText();
	}
	
	public void setValue(String value) {
		valueField.setText(value);
	}
}
