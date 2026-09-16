package pixelbot.captcha.algorithm.seam;

import pixelbot.captcha.TileImage;
import pixelbot.elements.ElementDescriptor;

/**
 * Seam cost as the plain colour difference between the touching rows or columns of two tiles.
 * <p>
 * For a left-right seam this is
 *
 * <pre>
 *   D(i, j) = sum over y, over channels c of |i[w-1, y, c] - j[0, y, c]|^p
 * </pre>
 *
 * with {@code p = 2} — the sum of squared differences that every jigsaw paper uses as its
 * baseline. It has no tunable threshold, works on colour directly, and is one subtraction per
 * pixel of the seam.
 * <p>
 * It is a baseline, and it has a known weakness: it rewards two tiles for being uniformly similar
 * rather than for continuing the same structure, so a pair of flat background tiles matches
 * everything. {@link MahalanobisGradientAlgorithm} fixes exactly that.
 */
public class SeamDissimilarityAlgorithm extends SeamAlgorithm {

	private static final int POWER = 2;

	public SeamDissimilarityAlgorithm(ElementDescriptor[] elems) {
		super(elems);
	}

	@Override
	protected double costRightOf(TileImage left, TileImage right) {
		int height = Math.min(left.getHeight(), right.getHeight());
		if (height <= 0 || left.getWidth() <= 0 || right.getWidth() <= 0) {
			return 0;
		}

		int lastColumn = left.getWidth() - 1;
		double total = 0;
		for (int y = 0; y < height; ++y) {
			for (int c = 0; c < 3; ++c) {
				total += power(left.channel(lastColumn, y, c) - right.channel(0, y, c));
			}
		}
		return total / height;
	}

	@Override
	protected double costBelow(TileImage top, TileImage bottom) {
		int width = Math.min(top.getWidth(), bottom.getWidth());
		if (width <= 0 || top.getHeight() <= 0 || bottom.getHeight() <= 0) {
			return 0;
		}

		int lastRow = top.getHeight() - 1;
		double total = 0;
		for (int x = 0; x < width; ++x) {
			for (int c = 0; c < 3; ++c) {
				total += power(top.channel(x, lastRow, c) - bottom.channel(x, 0, c));
			}
		}
		return total / width;
	}

	private static double power(int difference) {
		double d = Math.abs(difference);
		return POWER == 2 ? d * d : Math.pow(d, POWER);
	}
}
