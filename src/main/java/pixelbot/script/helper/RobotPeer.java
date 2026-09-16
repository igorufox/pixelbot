package pixelbot.script.helper;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

import pixelbot.components.generic.HighlightScreen.HighlightColor;

public class RobotPeer implements IPeer {

	private Robot robot;
	private Rectangle rect_work_area;
	private int[] pixels = null;
	private IOHelper helper;

	public RobotPeer(IOHelper helper) {
		this.helper = helper;
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			helper.log(Level.SEVERE, "Robot is unavailable: " + e);
		}
	}

	@Override
	public void dispose() {}

	@Override
	public void grabScreen(Rectangle rect) {
		try {
			if (rect.width < 0)
				rect.width = 0;
			if (rect.height < 0)
				rect.height = 0;
			Rectangle abs_rect = new Rectangle(rect);
			abs_rect.translate(this.rect_work_area.x, this.rect_work_area.y);
			this.helper.highlightOff();
			int[] new_pixels = this.getRGBPixels(abs_rect);
			this.helper.highlightOn();

			for (int i = 0; i < new_pixels.length; ++i) {
				int x = i % rect.width;
				int y = i / rect.width;
				this.pixels[rect.x + x + this.rect_work_area.width * (rect.y + y)] = new_pixels[i];
			}

			if (this.helper.isHighlightElements()) {
				this.helper.highlightRegion(HighlightColor.scheme4, rect);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.helper.log(Level.SEVERE, e.toString());
		}
	}

	@Override
	public void grabScreen() {
		try {
			this.helper.highlightOff();
			this.pixels = this.getRGBPixels(this.rect_work_area);
			this.helper.highlightOn();
			if (this.helper.isHighlightElements()) {
				this.helper.highlightRegion(HighlightColor.scheme4, new Rectangle(
						this.rect_work_area.getSize()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.helper.log(Level.SEVERE, e.toString());
		}
	}

	/**
	 * Reads a screen rectangle as a packed ARGB array. This used to reach into the internal
	 * java.awt.peer.RobotPeer; that package is gone since Java 9, so it goes through the public
	 * Robot API instead.
	 */
	private int[] getRGBPixels(Rectangle rect) {
		if (rect.width <= 0 || rect.height <= 0) {
			return new int[0];
		}
		BufferedImage image = this.robot.createScreenCapture(rect);
		return image.getRGB(0, 0, rect.width, rect.height, null, 0, rect.width);
	}

	@Override
	public int[] getPixels() {
		return this.pixels;
	}

	@Override
	public void resetWorkingArea() {
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

		this.rect_work_area = virtualBounds;
	}

	@Override
	public void setWorkingArea(Rectangle r) {
		this.rect_work_area = r;

	}

	@Override
	public Rectangle getWorkingArea() {
		return this.rect_work_area.getBounds();
	}

	@Override
	public void mousePress(int button) {
		this.robot.mousePress(RobotPeer.toMask(button));
	}

	@Override
	public void mouseRelease(int button) {
		this.robot.mouseRelease(RobotPeer.toMask(button));
	}

	private static int toMask(int button) {
		int int_button = InputEvent.BUTTON1_DOWN_MASK;
		switch (button) {
		case 1:
			int_button = InputEvent.BUTTON1_DOWN_MASK;
			break;
		case 2:
			int_button = InputEvent.BUTTON2_DOWN_MASK;
			break;
		case 3:
			int_button = InputEvent.BUTTON3_DOWN_MASK;
			break;
		default:
			int_button = InputEvent.BUTTON1_DOWN_MASK;
			break;
		}
		return int_button;
	}

	@Override
	public void mouseMove(int x, int y) {
		Point p = this.toDisplay2d(new Point(x, y));
		this.robot.mouseMove(p.x, p.y);
	}

	@Override
	public Point3d getMousePosition() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		return new Point3d(p.x - this.rect_work_area.x, p.y - this.rect_work_area.y, 0);
	}

	@Override
	public Point3d toDisplay3d(Point p) {
		return new Point3d(p.x + this.rect_work_area.x, p.y + this.rect_work_area.y, 0);
	}

	@Override
	public Point toDisplay2d(Point p) {
		return new Point(p.x + this.rect_work_area.x, p.y + this.rect_work_area.y);
	}

	@Override
	public void keyPress(int keycode) {
		this.robot.keyPress(keycode);
	}

	@Override
	public void keyRelease(int keycode) {
		this.robot.keyRelease(keycode);
	}

}
