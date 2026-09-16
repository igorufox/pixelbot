package pixelbot.captcha.algorithm;

import java.awt.*;
import java.util.*;
import java.util.List;

import pixelbot.captcha.CaptchaConstants;
import pixelbot.captcha.solver.BaseCaptchaSolver;
import pixelbot.elements.ElementDescriptor;

/**
 * Scores an assembly by how tightly its ink is concentrated: the spread of the ink pixels over the
 * whole canvas, plus the spread along a sheared diagonal scan.
 * <p>
 * This is the weakest of the scorers here. A global variance cannot tell a correctly reassembled
 * picture from any other compact blob, and the two weights are hand-fitted to one source of
 * images. {@link DevidedVarianceAlgorithm} measures the same quantity per band and discriminates
 * far better; prefer that, or one of the seam scorers.
 */
public class VarianceAlgorithm extends Algorithm {

	private static final long SPREAD_WEIGHT = 10000;
	private static final long DIAGONAL_WEIGHT = 1250;

	/** Half-width of the cross drawn at each tile's centre of mass in the debug overlay. */
	private static final int MASS_CENTRE_MARKER = 5;

	public VarianceAlgorithm(ElementDescriptor[] elems) {
		super(elems);
	}

	@Override
	protected long calculateWeight(BitSet matrix) {
		MomentStats spread = new MomentStats();
		for (int j = 0; j < CaptchaConstants.capcha_height; ++j) {
			for (int i = 0; i < CaptchaConstants.capcha_width; ++i) {
				if (BaseCaptchaSolver.getPoint(matrix, i, j)) {
					spread.add(i, j);
				}
			}
		}

		MomentStats diagonal = new MomentStats();
		for (int k = 0; k < CaptchaConstants.capcha_width; ++k) {
			for (int l = -CaptchaConstants.capcha_width; l < CaptchaConstants.capcha_width; ++l) {
				int x = k - (l / 2);
				int y = k - CaptchaConstants.elem_full_dimension / 2 + ((l + 1) / 2);

				if (x >= 0 && x < CaptchaConstants.capcha_width && y >= 0
						&& y < CaptchaConstants.capcha_height
						&& BaseCaptchaSolver.getPoint(matrix, x, y)) {
					diagonal.add(k, l);
				}
			}
		}

		double result = (spread.varianceX() + spread.varianceY()) * SPREAD_WEIGHT
				+ (diagonal.varianceX() + diagonal.varianceY()) * DIAGONAL_WEIGHT;
		return (long) result;
	}

	/** A cross at each tile's centre of mass, for the debug overlay. */
	private static BitSet massCentreMatrix(BitSet matrix) {
		BitSet result = new BitSet();

		for (Point p : CaptchaConstants.offsets) {
			Point mc = VarianceAlgorithm.massCentre(matrix, p);
			if (mc == null) {
				continue;
			}
			for (int i = 0; i < MASS_CENTRE_MARKER; ++i) {
				BaseCaptchaSolver.setPoint(result, mc.x + i, mc.y + i);
				BaseCaptchaSolver.setPoint(result, mc.x + i, mc.y - i);
				BaseCaptchaSolver.setPoint(result, mc.x - i, mc.y + i);
				BaseCaptchaSolver.setPoint(result, mc.x - i, mc.y - i);
			}
		}
		return result;
	}

	/** Centre of mass of the ink in one tile, or null when the tile has none. */
	private static Point massCentre(BitSet matrix, Point offset) {
		long totalX = 0;
		long totalY = 0;
		int count = 0;
		for (int j = offset.y; j < offset.y + CaptchaConstants.elem_full_dimension; ++j) {
			for (int i = offset.x; i < offset.x + CaptchaConstants.elem_full_dimension; ++i) {
				if (BaseCaptchaSolver.getPoint(matrix, i, j)) {
					totalX += i;
					totalY += j;
					count++;
				}
			}
		}
		if (count == 0) {
			return null;
		}
		return new Point((int) (totalX / count), (int) (totalY / count));
	}

	/** A cross through the middle of each tile, for the debug overlay. */
	private static BitSet tileCrossMatrix() {
		BitSet result = new BitSet();
		for (Point p : CaptchaConstants.offsets) {
			for (int j = p.y; j < p.y + CaptchaConstants.elem_full_dimension; ++j) {
				BaseCaptchaSolver.setPoint(result, p.x + CaptchaConstants.elem_full_dimension / 2,
						j);
			}
			for (int i = p.x; i < p.x + CaptchaConstants.elem_full_dimension; ++i) {
				BaseCaptchaSolver.setPoint(result, i, p.y + CaptchaConstants.elem_full_dimension
						/ 2);
			}
		}
		return result;
	}

	@Override
	public Collection<FillDescription> getFills(byte[] a) {
		List<FillDescription> result = new ArrayList<FillDescription>();

		result.addAll(super.getFills(a));
		result.add(new FillDescription("Variance Mass Center", VarianceAlgorithm
				.massCentreMatrix(this.getMatrix(a)), Color.blue));
		result.add(new FillDescription("Variance Strikeout", VarianceAlgorithm.tileCrossMatrix(),
				Color.green));

		return result;
	}
}
