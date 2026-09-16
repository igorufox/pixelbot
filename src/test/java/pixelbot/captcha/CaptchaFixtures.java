package pixelbot.captcha;

import java.util.Date;
import java.util.EnumSet;
import java.util.Random;

import pixelbot.elements.ElemTools;
import pixelbot.elements.ElementDescriptor;
import pixelbot.elements.IElementDescriptor.ColorType;
import pixelbot.elements.IElementDescriptor.Locale;

/** Synthetic captchas: a picture, cut into six tiles, shuffled. */
public final class CaptchaFixtures {

	public static final int TILE = CaptchaConstants.elem_dimension;
	public static final int COLUMNS = 3;
	public static final int ROWS = 2;

	private CaptchaFixtures() {
	}

	/**
	 * Descriptors render through the global {@code ElemTools.factor}, which the application sets
	 * to 1 on start-up. Tests have to do the same or every tile comes out magnified.
	 */
	public static void useUnscaledTiles() {
		ElemTools.factor = 1;
	}

	/**
	 * Six tiles cut, in reading order, from one continuous picture.
	 *
	 * @param lineArt true for black strokes on white, which is what the mask-based scorers expect;
	 *                false for a smooth colour field, which is what the seam scorers expect
	 */
	public static ElementDescriptor[] tiles(long seed, boolean lineArt) {
		int width = TILE * COLUMNS;
		int height = TILE * ROWS;
		int[] picture = lineArt ? strokes(seed, width, height) : colourField(seed, width, height);

		ElementDescriptor[] result = new ElementDescriptor[COLUMNS * ROWS];
		for (int row = 0; row < ROWS; ++row) {
			for (int column = 0; column < COLUMNS; ++column) {
				ElementDescriptor tile = new ElementDescriptor(TILE, TILE,
						EnumSet.allOf(Locale.class), ColorType.CT_MULTICOLOR, 0, new Date());
				for (int y = 0; y < TILE; ++y) {
					for (int x = 0; x < TILE; ++x) {
						tile.addPixel(x, y, picture[(column * TILE + x) + (row * TILE + y) * width]);
					}
				}
				result[column + row * COLUMNS] = tile;
			}
		}
		return result;
	}

	/** Reorders {@code tiles} so that {@code shuffled[i] == tiles[order[i]]}. */
	public static ElementDescriptor[] shuffle(ElementDescriptor[] tiles, int[] order) {
		ElementDescriptor[] result = new ElementDescriptor[order.length];
		for (int i = 0; i < order.length; ++i) {
			result[i] = tiles[order[i]];
		}
		return result;
	}

	/** A permutation of 0..5 drawn from {@code random}. */
	public static int[] randomOrder(Random random) {
		int[] order = { 0, 1, 2, 3, 4, 5 };
		for (int i = order.length - 1; i > 0; --i) {
			int j = random.nextInt(i + 1);
			int tmp = order[i];
			order[i] = order[j];
			order[j] = tmp;
		}
		return order;
	}

	/** Smooth, strongly structured colour — the sort of thing a seam measure is built for. */
	private static int[] colourField(long seed, int width, int height) {
		Random random = new Random(seed);
		double ax = 1 + random.nextDouble() * 3;
		double ay = 1 + random.nextDouble() * 3;
		double phase = random.nextDouble() * Math.PI;

		int[] picture = new int[width * height];
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				double u = (double) x / width;
				double v = (double) y / height;
				int r = channel(Math.sin(ax * 6.0 * u + phase) * Math.cos(ay * 4.0 * v));
				int g = channel(Math.sin((ax + ay) * 5.0 * (u + v) + phase));
				int b = channel(Math.cos(ay * 7.0 * v - ax * 3.0 * u));
				picture[x + y * width] = (r << 16) | (g << 8) | b;
			}
		}
		return picture;
	}

	/** Dark strokes on a light background, with speckle the denoising step should discard. */
	private static int[] strokes(long seed, int width, int height) {
		Random random = new Random(seed);
		int[] picture = new int[width * height];
		java.util.Arrays.fill(picture, 0xFFFFFF);

		for (int stroke = 0; stroke < 12; ++stroke) {
			double x = random.nextInt(width);
			double y = random.nextInt(height);
			double angle = random.nextDouble() * Math.PI * 2;
			double turn = (random.nextDouble() - 0.5) * 0.05;

			for (int step = 0; step < 900; ++step) {
				angle += turn;
				x += Math.cos(angle);
				y += Math.sin(angle);
				if (x < 2 || y < 2 || x >= width - 2 || y >= height - 2) {
					break;
				}
				for (int dy = -2; dy <= 2; ++dy) {
					for (int dx = -2; dx <= 2; ++dx) {
						picture[((int) x + dx) + ((int) y + dy) * width] = 0x101010;
					}
				}
			}
		}

		for (int speck = 0; speck < 400; ++speck) {
			int x = 1 + random.nextInt(width - 2);
			int y = 1 + random.nextInt(height - 2);
			picture[x + y * width] = 0x202020;
		}

		return picture;
	}

	private static int channel(double value) {
		int scaled = (int) Math.round((value + 1) * 127.5);
		return Math.max(0, Math.min(255, scaled));
	}
}
