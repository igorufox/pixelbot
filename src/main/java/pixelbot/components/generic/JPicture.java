package pixelbot.components.generic;

import java.awt.*;

import javax.swing.*;

public class JPicture extends JPanel {
	private static final long serialVersionUID = -4715526222978409663L;

	protected Icon icon = null;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.icon != null) {
			this.icon.paintIcon(this, g, (this.getWidth() - this.icon.getIconWidth()) / 2,
					(this.getHeight() - this.icon.getIconHeight()) / 2);
		}
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		if (icon != null) {
			this.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
		}
		revalidate();
		repaint();

	}

	public Icon getIcon() {
		return this.icon;
	}

}
