package pixelbot.captcha.algorithm;

import java.awt.*;
import java.util.*;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import pixelbot.captcha.CaptchaConstants;
import pixelbot.captcha.solver.BaseCaptchaSolver;
import pixelbot.elements.ElementDescriptor;

/**
 * Strips texture from each tile, thickens what is left, and scores an assembly by how well
 * the thickened contours meet at the seams.
 * <p>
 * The ink of a tile is grouped into connected blobs (see {@link GridClusters}). A blob is discarded as texture when
 * it is small, or when it is mid-sized but too round — roundness being measured by the same
 * generalised variance used elsewhere, {@code Dx*Dy - cov^2}, since a stroke is thin and a
 * speckle is not. Surviving contours are dilated by a 12-point structuring element, so two
 * strokes that ought to continue across a seam overlap once the tiles are placed together.
 * <p>
 * This is ordinary morphological image processing done by hand, and it is the most
 * discriminating of the mask-based scorers. It is also the slowest, and its four size and
 * roundness thresholds were fitted to one source of images.
 */
public class BorderClusterAlgorithm extends Algorithm {

	private static final Logger LOG = Logger.getLogger(BorderClusterAlgorithm.class
			.getName());

	/** Ink pixels this far apart or closer belong to the same blob. */
	private static final double CLUSTER_RADIUS = 4;

	/** Blobs below this many pixels are always texture. */
	private static final int NOISE_SIZE = 150;

	/** Blobs above this many pixels are always kept. */
	private static final int STRUCTURE_SIZE = 1000;

	/** Between the two sizes, a blob is texture when it is rounder than this. */
	private static final double ROUNDNESS_LIMIT = 40000;

	/** Small-to-mid blobs get the stricter limit. */
	private static final int SMALL_STRUCTURE_SIZE = 600;
	private static final double SMALL_ROUNDNESS_LIMIT = 15000;

	private BitSet[] clearmasks = new BitSet[6];
	private BitSet[] fillmasks = new BitSet[6];

	public BorderClusterAlgorithm(ElementDescriptor[] elems) {
		super(elems);

		int size = CaptchaConstants.elem_full_dimension;
		for (int k = 0; k < 6; ++k) {
			boolean[] ink = new boolean[size * size];
			for (int j = 0; j < size; ++j) {
				for (int i = 0; i < size; ++i) {
					ink[i + j * size] = this.ceds[k].getPoint(i, j);
				}
			}

			this.clearmasks[k] = new BitSet();
			for (int[] cluster : GridClusters.cluster(ink, size, size, CLUSTER_RADIUS)) {
				boolean filter;
				int blob = cluster.length;
				if (blob < NOISE_SIZE) {
					filter = true;
				} else if (blob < STRUCTURE_SIZE) {
					MomentStats stats = new MomentStats();
					for (int index : cluster) {
						stats.add(index % size, index / size);
					}

					double roundness = stats.generalisedVariance();
					filter = blob < SMALL_STRUCTURE_SIZE ? roundness > SMALL_ROUNDNESS_LIMIT
							: roundness > ROUNDNESS_LIMIT;

					LOG.log(Level.FINEST, "tile {0} blob: size={1} roundness={2} texture={3}",
							new Object[] { Integer.valueOf(k), Integer.valueOf(blob),
									Double.valueOf(roundness), Boolean.valueOf(filter) });
				} else {
					filter = false;
				}

				if (filter) {
					for (int index : cluster) {
						this.clearmasks[k].set(index);
					}
				}
			}
		}

		Point[] pp = new Point[] { new Point(-1, -1), new Point(-1, 0), new Point(-1, +1),
				new Point(0, -1), new Point(0, +1), new Point(+1, -1), new Point(+1, 0),
				new Point(+1, 1), new Point(-2, 0), new Point(+2, 0), new Point(0, -2),
				new Point(0, +2) /*
								 * , new Point(+2, +1), new Point(+1, +2), new Point(+2, -1), new
								 * Point(+1, -2), new Point(-1, -2), new Point(-2, -1), new
								 * Point(-2, +1), new Point(-1, +2), new Point(-3, -3), new
								 * Point(+3, +3), new Point(+3, -3), new Point(-3, +3)
								 */};

		for (int k = 0; k < 6; ++k) {
			this.fillmasks[k] = new BitSet();
			for (int j = 0; j < CaptchaConstants.elem_full_dimension; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_full_dimension; ++i) {
					if (this.ceds[k].getPoint(i, j)
							&& !this.clearmasks[k]
									.get(i + j * CaptchaConstants.elem_full_dimension)) {
						for (Point p : pp) {
							int x = p.x + i;
							int y = p.y + j;
							if (x >= 0 && x < CaptchaConstants.elem_full_dimension && y >= 0
									&& y < CaptchaConstants.elem_full_dimension)
								if (!this.ceds[k].getPoint(x, y)) {
									this.fillmasks[k].set(x + y
											* CaptchaConstants.elem_full_dimension);
								}
						}
					}
				}
			}
		}
	}

	@Override
	public BitSet getMatrix(byte[] a) {
		BitSet matrix = super.getMatrix(a);
		for (int k = 0; k < 6; ++k) {
			Point p = CaptchaConstants.offsets[k];
			for (int j = 0; j < CaptchaConstants.elem_full_dimension; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_full_dimension; ++i) {
					if (this.fillmasks[a[k]].get(i + j * CaptchaConstants.elem_full_dimension)) {
						BaseCaptchaSolver.setPoint(matrix, p.x + i, p.y + j, true);
					} else if (this.clearmasks[a[k]].get(i + j
							* CaptchaConstants.elem_full_dimension)) {
						BaseCaptchaSolver.setPoint(matrix, p.x + i, p.y + j, false);
					}
				}
			}
		}
		return matrix;
	}

	@Override
	public boolean getXY(byte pos, int x, int y) {
		return !this.clearmasks[pos].get(x + y * CaptchaConstants.elem_full_dimension)
				&& super.getXY(pos, x, y);
	}

	@Override
	protected long calculateWeight(BitSet matrix) {
		long result = 0;

		for (int i = 0; i < CaptchaConstants.capcha_width; ++i) {
			boolean b1 = BaseCaptchaSolver.getPoint(matrix, i, CaptchaConstants.elem_full_dimension
					- CaptchaConstants.elem_border - 1);
			boolean b2 = BaseCaptchaSolver.getPoint(matrix, i, CaptchaConstants.elem_full_dimension
					+ CaptchaConstants.elem_border);

			boolean b01 = BaseCaptchaSolver.getPoint(matrix, i, CaptchaConstants.elem_border);
			boolean b02 = BaseCaptchaSolver.getPoint(matrix, i,
					CaptchaConstants.elem_full_dimension * 2 + CaptchaConstants.elem_border);

			if (b1 && b2) {
				result += 2;
			} else if (b1 == b2) {
				result++;
			} else {
				result -= 2;
			}
			if (b01)
				result--;
			if (b02)
				result--;
		}

		for (int j = 0; j < CaptchaConstants.capcha_width; ++j) {
			boolean b1 = BaseCaptchaSolver.getPoint(matrix, CaptchaConstants.elem_full_dimension
					- CaptchaConstants.elem_border - 1, j);
			boolean b2 = BaseCaptchaSolver.getPoint(matrix, CaptchaConstants.elem_full_dimension
					+ CaptchaConstants.elem_border, j);
			boolean b3 = BaseCaptchaSolver.getPoint(matrix, CaptchaConstants.elem_full_dimension
					* 2 - CaptchaConstants.elem_border - 1, j);
			boolean b4 = BaseCaptchaSolver.getPoint(matrix, CaptchaConstants.elem_full_dimension
					* 2 + CaptchaConstants.elem_border, j);

			boolean b01 = BaseCaptchaSolver.getPoint(matrix, CaptchaConstants.elem_border, j);
			boolean b02 = BaseCaptchaSolver.getPoint(matrix, CaptchaConstants.elem_full_dimension
					* 3 - CaptchaConstants.elem_border - 1, j);

			if (b1 && b2) {
				result += 2;
			} else if (b1 == b2) {
				result++;
			} else {
				result -= 2;
			}
			if (b3 && b4) {
				result += 2;
			} else if (b3 == b4) {
				result++;
			} else {
				result -= 2;
			}

			if (b01)
				result--;
			if (b02)
				result--;

		}
		return Short.MAX_VALUE - result;
	}

	private static BitSet getFillMatrix(byte[] a, BitSet[] masks) {
		BitSet matrix = new BitSet();
		for (int k = 0; k < 6; ++k) {
			Point p = CaptchaConstants.offsets[k];
			for (int j = 0; j < CaptchaConstants.elem_full_dimension; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_full_dimension; ++i) {
					BaseCaptchaSolver.setPoint(matrix, p.x + i, p.y + j,
							masks[a[k]].get(i + j * CaptchaConstants.elem_full_dimension));
				}
			}

		}
		return matrix;
	}

	@Override
	public Collection<FillDescription> getFills(byte[] a) {
		Vector<FillDescription> result = new Vector<FillDescription>();

		result.addAll(super.getFills(a));

		result.add(new FillDescription("Cluster Clear", BorderClusterAlgorithm.getFillMatrix(a,
				this.clearmasks), Color.gray));
		result.add(new FillDescription("zCluster Fills", BorderClusterAlgorithm.getFillMatrix(a,
				this.fillmasks), Color.magenta));

		return result;
	}
}
