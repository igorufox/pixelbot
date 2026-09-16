package pixelbot.script.helper;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.List;
import java.util.logging.Level;

import javax.sound.sampled.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;

import pixelbot.captcha.algorithm.Algorithm.Result;
import pixelbot.captcha.solver.*;
import pixelbot.components.generic.*;
import pixelbot.components.generic.HighlightScreen.HighlightColor;
import pixelbot.components.generic.HighlightScreen.IHighlight;
import pixelbot.elements.*;
import pixelbot.elements.IElementDescriptor.ColorType;
import pixelbot.json.JSONValue;
import pixelbot.log.ScriptLogLevel;
import pixelbot.misc.*;
import pixelbot.script.general.*;
import pixelbot.script.general.ScriptManager.StatisticsEvent;
import pixelbot.script.helper.IPeer.Point3d;
import pixelbot.script.misc.*;
import pixelbot.script.scope.general.*;

public class IOHelper {
	public Elements elements;
	private IOSettings settings;
	private ScriptManager manager = null;
	private IPeer peer = new RobotPeer(this);

	private HighlightScreen highlight = new HighlightScreen();

	public IOHelper(ScriptManager manager, IOSettings settings) {
		this.manager = manager;
		this.settings = settings;

		this.browser.resetWorkingArea();
	}

	public void grabScreen(Rectangle rect) {
		this.peer.grabScreen(rect);
	}

	public void grabScreen() {
		this.peer.grabScreen();
	}

	public BufferedImage getScreen() {
		this.grabScreen();

		BufferedImage result = new BufferedImage(this.browser.get().width,
				this.browser.get().height, BufferedImage.TYPE_INT_ARGB);
		result.setRGB(0, 0, this.browser.get().width, this.browser.get().height, this.getPixels(),
				0, this.browser.get().width);
		return result;
	}

	public BufferedImage getScreen_() {
		BufferedImage result = new BufferedImage(this.browser.get().width,
				this.browser.get().height, BufferedImage.TYPE_INT_ARGB);
		result.setRGB(0, 0, this.browser.get().width, this.browser.get().height, this.getPixels(),
				0, this.browser.get().width);
		return result;
	}

	public BufferedImage getScreen(Rectangle rectangle) {
		this.grabScreen(rectangle);

		BufferedImage result = new BufferedImage(this.browser.get().width,
				this.browser.get().height, BufferedImage.TYPE_INT_ARGB);
		result.setRGB(0, 0, this.browser.get().width, this.browser.get().height, this.getPixels(),
				0, this.browser.get().width);
		return result;
	}

	public int getPixel(int x, int y) {
		return this.getPixels()[x + y * this.browser.get().width];
	}

	public int[] getPixels() {
		return this.peer.getPixels();
	}

	public class Browser {

		public void resetWorkingArea() {
			IOHelper.this.peer.resetWorkingArea();
		}

		public void setWorkingArea(Rectangle r) {
			IOHelper.this.peer.setWorkingArea(r);
			if (IOHelper.this.settings.isHighlightElements()) {
				IOHelper.this.highlightRegion(HighlightColor.scheme2, new Rectangle(0, 0, r.width,
						r.height));
			}
			IOHelper.this.grabScreen();
		}

		public Rectangle get() {
			return IOHelper.this.peer.getWorkingArea();
		}

		public boolean detect() {
			Rectangle rect = null;

			this.resetWorkingArea();
			IOHelper.this.grabScreen();
			Rectangle t_l = IOHelper.this.findElement("интерфейс.Верхний левый угол");
			if (t_l == null) {
				IOHelper.this.log(ScriptLogLevel.Fatal,
						"Не могу найти верхний левый угол приложения");
				throw new RuntimeException("Initialization error");
			}
			Rectangle b_r = IOHelper.this.findElement("интерфейс.Нижний правый угол");
			if (b_r == null) {
				IOHelper.this.log(ScriptLogLevel.Fatal,
						"Не могу найти нижний правый угол приложения");
				throw new RuntimeException("Initialization error");
			}

			rect = new Rectangle(t_l.x, t_l.y, b_r.x - t_l.x + b_r.width, b_r.y - t_l.y
					+ b_r.height);

			this.setWorkingArea(rect);
			return true;
		}
	}

	public final Browser browser = new Browser();

	public ElementDescriptorBase learnElement(BufferedImage image, ColorType color_type,
			int selected_color) {

		ElementDescriptorBase ed = new ElementDescriptorBase();
		if (image != null) {
			ed.setDimensions(image.getWidth(), image.getHeight());

			int[] deepImage = new int[image.getWidth() * image.getHeight()];
			PixelGrabber pg = new PixelGrabber(image, 0, 0, image.getWidth(), image.getHeight(),
					deepImage, 0, image.getWidth());
			try {
				pg.grabPixels();
			} catch (InterruptedException e) {}
			int idx = 0;
			for (int j = 0; j < ed.getHeight(); j++) {

				for (int i = 0; i < ed.getWidth(); i++) {
					idx = j * ed.getWidth() + i;

					switch (color_type) {
					case CT_DISPERSION:
						Color cur_color = new Color(deepImage[idx]);
						Color sel_color = new Color(selected_color);

						int diff = Math.abs(cur_color.getBlue() - sel_color.getBlue())
								+ Math.abs(cur_color.getRed() - sel_color.getRed())
								+ Math.abs(cur_color.getGreen() - sel_color.getGreen());
						if (diff < 70) {
							ed.addPixel(i, j, selected_color);
						}
						break;
					case CT_MONOTON:
					case CT_UNICOLOR:
						if (deepImage[idx] == selected_color) {
							ed.addPixel(i, j, selected_color);
						}
						break;
					case CT_MULTICOLOR:
					case CT_HISTOGRAMM:
						ed.addPixel(i, j, deepImage[idx]);
						break;
					}

				}
			}
		}
		return ed;
	}

	public ElementDescriptor grabElement(int x, int y, int width, int heigth) {
		ElementDescriptor ed = new ElementDescriptor(width, heigth,
				EnumSet.allOf(IElementDescriptor.Locale.class), ColorType.CT_MULTICOLOR, 0,
				new Date());
		for (int i = 0; i < ed.getWidth(); ++i) {
			for (int j = 0; j < ed.getHeight(); ++j) {
				ed.addPixel(i, j, this.getPixel(i + x, j + y));
			}
		}
		return ed;
	}

	public List<Rectangle> findElements(IElementDescriptor ed, Rectangle r, int limit) {
		if (ed instanceof ElementDescriptor) {
			return this.findElements((ElementDescriptorBase) ed, ed.getColorType(), r, limit);
		} else if (ed instanceof ElementList) {
			List<Rectangle> result = new ArrayList<Rectangle>();
			for (ElementDescriptorBase e : (ElementList) ed) {
				result.addAll(this.findElements(e, ((ElementList) ed).getColorType(), r, limit));
				if (result.size() >= limit)
					break;
			}
			if (result.size() > limit) {
				result = result.subList(0, limit - 1);
			}

			return result;
		}
		System.err.println("findElements " + (ed == null ? "<null>" : ed.getClass().getName()));
		return Arrays.asList(new Rectangle[] {});
	}

	public List<Rectangle> findElements(ElementDescriptorBase ed, ColorType color_type,
			Rectangle r, int limit) {
		ArrayList<Rectangle> result = new ArrayList<Rectangle>();

		if (this.settings.isHighlightElements()) {
			this.highlightRegion(HighlightColor.scheme3, r);
		}

		if (ed == null) {
			return result;
		}
		if (!ed.getLocale().contains(this.settings.getLocaleEnum())) {
			return result;
		}
		if (color_type == ColorType.CT_HISTOGRAMM) {

			HistogramDesc hd = new HistogramDesc();
			for (PixelDescriptor pd : ed) {
				hd.addColor(pd.pixel_color);
				if (hd.getColorCount() > 9) {
					break;
				}
			}

			try {
				// for (int j = this.rect_work_area.y + r.y; j < this.rect_work_area.y +
				// r.height; j
				// += ed.height / 2)
				// for (int i = this.rect_work_area.x + r.x; i < this.rect_work_area.x +
				// r.width; i
				// += ed.width / 2) {
				for (int j = r.y; j < r.y + r.height; j += ed.getHeight() / 2)
					for (int i = r.x; i < r.x + r.width; i += ed.getWidth() / 2) {
						hd.clearMatches();

						out: for (int jj = 0; jj < ed.getHeight(); jj++) {
							for (int ii = 0; ii < ed.getWidth(); ii++) {
								int col = this.getPixel(i + ii, j + jj);
								hd.matchColor(col);
								if (hd.isMatches()) {
									break out;
								}
							}
						}

						if (hd.isMatches()) {
							result.add(new Rectangle(i, j, ed.getWidth(), ed.getHeight()));
						}
					}
			} catch (Exception e) {}

			for (int i = 1; i < result.size(); i++) {
				Rectangle fl1 = result.get(i);
				for (int j = 0; j < i; j++) {
					Rectangle fl2 = result.get(j);
					if ((Math.abs(fl1.x - fl2.x) <= 1.5 * ed.getWidth())
							&& (Math.abs(fl1.y - fl2.y) <= 1.5 * ed.getHeight())) {
						fl1.x = (fl1.x + fl2.x >> 1);
						fl1.y = (fl1.y + fl2.y >> 1);
						result.remove(i);
						result.remove(j);
						result.add(fl1);
						i--;
						break;
					}
				}
			}
		} else if ((color_type == ColorType.CT_MULTICOLOR) || (color_type == ColorType.CT_UNICOLOR)) {
			try {
				int length = this.browser.get().width * (r.y + r.height) + r.x + r.width;
				for (int i = this.browser.get().width * r.y + r.x; i < length; i++) {
					if (i % this.browser.get().width >= r.x + r.width) {
						i += this.browser.get().width - r.width;
						if (i >= length)
							break;
					}
					boolean found = true;
					for (PixelDescriptor pd : ed) {
						if (i + pd.offset_x + pd.offset_y * this.browser.get().width > length
								|| pd.pixel_color != this.getPixel(i + pd.offset_x, pd.offset_y)) {
							found = false;
							break;
						}
					}
					if (found) {
						result.add(new Rectangle(i % this.browser.get().width, i
								/ this.browser.get().width, ed.getWidth(), ed.getHeight()));
						if (limit <= result.size()) {
							break;
						}
					}
				}
			} catch (Exception e) {}
		} else if (color_type == ColorType.CT_MONOTON) {

			try {
				int length = this.browser.get().width * (r.y + r.height) + r.x + r.width;
				for (int i = this.browser.get().width * r.y + r.x; i < length; i++) {
					if (i % this.browser.get().width >= r.x + r.width) {
						i += this.browser.get().width - r.width;
						if (i >= length)
							break;
					}
					boolean found = true;

					int unic_color = 0;
					int last_offset_x = 0;
					int last_offset_y = 0;
					// int unic_color = this.pixels[i + pd.offset_x + pd.offset_y
					// * this.rect_work_area.width];
					// int last_offset_x = pd.offset_x;
					// int last_offset_y = pd.offset_y;
					for (PixelDescriptor pd : ed) {
						if (unic_color == 0) {
							unic_color = this.getPixel(i + pd.offset_x, pd.offset_y);
							last_offset_x = pd.offset_x;
							last_offset_y = pd.offset_y;
						}

						if (unic_color != this.getPixel(i + pd.offset_x, pd.offset_y)) {
							found = false;
							break;
						}

						if ((last_offset_y == pd.offset_y) && (pd.offset_x - last_offset_x > 1)) {

							if (unic_color == this.getPixel(i + pd.offset_x - 1, pd.offset_y)) {
								found = false;
								break;
							}

						}

						last_offset_x = pd.offset_x;
						last_offset_y = pd.offset_y;
					}

					if (found) {
						result.add(new Rectangle(i % this.browser.get().width, i
								/ this.browser.get().width, ed.getWidth(), ed.getHeight()));
						if (limit <= result.size()) {
							break;
						}
					}
				}
			} catch (Exception e) {}
		} else if (color_type == ColorType.CT_DISPERSION) {
			try {

				int length = this.browser.get().width * (r.y + r.height) + r.x + r.width;
				for (int i = this.browser.get().width * r.y + r.x; i < length; i++) {
					if (i % this.browser.get().width >= r.x + r.width) {
						i += this.browser.get().width - r.width;
						if (i >= length)
							break;
					}
					boolean found = true;
					for (PixelDescriptor pd : ed) {
						Color cur_color = new Color(this.getPixel(i + pd.offset_x, pd.offset_y));
						Color sel_color = new Color(pd.pixel_color);

						int diff = Math.abs(cur_color.getBlue() - sel_color.getBlue())
								+ Math.abs(cur_color.getRed() - sel_color.getRed())
								+ Math.abs(cur_color.getGreen() - sel_color.getGreen());
						if (diff >= 70) {// ?
							found = false;
							break;
						}
					}
					if (found) {
						result.add(new Rectangle(i % this.browser.get().width, i
								/ this.browser.get().width, ed.getWidth(), ed.getHeight()));
						if (limit <= result.size()) {
							break;
						}
					}
				}

			} catch (Exception e) {}

		}
		if (this.settings.isHighlightElements()) {
			this.highlightRegion(HighlightColor.scheme1, result.toArray(new Rectangle[] {}));
		}
		return result;
	}

	public List<Rectangle> findElements(IElementDescriptor ed, Rectangle r) {
		return findElements(ed, r, Integer.MAX_VALUE);
	}

	public List<Rectangle> findElements(String key, Rectangle r) {
		return findElements(this.elements.get(key), r);
	}

	public List<Rectangle> findElements(String key) {
		return findElements(key, new Rectangle(this.browser.get().getSize()));
	}

	public Rectangle findElement(IElementDescriptor ed, Rectangle r) {
		List<Rectangle> res = findElements(ed, r, 1);
		if (res.size() != 0) {
			return res.get(0);
		}
		return null;
	}

	public Rectangle findElement(String key, Rectangle r) {
		if (!this.elements.containsKey(key)) {
			System.err.println("findElement key <" + key + "> not found");
			return null;
		}
		return this.findElement(this.elements.get(key), r);
	}

	public Rectangle findElement(String key) {
		if (!this.elements.containsKey(key)) {
			System.err.println("findElement key <" + key + "> not found");
			return null;
		}
		return this
				.findElement(this.elements.get(key), new Rectangle(this.browser.get().getSize()));

	}

	public List<IClassifierItem> getElementsDesc(String category) {
		ArrayList<IClassifierItem> al = new ArrayList<IClassifierItem>();
		Elements me = this.elements;
		for (Map.Entry<String, IElementDescriptor> entry : me.entrySet()) {
			if (entry.getKey().startsWith(category + ".")) {
				al.add(new ScopeClassifierItem(entry.getValue().getOrder(), entry.getKey()
						.substring(category.length() + 1)));
			}
		}
		Collections.sort(al);
		return al;
	}

	public List<IClassifierItem> getElementsDesc(String category, int level) {
		ArrayList<IClassifierItem> al = new ArrayList<IClassifierItem>();
		HashSet<IClassifierItem> set = new HashSet<IClassifierItem>();
		Elements me = this.elements;
		outer: for (Map.Entry<String, IElementDescriptor> entry : me.entrySet()) {
			if (entry.getKey().startsWith(category + ".")) {

				String key = entry.getKey().substring(category.length() + 1);
				StringBuffer sb = new StringBuffer();
				for (String t : Arrays.copyOf(key.split("\\."), level)) {
					if (t == null)
						continue outer;
					sb.append(t).append(".");
				}
				if (sb.length() != 0) {
					sb.setLength(sb.length() - 1);
				}
				set.add(new ScopeClassifierItem(0, sb.toString()));
			}
		}
		al.addAll(set);
		Collections.sort(al);
		return al;
	}

	private class HistogramDesc {
		private int[] colors = new int[10];
		private short colorscount = 0;
		private boolean[] matches = new boolean[10];
		private short matchescount = 0;

		public HistogramDesc() {}

		private int getColorIndex(int color) {
			return Arrays.binarySearch(this.colors, color);
		}

		public void clearMatches() {
			for (int i = 0; i < 10; ++i) {
				this.matches[i] = false;

			}
			this.matchescount = 0;
		}

		public void addColor(int color) {
			if (this.getColorIndex(color) < 0) {
				this.colors[this.colorscount++] = color;
				Arrays.sort(this.colors);
			}
		}

		public int getColorCount() {
			return this.colorscount;
		}

		public void matchColor(int color) {
			int index = this.getColorIndex(color);
			if (index > 0) {
				if (!this.matches[index]) {
					this.matches[index] = true;
					this.matchescount++;
				}
			}

		}

		public boolean isMatches() {
			return this.matchescount > 3;
		}
	}

	public int square(int color_min, int color_max, Rectangle r) {
		int sum = 0;
		try {
			if (this.settings.isHighlightElements()) {
				this.highlightRegion(HighlightColor.scheme3, r);
			}
			int length = this.browser.get().width * (r.y + r.height) + r.x + r.width;
			for (int i = this.browser.get().width * r.y + r.x; i < length; i++) {
				if (i % this.browser.get().width >= r.x + r.width) {
					i += this.browser.get().width - r.width;
					if (i >= length)
						break;
				}
				int col = this.getPixels()[i];
				int col_min = color_min;
				int col_max = color_max;
				if (((col & 0xFF) < (col_min & 0xFF)) || ((col & 0xFF) > (col_max & 0xFF)))
					continue;
				col >>= 8;
				col_min >>= 8;
				col_max >>= 8;
				if (((col & 0xFF) < (col_min & 0xFF)) || ((col & 0xFF) > (col_max & 0xFF)))
					continue;
				col >>= 8;
				col_min >>= 8;
				col_max >>= 8;
				if (((col & 0xFF) < (col_min & 0xFF)) || ((col & 0xFF) > (col_max & 0xFF)))
					continue;
				sum++;

			}

		} catch (Exception e) {}
		return sum;

	}

	public Rectangle getRectangle(int x, int y, int width, int height) {
		return new Rectangle(x, y, width, height);
	}

	public void highlightRegion(IHighlight highlight) {
		this.highlight.addHighlight(highlight);
	}

	public void highlightRegion(String scheme, Rectangle... rects) {
		this.highlightRegion(HighlightColor.valueOf(scheme), rects);
	}

	public void highlightRegion(HighlightColor scheme, Rectangle... rects) {
		List<Rectangle> l = new ArrayList<Rectangle>();
		for (Rectangle r : rects) {
			l.add(new Rectangle(this.peer.toDisplay2d(r.getLocation()), r.getSize()));
		}
		this.highlight.addHighlight(scheme, l.toArray(new Rectangle[] {}));
	}

	void highlightOff() {
		this.highlight.off();
	}

	void highlightOn() {
		this.highlight.on();
	}

	boolean isHighlightElements() {
		return this.settings.isHighlightElements();
	}

	public boolean mouseMove(int x, int y) throws InterruptedException {
		Point3d expect_loc = this.peer.getMousePosition();
		Point3d actual_loc;
		int delta, deltax, deltay;

		while (true) {
			actual_loc = this.peer.getMousePosition();
			if (this.settings.isCheckUserActivity()) {
				if (!actual_loc.equals(expect_loc)) {
					throw new UserInterruptionException();
				}
			}
			deltax = x - actual_loc.x;
			deltay = y - actual_loc.y;
			delta = Math.max(Math.abs(deltax), Math.abs(deltay));
			if (delta < this.settings.getMouseMovingSpeed()) {
				this.peer.mouseMove(x, y);
				Thread.sleep(this.settings.getMousePauseAfterPosition());
				return true;
			}
			this.peer.mouseMove(
					actual_loc.x
							+ (int) ((Math.random() + 1 / 2) * this.settings.getMouseMovingSpeed()
									* deltax / delta), actual_loc.y
							+ (int) ((Math.random() + 1 / 2) * this.settings.getMouseMovingSpeed()
									* deltay / delta));
			expect_loc = this.peer.getMousePosition();

			Thread.sleep(2);
		}
	}

	public void mouseClick(int count) throws InterruptedException {
		this.highlight.off();
		for (int i = 0; i < count; i++) {
			this.peer.mousePress(1);
			Thread.sleep(this.settings.getMouseClickDuration());
			this.peer.mouseRelease(1);
			Thread.sleep(this.settings.getMousePauseAfterClick());
		}
		this.highlight.on();
	}

	public void mousePress(int button) throws InterruptedException {
		this.highlight.off();
		this.peer.mousePress(button);
	}

	public void mouseRelease(int button) throws InterruptedException {
		this.highlight.off();
		this.peer.mouseRelease(button);
		this.highlight.on();
	}

	public void keyPress(int keycode) throws InterruptedException {
		this.peer.keyPress(keycode);
	}

	public void keyRelease(int keycode) throws InterruptedException {
		this.peer.keyRelease(keycode);
	}

	public void keyClick(int keycode, int modifiers) throws InterruptedException {
		if ((modifiers & Event.CTRL_MASK) != 0) {
			this.keyPress(KeyEvent.VK_CONTROL);
		}
		if ((modifiers & Event.SHIFT_MASK) != 0) {
			this.keyPress(KeyEvent.VK_SHIFT);
		}
		if ((modifiers & Event.ALT_MASK) != 0) {
			this.keyPress(KeyEvent.VK_ALT);
		}
		this.keyPress(keycode);
		this.keyRelease(keycode);
		if ((modifiers & Event.ALT_MASK) != 0) {
			this.keyRelease(KeyEvent.VK_ALT);
		}
		if ((modifiers & Event.SHIFT_MASK) != 0) {
			this.keyRelease(KeyEvent.VK_SHIFT);
		}
		if ((modifiers & Event.CTRL_MASK) != 0) {
			this.keyRelease(KeyEvent.VK_CONTROL);
		}
	}

	public void pasteText(String txt) throws InterruptedException {
		StringSelection stringSelection = new StringSelection(txt);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, new ClipboardOwner() {
			@Override
			public void lostOwnership(Clipboard clipboard, Transferable contents) {}
		});
		this.keyClick(KeyEvent.VK_V, Event.CTRL_MASK);
	}

	public void printText(String txt) throws InterruptedException {
		for (char c : txt.toCharArray()) {
			switch (c) {
			case 'a':
				keyClick(KeyEvent.VK_A, 0);
				break;
			case 'b':
				keyClick(KeyEvent.VK_B, 0);
				break;
			case 'c':
				keyClick(KeyEvent.VK_C, 0);
				break;
			case 'd':
				keyClick(KeyEvent.VK_D, 0);
				break;
			case 'e':
				keyClick(KeyEvent.VK_E, 0);
				break;
			case 'f':
				keyClick(KeyEvent.VK_F, 0);
				break;
			case 'g':
				keyClick(KeyEvent.VK_G, 0);
				break;
			case 'h':
				keyClick(KeyEvent.VK_H, 0);
				break;
			case 'i':
				keyClick(KeyEvent.VK_I, 0);
				break;
			case 'j':
				keyClick(KeyEvent.VK_J, 0);
				break;
			case 'k':
				keyClick(KeyEvent.VK_K, 0);
				break;
			case 'l':
				keyClick(KeyEvent.VK_L, 0);
				break;
			case 'm':
				keyClick(KeyEvent.VK_M, 0);
				break;
			case 'n':
				keyClick(KeyEvent.VK_N, 0);
				break;
			case 'o':
				keyClick(KeyEvent.VK_O, 0);
				break;
			case 'p':
				keyClick(KeyEvent.VK_P, 0);
				break;
			case 'q':
				keyClick(KeyEvent.VK_Q, 0);
				break;
			case 'r':
				keyClick(KeyEvent.VK_R, 0);
				break;
			case 's':
				keyClick(KeyEvent.VK_S, 0);
				break;
			case 't':
				keyClick(KeyEvent.VK_T, 0);
				break;
			case 'u':
				keyClick(KeyEvent.VK_U, 0);
				break;
			case 'v':
				keyClick(KeyEvent.VK_V, 0);
				break;
			case 'w':
				keyClick(KeyEvent.VK_W, 0);
				break;
			case 'x':
				keyClick(KeyEvent.VK_X, 0);
				break;
			case 'y':
				keyClick(KeyEvent.VK_Y, 0);
				break;
			case 'z':
				keyClick(KeyEvent.VK_Z, 0);
				break;
			case 'A':
				keyClick(KeyEvent.VK_A, InputEvent.SHIFT_MASK);
				break;
			case 'B':
				keyClick(KeyEvent.VK_B, InputEvent.SHIFT_MASK);
				break;
			case 'C':
				keyClick(KeyEvent.VK_C, InputEvent.SHIFT_MASK);
				break;
			case 'D':
				keyClick(KeyEvent.VK_D, InputEvent.SHIFT_MASK);
				break;
			case 'E':
				keyClick(KeyEvent.VK_E, InputEvent.SHIFT_MASK);
				break;
			case 'F':
				keyClick(KeyEvent.VK_F, InputEvent.SHIFT_MASK);
				break;
			case 'G':
				keyClick(KeyEvent.VK_G, InputEvent.SHIFT_MASK);
				break;
			case 'H':
				keyClick(KeyEvent.VK_H, InputEvent.SHIFT_MASK);
				break;
			case 'I':
				keyClick(KeyEvent.VK_I, InputEvent.SHIFT_MASK);
				break;
			case 'J':
				keyClick(KeyEvent.VK_J, InputEvent.SHIFT_MASK);
				break;
			case 'K':
				keyClick(KeyEvent.VK_K, InputEvent.SHIFT_MASK);
				break;
			case 'L':
				keyClick(KeyEvent.VK_L, InputEvent.SHIFT_MASK);
				break;
			case 'M':
				keyClick(KeyEvent.VK_M, InputEvent.SHIFT_MASK);
				break;
			case 'N':
				keyClick(KeyEvent.VK_N, InputEvent.SHIFT_MASK);
				break;
			case 'O':
				keyClick(KeyEvent.VK_O, InputEvent.SHIFT_MASK);
				break;
			case 'P':
				keyClick(KeyEvent.VK_P, InputEvent.SHIFT_MASK);
				break;
			case 'Q':
				keyClick(KeyEvent.VK_Q, InputEvent.SHIFT_MASK);
				break;
			case 'R':
				keyClick(KeyEvent.VK_R, InputEvent.SHIFT_MASK);
				break;
			case 'S':
				keyClick(KeyEvent.VK_S, InputEvent.SHIFT_MASK);
				break;
			case 'T':
				keyClick(KeyEvent.VK_T, InputEvent.SHIFT_MASK);
				break;
			case 'U':
				keyClick(KeyEvent.VK_U, InputEvent.SHIFT_MASK);
				break;
			case 'V':
				keyClick(KeyEvent.VK_V, InputEvent.SHIFT_MASK);
				break;
			case 'W':
				keyClick(KeyEvent.VK_W, InputEvent.SHIFT_MASK);
				break;
			case 'X':
				keyClick(KeyEvent.VK_X, InputEvent.SHIFT_MASK);
				break;
			case 'Y':
				keyClick(KeyEvent.VK_Y, InputEvent.SHIFT_MASK);
				break;
			case 'Z':
				keyClick(KeyEvent.VK_Z, InputEvent.SHIFT_MASK);
				break;
			case '`':
				keyClick(KeyEvent.VK_BACK_QUOTE, 0);
				break;
			case '0':
				keyClick(KeyEvent.VK_0, 0);
				break;
			case '1':
				keyClick(KeyEvent.VK_1, 0);
				break;
			case '2':
				keyClick(KeyEvent.VK_2, 0);
				break;
			case '3':
				keyClick(KeyEvent.VK_3, 0);
				break;
			case '4':
				keyClick(KeyEvent.VK_4, 0);
				break;
			case '5':
				keyClick(KeyEvent.VK_5, 0);
				break;
			case '6':
				keyClick(KeyEvent.VK_6, 0);
				break;
			case '7':
				keyClick(KeyEvent.VK_7, 0);
				break;
			case '8':
				keyClick(KeyEvent.VK_8, 0);
				break;
			case '9':
				keyClick(KeyEvent.VK_9, 0);
				break;
			case '-':
				keyClick(KeyEvent.VK_MINUS, 0);
				break;
			case '=':
				keyClick(KeyEvent.VK_EQUALS, 0);
				break;
			case '~':
				keyClick(KeyEvent.VK_BACK_QUOTE, InputEvent.SHIFT_MASK);
				break;
			case '!':
				keyClick(KeyEvent.VK_EXCLAMATION_MARK, 0);
				break;
			case '@':
				keyClick(KeyEvent.VK_2, InputEvent.SHIFT_MASK);
				break;
			case '#':
				keyClick(KeyEvent.VK_NUMBER_SIGN, 0);
				break;
			case '$':
				keyClick(KeyEvent.VK_DOLLAR, 0);
				break;
			case '%':
				keyClick(KeyEvent.VK_5, InputEvent.SHIFT_MASK);
				break;
			case '^':
				keyClick(KeyEvent.VK_CIRCUMFLEX, 0);
				break;
			case '&':
				keyClick(KeyEvent.VK_AMPERSAND, 0);
				break;
			case '*':
				keyClick(KeyEvent.VK_ASTERISK, 0);
				break;
			case '(':
				keyClick(KeyEvent.VK_LEFT_PARENTHESIS, 0);
				break;
			case ')':
				keyClick(KeyEvent.VK_RIGHT_PARENTHESIS, 0);
				break;
			case '_':
				keyClick(KeyEvent.VK_UNDERSCORE, 0);
				break;
			case '+':
				keyClick(KeyEvent.VK_PLUS, 0);
				break;
			case '\t':
				keyClick(KeyEvent.VK_TAB, 0);
				break;
			case '\n':
				keyClick(KeyEvent.VK_ENTER, 0);
				break;
			case '[':
				keyClick(KeyEvent.VK_OPEN_BRACKET, 0);
				break;
			case ']':
				keyClick(KeyEvent.VK_CLOSE_BRACKET, 0);
				break;
			case '\\':
				keyClick(KeyEvent.VK_BACK_SLASH, 0);
				break;
			case '{':
				keyClick(KeyEvent.VK_OPEN_BRACKET, InputEvent.SHIFT_MASK);
				break;
			case '}':
				keyClick(KeyEvent.VK_CLOSE_BRACKET, InputEvent.SHIFT_MASK);
				break;
			case '|':
				keyClick(KeyEvent.VK_BACK_SLASH, InputEvent.SHIFT_MASK);
				break;
			case ';':
				keyClick(KeyEvent.VK_SEMICOLON, 0);
				break;
			case ':':
				keyClick(KeyEvent.VK_COLON, 0);
				break;
			case '\'':
				keyClick(KeyEvent.VK_QUOTE, 0);
				break;
			case '"':
				keyClick(KeyEvent.VK_QUOTEDBL, 0);
				break;
			case ',':
				keyClick(KeyEvent.VK_COMMA, 0);
				break;
			case '<':
				keyClick(KeyEvent.VK_LESS, 0);
				break;
			case '.':
				keyClick(KeyEvent.VK_PERIOD, 0);
				break;
			case '>':
				keyClick(KeyEvent.VK_GREATER, 0);
				break;
			case '/':
				keyClick(KeyEvent.VK_SLASH, 0);
				break;
			case '?':
				keyClick(KeyEvent.VK_SLASH, InputEvent.SHIFT_MASK);
				break;
			case ' ':
				keyClick(KeyEvent.VK_SPACE, 0);
				break;
			default:
				break;
			}
		}
	}

	public void log(String level, String msg) {
		this.log(Level.parse(level), msg);
	}

	public void log(Level level, String msg) {
		this.manager.log(level, msg);
	}

	public void statistics(String dest, Object data, int value) {
		this.manager.fireStatictics(new StatisticsEvent(dest, data, value));
	}

	public synchronized void playSound(final String file) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try (Clip clip = AudioSystem.getClip();
						InputStream is = new FileInputStream(new File(Tools.workdir, "media/"
								+ file));
						BufferedInputStream bis = new BufferedInputStream(is);
						AudioInputStream ais = AudioSystem.getAudioInputStream(bis);) {

					clip.open(ais);
					clip.start();
					while (clip.getMicrosecondLength() != clip.getMicrosecondPosition()) {
						Thread.sleep(100);
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}

	public IScopeMap loadConfiguration(String file_name) {
		try (Reader fr = new UnicodeReader(new FileInputStream(new File(file_name).isAbsolute()
				? new File(file_name) : new File(Tools.workdir, file_name)), "UTF-8")) {
			IScopeObject o = JSONScriptValue.parseForScope(this.manager, fr);
			return (IScopeMap) o;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveConfiguration(Map<String, Object> value, String file_name) {
		try (FileWriter fw = new FileWriter(new File(file_name).isAbsolute() ? new File(file_name)
				: new File(Tools.workdir, file_name))) {
			String encoded = JSONScriptValue.toJSONString(value);

			encoded = JSONValue.formatJs(encoded);
			fw.write(encoded);
		} catch (FileNotFoundException e) {} catch (IOException e) {}
	}

	public void saveAsConfiguration(Map<String, Object> value, List<Map<String, Object>> types) {
		final JFileChooser fc = new JFileChooser(".");
		fc.setAcceptAllFileFilterUsed(true);
		fc.setMultiSelectionEnabled(false);

		if (types != null) {
			for (Map<String, Object> type : types) {
				fc.addChoosableFileFilter(new FileNameExtensionFilter(type.get("name").toString(),
						new String[] { type.get("extension").toString() }));
			}
		}

		if (JFileChooser.APPROVE_OPTION == fc.showSaveDialog(null)) {
			File f = fc.getSelectedFile();
			FileFilter ff = fc.getFileFilter();
			if (ff instanceof FileNameExtensionFilter) {
				f = this.manager.addExtension(f, ((FileNameExtensionFilter) ff).getExtensions()[0]);
			}

			this.saveConfiguration(value, f.getAbsolutePath());
		}
	}

	/**
	 * Reassembles a tile captcha, returning the six tiles in the order they belong in.
	 *
	 * @param type the puzzle kind, see {@link CaptchaSolverFactory#produce}
	 * @return the ordered tiles, or null if the image holds no grid or the type is unknown
	 */
	public IElementDescriptor[] solveCaptcha(String type, IElementDescriptor raw, String param) {
		try {
			ElementDescriptor[] elems = CaptchaSolverFactory.parseDescriptor(raw.getImage());
			if (elems == null) {
				this.log(Level.FINE, "no captcha grid found in the given region");
				return null;
			}

			BaseCaptchaSolver solver = CaptchaSolverFactory.produce(type, null, elems);
			if (solver == null) {
				return null;
			}

			Result a = solver.solve();
			if (a == null || a.getA() == null) {
				this.log(Level.WARNING, "captcha solver returned no arrangement");
				return null;
			}

			IElementDescriptor[] result = new IElementDescriptor[6];
			for (int i = 0; i < 6; ++i) {
				result[i] = elems[a.getA()[i]];
			}
			return result;
		} catch (Exception e) {
			this.log(Level.SEVERE, "captcha solving failed: " + e);
			return null;
		}
	}

	private Map<String, IElementDescriptor> recognision_chars = null;

	public String recognizeText(byte color_min, byte color_max, Rectangle r) {
		this.recognision_chars = null;

		if (this.recognision_chars == null) {
			try {
				List<IClassifierItem> chars_t = this.getElementsDesc("интерфейс.Характеристики");
				this.recognision_chars = new HashMap<String, IElementDescriptor>();
				for (IClassifierItem key : chars_t) {
					if (key == null || key.getText().length() > 1)
						continue;
					this.recognision_chars.put(key.getText(),
							this.elements.get("интерфейс.Характеристики." + key.getText()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		BitSet bitmask = new BitSet();

		if (this.settings.isHighlightElements()) {
			this.highlightRegion(HighlightColor.scheme3, r);
		}
		try {
			int length = this.browser.get().width * (r.y + r.height) + r.x + r.width;
			for (int i = this.browser.get().width * r.y + r.x; i < length; i++) {
				if (i % this.browser.get().width >= r.x + r.width) {
					i += this.browser.get().width - r.width;
					if (i >= length)
						break;
				}
				int c = this.getPixels()[i];
				int cr = (c & 0xFF0000) >> 16;
				int cg = (c & 0xFF00) >> 8;
				int cb = c & 0xFF;
				if (cr != cb || cr != cg)
					continue;

				int col_min = color_min & 0xFF;
				int col_max = color_max & 0xFF;
				if ((cr < (col_min & 0xFF)) || (cr > (col_max & 0xFF)))
					continue;

				bitmask.set(i);

			}

			int t = bitmask.nextSetBit(0);
			if (t == -1) {
				return "";
			}
			int starty = t / this.browser.get().width;
			int endy = bitmask.length() / this.browser.get().width;

			int startx = -1;
			for (int x = 0; x < this.browser.get().width; ++x) {
				for (int y = starty; y <= endy; ++y) {
					if (bitmask.get(x + y * this.browser.get().width)) {
						startx = x;
						break;
					}
				}
				if (startx != -1)
					break;
			}

			int endx = -1;
			for (int x = this.browser.get().width - 1; x >= 0; --x) {
				for (int y = starty; y <= endy; ++y) {
					if (bitmask.get(x + y * this.browser.get().width)) {
						endx = x;
						break;
					}
				}
				if (endx != -1)
					break;
			}
			int weigth = 0;

			while (weigth < 3) {
				weigth = 0;
				for (int x = startx; x <= endx; ++x) {
					if (bitmask.get(x + starty * this.browser.get().width)) {
						weigth++;
					}
				}
				if (weigth < 3) {
					starty++;
				}
			}

			weigth = 0;
			while (weigth < 3) {
				weigth = 0;
				for (int x = startx; x <= endx; ++x) {
					if (bitmask.get(x + endy * this.browser.get().width)) {
						weigth++;
					}
				}
				if (weigth < 3) {
					endy--;
				}
			}

			List<List<Point>> parsed = new ArrayList<List<Point>>();
			int startx_ = startx;
			int endx_ = 0;
			for (int x = startx; x <= endx + 1; ++x) {
				weigth = 0;
				for (int y = starty; y <= endy; ++y) {
					if (bitmask.get(x + y * this.browser.get().width)) {
						weigth++;
					}
				}
				if (weigth == 0 || x - startx_ == 5) {
					endx_ = x - 1;
					if (startx_ <= endx_) {
						List<Point> elem = new ArrayList<Point>();
						for (int x_ = startx_; x_ <= endx_; ++x_) {
							for (int y_ = starty; y_ <= endy; ++y_) {
								if (bitmask.get(x_ + y_ * this.browser.get().width)) {
									elem.add(new Point(x_ - startx_, y_ - starty));
								}
							}

						}
						parsed.add(elem);
					} else {
						parsed.add(null);
					}
					startx_ = x + 1;
				}
			}
			StringBuffer result = new StringBuffer();
			// String date = Long.toString(System.currentTimeMillis());
			// int seq = 0;
			for (List<Point> pp : parsed) {
				if (pp == null) {
					result.append(" ");
				} else {
					// boolean found = false;
					for (Entry<String, IElementDescriptor> e : this.recognision_chars.entrySet()) {
						// System.out.println(e.getKey());
						if (e.getValue().match(this.settings.getLocaleEnum(), pp)) {
							result.append(e.getKey());
							// found = true;
							break;
						}
					}
					// if (!found) {
					// ElementDescriptor el = new ElementDescriptor(5, 8, EnumSet.of(Locale.en),
					// ColorType.CT_MONOTON, 1, new Date());
					// for (Point p : pp) {
					// el.addPixel(p.x, p.y, 0x0);
					// }
					// this.elements.put("интерфейс.Характеристики.e." + date + "." + (++seq), el);
					// }
				}
			}

			// System.out.println(result.toString());
			// System.out.println(result.toString().trim().replaceAll("\\s{3,}", "_")
			// .replaceAll("\\s+", "").replaceAll("[_]+", " "));
			return result.toString().trim().replaceAll("\\s{3,}", "_").replaceAll("\\s+", "")
					.replaceAll("[_]+", " ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void dispose() {
		this.highlight.dispose();
		this.peer.dispose();
	}

}
