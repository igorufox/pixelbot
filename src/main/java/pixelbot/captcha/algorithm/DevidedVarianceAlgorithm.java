package pixelbot.captcha.algorithm;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pixelbot.captcha.*;
import pixelbot.captcha.solver.BaseCaptchaSolver;
import pixelbot.elements.ElementDescriptor;

/**
 * Scores an assembly by how elongated the ink is inside each band of the canvas.
 * <p>
 * The canvas is split into five overlapping bands — the two tile rows and the three tile
 * columns — and each band contributes the determinant of the covariance matrix of its ink
 * points, {@code Dx*Dy - cov^2}. That is the squared area of the dispersion ellipse, so a band
 * scores low when its ink forms a thin oriented structure: a stroke running unbroken across a
 * tile seam. Measuring per band rather than globally is what makes this discriminate at all.
 * <p>
 * Tiles are denoised first: the ink of each tile is grouped into connected blobs and blobs under
 * 300 pixels are masked out as texture. See {@link GridClusters} for why that grouping is not
 * done with a general-purpose clusterer any more.
 * <p>
 * {@link FastDividedVarianceAlgorithm} computes the same score without rebuilding the canvas
 * for every permutation.
 */
public class DevidedVarianceAlgorithm extends Algorithm {

	private static final Logger LOG = Logger.getLogger(DevidedVarianceAlgorithm.class.getName());

	/** Ink pixels this far apart or closer belong to the same blob. */
	protected static final double CLUSTER_RADIUS = 4;

	/** Ink clusters smaller than this are treated as texture and ignored. */
	protected static final int NOISE_CLUSTER_SIZE = 300;

	protected final BitSet[] masks = new BitSet[6];

	public DevidedVarianceAlgorithm(ElementDescriptor[] elems) {
		super(elems);

		int size = CaptchaConstants.elem_full_dimension;
		for (int k = 0; k < 6; ++k) {
			boolean[] ink = new boolean[size * size];
			for (int j = 0; j < size; ++j) {
				for (int i = 0; i < size; ++i) {
					ink[i + j * size] = this.ceds[k].getPoint(i, j);
				}
			}

			this.masks[k] = new BitSet();
			for (int[] cluster : GridClusters.cluster(ink, size, size, CLUSTER_RADIUS)) {
				if (cluster.length < NOISE_CLUSTER_SIZE) {
					for (int index : cluster) {
						this.masks[k].set(index);
					}
				}
			}
		}

		LOG.log(Level.FINE, "denoised {0} tiles", Integer.valueOf(this.masks.length));
	}

	@Override
	public BitSet getMatrix(byte[] a) {
		BitSet matrix = super.getMatrix(a);
		for (int k = 0; k < 6; ++k) {
			Point p = CaptchaConstants.offsets[k];
			for (int j = 0; j < CaptchaConstants.elem_full_dimension; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_full_dimension; ++i) {
					if (this.masks[a[k]].get(i + j * CaptchaConstants.elem_full_dimension)) {
						BaseCaptchaSolver.setPoint(matrix, p.x + i, p.y + j, false);
					}
				}
			}
		}
		return matrix;
	}

	@Override
	public boolean getXY(byte pos, int x, int y) {
		return !this.masks[pos].get(x + y * CaptchaConstants.elem_full_dimension)
				&& super.getXY(pos, x, y);
	}

	@Override
	protected long calculateWeight(BitSet matrix) {
		MomentStats[] bands = new MomentStats[BAND_COUNT];
		for (int k = 0; k < BAND_COUNT; ++k) {
			bands[k] = new MomentStats();
		}

		for (int j = 0; j < CaptchaConstants.capcha_height; ++j) {
			for (int i = 0; i < CaptchaConstants.capcha_width; ++i) {
				if (BaseCaptchaSolver.getPoint(matrix, i, j)) {
					bands[rowBand(j)].add(i, j);
					bands[columnBand(i)].add(i, j);
				}
			}
		}

		double result = 0;
		for (MomentStats band : bands) {
			result += band.generalisedVariance();
		}
		return (long) result;
	}

	/** Two row bands and three column bands. */
	protected static final int BAND_COUNT = 5;

	/** Band index for a canvas row: 0 for the top tile row, 1 for the bottom one. */
	protected static int rowBand(int y) {
		return y < CaptchaConstants.elem_full_dimension ? 0 : 1;
	}

	/** Band index for a canvas column: 2, 3 or 4 for the left, middle and right tile column. */
	protected static int columnBand(int x) {
		if (x < CaptchaConstants.elem_full_dimension) {
			return 2;
		}
		return x < CaptchaConstants.elem_full_dimension * 2 ? 3 : 4;
	}

	private static BitSet getFillMatrix(BitSet matrix) {
		BitSet result = new BitSet();

		for (Point p : CaptchaConstants.offsets) {

			Point mc = DevidedVarianceAlgorithm.calculateMassCenter(matrix, p);

			for (int i = 0; i < 5; ++i) {
				BaseCaptchaSolver.setPoint(result, mc.x + i, mc.y + i);
				BaseCaptchaSolver.setPoint(result, mc.x + i, mc.y - i);
				BaseCaptchaSolver.setPoint(result, mc.x - i, mc.y + i);
				BaseCaptchaSolver.setPoint(result, mc.x - i, mc.y - i);
			}
		}
		return result;
	}

	private static Point calculateMassCenter(BitSet matrix, Point offset) {
		int total_x = 0, total_y = 0, count = 0;
		for (int j = offset.y; j < offset.y + CaptchaConstants.elem_full_dimension; ++j) {
			for (int i = offset.x; i < offset.x + CaptchaConstants.elem_full_dimension; ++i) {
				if (BaseCaptchaSolver.getPoint(matrix, i, j)) {
					total_x += i;
					total_y += j;
					count++;
				}
			}
		}
		return new Point(total_x / count, total_y / count);
	}

	private static BitSet getFillMatrix2() {
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

	private static BitSet getFillMatrix3(BitSet matrix) {
		BitSet result = new BitSet();
		short[] xArray = new short[CaptchaConstants.capcha_width];
		short[] yArray = new short[CaptchaConstants.capcha_height];

		for (int j = 0; j < CaptchaConstants.elem_full_dimension; ++j) {
			for (int i = 0; i < CaptchaConstants.capcha_width; ++i) {
				if (BaseCaptchaSolver.getPoint(matrix, i, j)) {
					xArray[i]++;
					yArray[j]++;
				}
			}
		}

		// for (int j = 0; j < CaptchaConstants.capcha_height; ++j) {
		// BaseCaptchaSolver.setPoint(result, yArray[j], j);
		// }

		for (int i = 0; i < CaptchaConstants.capcha_width; ++i) {
			for (int j = 0; j < xArray[i]; ++j) {
				BaseCaptchaSolver.setPoint(result, i, j);
			}
		}

		return result;
	}

	@Override
	public Collection<FillDescription> getFills(byte[] a) {
		Vector<FillDescription> result = new Vector<FillDescription>();

		result.addAll(super.getFills(a));

		result.add(new FillDescription("Variance Mass Center", DevidedVarianceAlgorithm
				.getFillMatrix(this.getMatrix(a)), Color.blue));
		result.add(new FillDescription("Variance Strikeout", DevidedVarianceAlgorithm
				.getFillMatrix2(), Color.green));
		result.add(new FillDescription("Variance 3", DevidedVarianceAlgorithm.getFillMatrix3(this
				.getMatrix(a)), new Color(0, 0, 255, 127)));

		return result;
	}
}
