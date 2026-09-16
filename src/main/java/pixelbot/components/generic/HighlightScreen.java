package pixelbot.components.generic;

import java.awt.*;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.geom.Area;
import java.util.*;
import java.util.List;

import javax.swing.JWindow;

public class HighlightScreen extends JWindow {
	private static final long serialVersionUID = 1011724006651107843L;

	private enum Transparency {
		None, TRANSLUCENT, PERPIXEL_TRANSLUCENT
	}

	public enum HighlightColor {
		scheme1, scheme2, scheme3, scheme4;

		private static final Color red = new Color(255, 0, 0, 150);
		private static final Color yellow = new Color(255, 255, 0, 150);
		private static final Color green = new Color(0, 255, 0, 150);
		private static final Color blue = new Color(0, 0, 255, 150);
		private static final Color magenta = new Color(255, 0, 255, 150);
		private static final Color cyan = new Color(0, 255, 255, 150);

		protected Color getColor(int i) {
			boolean b = (i % 2 == 0);
			switch (this) {
			case scheme1:
				return b ? HighlightColor.red : HighlightColor.yellow;
			case scheme2:
				return b ? HighlightColor.yellow : HighlightColor.green;
			case scheme3:
				return b ? HighlightColor.green : HighlightColor.blue;
			case scheme4:
				return b ? HighlightColor.cyan : HighlightColor.magenta;
			default:
				return Color.black;
			}
		}
	}

	public interface IHighlight {
		public long getMillis();

		public void paint(Graphics g, Area shape);
	}

	private static class Highlight implements IHighlight {
		public Highlight(Rectangle bounds, HighlightColor scheme) {
			this.bounds = bounds;
			this.millis = System.currentTimeMillis() + 1500;
			this.scheme = scheme;
		}

		private Rectangle bounds;
		private long millis;

		private HighlightColor scheme = HighlightColor.scheme1;
		private int thickness = 2;
		private int dashWidth = 5;
		private int dashHeight = 5;

		@Override
		public long getMillis() {
			return this.millis;
		}

		@Override
		public void paint(Graphics g, Area shape) {
			int numWide = Math.round((this.bounds.width - this.thickness) / this.dashWidth);
			int numHigh = Math.round((this.bounds.height - this.thickness) / this.dashHeight);
			int startPoint;
			for (int i = 0; i <= numWide; ++i) {
				g.setColor(this.scheme.getColor(i));
				startPoint = this.bounds.x + this.dashWidth * i;
				g.fillRect(startPoint, this.bounds.y, this.dashWidth, this.thickness);
				g.fillRect(startPoint, this.bounds.y + this.bounds.height - this.thickness,
						this.dashWidth, this.thickness);
			}
			for (int i = 0; i <= numHigh; ++i) {
				g.setColor(this.scheme.getColor(i));
				startPoint = this.bounds.y + this.dashHeight * i;
				g.fillRect(this.bounds.x, startPoint, this.thickness, this.dashHeight);
				g.fillRect(this.bounds.x + this.bounds.width - this.thickness, startPoint,
						this.thickness, this.dashHeight);
			}
			if (shape != null) {
				shape.add(new Area(new Rectangle(this.bounds.x, this.bounds.y, this.thickness,
						this.bounds.height)));
				shape.add(new Area(new Rectangle(this.bounds.x, this.bounds.y, this.bounds.width,
						this.thickness)));
				shape.add(new Area(new Rectangle(
						this.bounds.x + this.bounds.width - this.thickness, this.bounds.y,
						this.thickness, this.bounds.height)));
				shape.add(new Area(new Rectangle(this.bounds.x, this.bounds.y + this.bounds.height
						- this.thickness, this.bounds.width, this.thickness)));
			}
		}

	}

	private List<IHighlight> highlights = new ArrayList<HighlightScreen.IHighlight>();
	private Timer timer = null;
	private Transparency isopaque = Transparency.None;

	public HighlightScreen() {
		super(null, findGraphicsConfiguration());

		try {
			this.setBackground(new Color(0, 0, 0, 0));
			GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice();
			if (device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
				this.isopaque = Transparency.TRANSLUCENT;
			} else if (device
					.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
				this.isopaque = Transparency.PERPIXEL_TRANSLUCENT;
			}
		} catch (Throwable e) {}
		if (this.isopaque != Transparency.None) {
			Rectangle virtualBounds = new Rectangle();
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			for (int j = 0; j < gs.length; j++) {
				GraphicsDevice gd = gs[j];
				GraphicsConfiguration[] gc = gd.getConfigurations();
				for (int i = 0; i < gc.length; i++) {
					virtualBounds = virtualBounds.union(gc[i].getBounds());
				}
			}
			this.setBounds(virtualBounds);

			this.setAlwaysOnTop(true);
			this.setVisible(true);
			this.timer = new Timer("HighlightScreen", true);
			this.timer.schedule(new TimerTask() {
				@Override
				public void run() {
					HighlightScreen.this.showHighlights();
				}
			}, new Date(), 100);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.timer.cancel();

	}

	private static GraphicsConfiguration findGraphicsConfiguration() {
		try {
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

			GraphicsDevice[] devices = env.getScreenDevices();
			if (devices.length > 0) {
				if (devices[0].getDefaultConfiguration().isTranslucencyCapable()) {
					return devices[0].getDefaultConfiguration();
				}
				GraphicsConfiguration[] configs = devices[0].getConfigurations();
				for (int j = 0; j < configs.length; j++) {
					if (configs[j].isTranslucencyCapable()) {
						return configs[j];
					}
				}
			}
		} catch (Throwable e) {}
		return null;
	}

	@Override
	public void paint(Graphics g) {
		((Graphics2D) g).setBackground(new Color(0, 0, 0, 0));
		g.clearRect(0, 0, getWidth(), getHeight());
		long millis = System.currentTimeMillis() + 500;

		if (this.isopaque != Transparency.None) {
			Area shape = this.isopaque == Transparency.PERPIXEL_TRANSLUCENT ? new Area(
					new Rectangle(0, 0, 1, 1)) : null;
			for (int p = 0; p < this.highlights.size(); ++p) {
				if (System.currentTimeMillis() > millis) {
					this.highlights.clear();
					break;
				}
				try {
					IHighlight h = this.highlights.get(p);
					if (h != null)
						h.paint(g, shape);
				} catch (Exception e) {}
			}
			if (this.isopaque == Transparency.PERPIXEL_TRANSLUCENT) {
				this.setShape(shape);
			}
		}

	}

	protected void showHighlights() {
		long millis = System.currentTimeMillis();
		boolean need_repaint = false;
		try {
			for (int i = 0; i < this.highlights.size(); ++i) {
				IHighlight h = this.highlights.get(i);
				if (h != null && h.getMillis() < millis) {
					this.highlights.remove(i);
					--i;
					need_repaint = true;
				}
			}
		} catch (Exception e) {}
		if (this.highlights.size() == 0 && this.isVisible()) {
			this.off();
			need_repaint = false;
		}
		if (need_repaint) {
			this.repaint();
		}
	}

	public void addHighlight(HighlightColor scheme, Rectangle... rects) {
		for (Rectangle rect : rects) {
			this.addHighlight(new Highlight(rect, scheme));
		}
	}

	public void addHighlight(IHighlight highlight) {
		this.highlights.add(highlight);
		if (!this.isVisible()) {
			this.on();
		} else {
			this.repaint();
		}

	}

	public void off() {
		this.setVisible(false);
	}

	public void on() {
		this.setVisible(true);
	}

}
