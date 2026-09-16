package pixelbot.captcha.algorithm;

import java.awt.Point;

import pixelbot.captcha.CaptchaConstants;
import pixelbot.elements.ElementDescriptor;

/**
 * {@link DevidedVarianceAlgorithm}'s score, computed without touching the canvas.
 * <p>
 * The band score is a function of the variance and covariance of the band's ink points, and those
 * come from six running sums — count, {@code sum x}, {@code sum y}, {@code sum x^2},
 * {@code sum y^2}, {@code sum xy} — which are additive. A tile placed at a grid position always
 * falls inside exactly one row band and exactly one column band, because the tiles tile the canvas
 * exactly. So the sums for every (tile, position) pair can be accumulated once, and scoring a
 * permutation becomes six lookups and a handful of additions per band.
 * <p>
 * The original rebuilt an 87k-bit canvas and rescanned it for each of the 720 permutations: about
 * 63 million pixel tests per solve. This scans 36 tile placements once — about 545 thousand pixel
 * tests — and then does roughly 30 additions per permutation. The score is the same; only the
 * arithmetic order changes, so results agree to floating-point rounding.
 */
public class FastDividedVarianceAlgorithm extends DevidedVarianceAlgorithm {

	/** Sufficient statistics for tile {@code t} placed at grid position {@code k}. */
	private final MomentStats[][] placement = new MomentStats[6][6];

	/** Which of the five bands each grid position contributes to: row band, then column band. */
	private final int[][] positionBands = new int[6][2];

	public FastDividedVarianceAlgorithm(ElementDescriptor[] elems) {
		super(elems);

		for (int k = 0; k < CaptchaConstants.offsets.length; ++k) {
			Point offset = CaptchaConstants.offsets[k];
			this.positionBands[k][0] = rowBand(offset.y);
			this.positionBands[k][1] = columnBand(offset.x);

			for (byte tile = 0; tile < 6; ++tile) {
				MomentStats stats = new MomentStats();
				for (int j = 0; j < CaptchaConstants.elem_full_dimension; ++j) {
					for (int i = 0; i < CaptchaConstants.elem_full_dimension; ++i) {
						if (this.getXY(tile, i, j)) {
							stats.add(offset.x + i, offset.y + j);
						}
					}
				}
				this.placement[tile][k] = stats;
			}
		}
	}

	@Override
	public long calculateWeight(byte[] a) {
		MomentStats[] bands = new MomentStats[BAND_COUNT];
		for (int b = 0; b < BAND_COUNT; ++b) {
			bands[b] = new MomentStats();
		}

		for (int k = 0; k < a.length; ++k) {
			MomentStats stats = this.placement[a[k]][k];
			bands[this.positionBands[k][0]].add(stats);
			bands[this.positionBands[k][1]].add(stats);
		}

		double result = 0;
		for (MomentStats band : bands) {
			result += band.generalisedVariance();
		}
		return (long) result;
	}
}
