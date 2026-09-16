package pixelbot.components.generic;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.EventListener;

import javax.swing.Icon;

import pixelbot.components.generic.JSelectablePicture.StateEvent.Type;

public class JSelectablePicture extends JPicture {
	private static final long serialVersionUID = -4715526222978409663L;

	public static class StateEvent {
		public enum Type {
			a, b;
		}

		public Integer x;
		public Integer y;
		public Color color;
		public Type type;
	}

	public abstract interface StateListener extends EventListener {
		public abstract void action(StateEvent event);
	}

	Point selpoint = new Point();
	Dimension seldem = new Dimension();
	public boolean mpressed = false;

	private int mouse_pos_x = 0;
	private int mouse_pos_y = 0;

	private BufferedImage image = null;

	public Rectangle getSelrect() {
		Rectangle result = new Rectangle(this.selpoint);
		if (this.seldem.width < 0) {
			if (result.x > -this.seldem.width) {
				result.x += this.seldem.width;
				result.width = -this.seldem.width;
			} else {
				result.width = result.x;
				result.x = 0;
			}
		} else if (this.seldem.width == 0) {
			result.width = 1;
		} else {
			if (result.x + this.seldem.width >= this.icon.getIconWidth()) {
				result.width = this.icon.getIconWidth() - 1 - result.x;
			} else {
				result.width = this.seldem.width;
			}
		}

		if (this.seldem.height < 0) {
			if (result.y > -this.seldem.height) {
				result.y += this.seldem.height;
				result.height = -this.seldem.height;
			} else {
				result.height = result.y;
				result.y = 0;
			}
		} else if (this.seldem.height == 0) {
			result.height = 1;
		} else {
			if (result.y + this.seldem.height >= this.icon.getIconHeight()) {
				result.height = this.icon.getIconHeight() - 1 - result.y;
			} else {
				result.height = this.seldem.height;
			}
		}

		return result;
	}

	public JSelectablePicture() {

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (JSelectablePicture.this.icon == null)
					return;
				JSelectablePicture.this.mpressed = true;
				JSelectablePicture.this.selpoint.setLocation(e.getX(), e.getY());
				JSelectablePicture.this.seldem.setSize(1, 1);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (JSelectablePicture.this.icon == null)
					return;
				JSelectablePicture.this.seldem.setSize(e.getX()
						- JSelectablePicture.this.selpoint.x, e.getY()
						- JSelectablePicture.this.selpoint.y);
				JSelectablePicture.this.fireStateChanged(Type.b);
				JSelectablePicture.this.mpressed = false;
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (JSelectablePicture.this.icon == null)
					return;
				if (JSelectablePicture.this.mpressed) {
					JSelectablePicture.this.seldem.setSize(e.getX()
							- JSelectablePicture.this.selpoint.x, e.getY()
							- JSelectablePicture.this.selpoint.y);
					JSelectablePicture.this.setMouse_pos_x(e.getX());
					JSelectablePicture.this.setMouse_pos_y(e.getY());
					JSelectablePicture.this.repaint();
					JSelectablePicture.this.fireStateChanged(Type.a);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (JSelectablePicture.this.icon == null)
					return;
				JSelectablePicture.this.setMouse_pos_x(e.getX());
				JSelectablePicture.this.setMouse_pos_y(e.getY());
				JSelectablePicture.this.repaint();
				JSelectablePicture.this.fireStateChanged(Type.a);
			}
		});
	}

	@Override
	public void setIcon(Icon icon) {
		super.setIcon(icon);
		this.image = null;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			Rectangle r = this.getSelrect();
			g.drawRect(r.x, r.y, r.width, r.height);

			g.setColor(new Color(255, 153, 51, 150));
			g.fillRoundRect(this.mouse_pos_x + 2, this.mouse_pos_y - 17, 57, 14, 15, 15);
			g.setColor(Color.RED);
			int col = this.getImage().getRGB(this.mouse_pos_x, this.mouse_pos_y);
			g.drawString(
					String.format("%02X%02X%02X", new Object[] { Integer.valueOf(col >> 16 & 0xFF),
							Integer.valueOf(col >> 8 & 0xFF), Integer.valueOf(col & 0xFF) }),
					this.mouse_pos_x + 7, this.mouse_pos_y - 5);
		} catch (Exception e) {

		}

	}

	public void addStateListener(StateListener x) {
		this.listenerList.add(StateListener.class, x);
	}

	void removeStateListener(StateListener x) {
		this.listenerList.remove(StateListener.class, x);
	}

	protected void fireStateChanged(Type type) {
		StateEvent event = new StateEvent();
		event.x = Integer.valueOf(this.mouse_pos_x);
		event.y = Integer.valueOf(this.mouse_pos_y);
		event.color = new Color(this.getImage().getRGB(this.mouse_pos_x, this.mouse_pos_y));
		event.type = type;
		Object[] listeners = this.listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == StateListener.class) {
				((StateListener) listeners[i + 1]).action(event);
			}
		}
	}

	public BufferedImage getSelImage() {
		Rectangle selrect = this.getSelrect();
		if (this.getImage() == null)
			return null;
		try {
			return this.getImage().getSubimage(
					-(this.getWidth() - this.icon.getIconWidth()) / 2 + selrect.x,
					-(this.getHeight() - this.icon.getIconHeight()) / 2 + selrect.y, selrect.width,
					selrect.height);
		} catch (Exception e) {
			return null;
		}

	}

	private BufferedImage getImage() {
		if (this.icon == null)
			return null;
		if (this.image == null) {
			this.image = new BufferedImage(this.icon.getIconWidth(), this.icon.getIconHeight(),
					BufferedImage.TYPE_INT_ARGB);
			this.icon.paintIcon(this, this.image.getGraphics(), 0, 0);
		}
		return this.image;
	}

	public void setMouse_pos_x(int mouse_pos_x) {
		this.mouse_pos_x = mouse_pos_x;
		this.mouse_pos_x = this.mouse_pos_x < 0 ? 0 : this.mouse_pos_x;
		this.mouse_pos_x = this.mouse_pos_x > this.icon.getIconWidth() - 1 ? this.icon
				.getIconWidth() - 1 : this.mouse_pos_x;
	}

	public void setMouse_pos_y(int mouse_pos_y) {
		this.mouse_pos_y = mouse_pos_y;
		this.mouse_pos_y = this.mouse_pos_y < 0 ? 0 : this.mouse_pos_y;
		this.mouse_pos_y = this.mouse_pos_y > this.icon.getIconHeight() - 1 ? this.icon
				.getIconHeight() - 1 : this.mouse_pos_y;
	}

}
