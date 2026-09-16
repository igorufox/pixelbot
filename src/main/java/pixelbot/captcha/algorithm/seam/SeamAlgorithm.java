package pixelbot.captcha.algorithm.seam;

import java.util.logging.Level;
import java.util.logging.Logger;

import pixelbot.captcha.TileImage;
import pixelbot.captcha.algorithm.Algorithm;
import pixelbot.elements.ElementDescriptor;

/**
 * Scores an assembly by how well neighbouring tiles agree along the seams between them.
 * <p>
 * This is how jigsaw reassembly is normally posed today, and it is a better fit for the problem
 * than the shape heuristics in the sibling package. Two observations do the work:
 * <ul>
 * <li>Whether tile <em>j</em> belongs to the right of tile <em>i</em> depends only on those two
 * tiles, not on the rest of the arrangement. So all 60 ordered pairs can be compared once, up
 * front, and an arrangement is then scored by adding up its seven adjacencies.</li>
 * <li>The comparison is over colour, not over a thresholded ink mask, so there is no sensitivity
 * constant to tune per image source.</li>
 * </ul>
 * Cost: one pass over 60 tile edges to build the tables, then seven array lookups per permutation.
 * The whole search finishes in well under a millisecond, against tens of millions of pixel tests
 * for the canvas-scanning scorers.
 * <p>
 * The grid is
 *
 * <pre>
 *   0 1 2
 *   3 4 5
 * </pre>
 *
 * giving four left-right seams and three top-bottom ones.
 *
 * @see SeamDissimilarityAlgorithm plain colour difference, the cheap baseline
 * @see MahalanobisGradientAlgorithm gradient compatibility, markedly more accurate
 */
public abstract class SeamAlgorithm extends Algorithm {

	private static final Logger LOG = Logger.getLogger(SeamAlgorithm.class.getName());

	/** Pairs of grid positions that share a left-right seam. */
	private static final int[][] HORIZONTAL = { { 0, 1 }, { 1, 2 }, { 3, 4 }, { 4, 5 } };

	/** Pairs of grid positions that share a top-bottom seam. */
	private static final int[][] VERTICAL = { { 0, 3 }, { 1, 4 }, { 2, 5 } };

	/**
	 * Seam costs are fractions; the search compares them as longs. Scaling keeps enough resolution
	 * that distinct arrangements do not collapse onto the same score.
	 */
	private static final double SCORE_SCALE = 1000;

	protected final TileImage[] tiles = new TileImage[6];

	/** {@code rightOf[i][j]}: cost of putting tile j immediately right of tile i. */
	private final double[][] rightOf = new double[6][6];

	/** {@code below[i][j]}: cost of putting tile j immediately below tile i. */
	private final double[][] below = new double[6][6];

	protected SeamAlgorithm(ElementDescriptor[] elems) {
		super(elems);

		for (int i = 0; i < 6; ++i) {
			this.tiles[i] = new TileImage(elems[i]);
		}

		this.prepare();

		for (int i = 0; i < 6; ++i) {
			for (int j = 0; j < 6; ++j) {
				if (i == j) {
					// A tile is never its own neighbour, but the table is indexed blindly.
					this.rightOf[i][j] = Double.MAX_VALUE / 16;
					this.below[i][j] = Double.MAX_VALUE / 16;
					continue;
				}
				this.rightOf[i][j] = this.costRightOf(this.tiles[i], this.tiles[j]);
				this.below[i][j] = this.costBelow(this.tiles[i], this.tiles[j]);
			}
		}

		LOG.log(Level.FINE, "{0}: built 60 pairwise seam costs", this.getClass().getSimpleName());
	}

	/** Hook for subclasses that need per-tile statistics before the cost tables are filled. */
	protected void prepare() {
		// nothing by default
	}

	/** How badly tile {@code right} continues tile {@code left} across their shared seam. */
	protected abstract double costRightOf(TileImage left, TileImage right);

	/** How badly tile {@code bottom} continues tile {@code top} across their shared seam. */
	protected abstract double costBelow(TileImage top, TileImage bottom);

	@Override
	public long calculateWeight(byte[] a) {
		double total = 0;
		for (int[] pair : HORIZONTAL) {
			total += this.rightOf[a[pair[0]]][a[pair[1]]];
		}
		for (int[] pair : VERTICAL) {
			total += this.below[a[pair[0]]][a[pair[1]]];
		}
		return (long) Math.min(total * SCORE_SCALE, Long.MAX_VALUE / 2.0);
	}
}
