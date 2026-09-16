package pixelbot.components.generic;

import java.awt.*;

import javax.swing.*;

public class JMultilineLabel extends JTextArea {
	private static final long serialVersionUID = -501274524077812117L;

	public JMultilineLabel() {
		super();
		
		this.setEditable(false);
		this.setCursor(null);
		this.setOpaque(false);
		this.setFocusable(false);
		this.setWrapStyleWord(true);
		this.setLineWrap(true);
		this.setBackground( new Color(0, 0, 0, 1) );
		this.setBorder(null);
	}
	
	@Override
	public Rectangle getBounds() {
		return super.getBounds();
	}
	
	
	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
	}
}