package pixelbot.captcha.solver;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import pixelbot.captcha.CaptchaConstants;
import pixelbot.captcha.algorithm.*;
import pixelbot.captcha.algorithm.Algorithm.FillDescription;
import pixelbot.captcha.algorithm.Algorithm.Result;

/**
 * Searches the 720 ways of arranging six tiles in the 3x2 grid and keeps the one an
 * {@link Algorithm} scores lowest.
 * <p>
 * The search is exhaustive because 6! is nothing; the interesting work is entirely in the scoring
 * function and in the boundary constraints that throw most permutations away before they are ever
 * scored.
 */
public abstract class BaseCaptchaSolver {

	private static final Logger LOG = Logger.getLogger(BaseCaptchaSolver.class.getName());

	/** The 720 permutations of six tiles, generated once. */
	private static final byte[][] PERMUTATIONS = allPermutations();

	protected List<Algorithm> algorithms;

	protected BaseCaptchaSolver(byte[][] boundaries, Algorithm... algorithms) {
		this(boundaries, Arrays.asList(algorithms));
	}

	protected BaseCaptchaSolver(byte[][] boundaries, List<Algorithm> algorithms) {
		this.algorithms = algorithms;

		Collection<byte[][]> b = this.calculateBoundaries(boundaries);

		for (Algorithm algorithm : algorithms) {
			algorithm.setBoundaries(b);
		}
	}

	protected Collection<FillDescription> getFills(final byte[] a) {
		TreeSet<FillDescription> result = new TreeSet<FillDescription>();

		for (Algorithm algorithm : this.algorithms) {
			result.addAll(algorithm.getFills(a));
		}
		return result;
	}

	public Icon getIcon(final byte[] a) {
		return new Icon() {

			private Collection<FillDescription> fills = BaseCaptchaSolver.this.getFills(a);

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {

				((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						1.0f));
				Color org_color = g.getColor();
				for (int j = 0; j < CaptchaConstants.capcha_height; ++j) {
					for (int i = 0; i < CaptchaConstants.capcha_width; ++i) {
						g.setColor(Color.white);
						g.fillRect(x + i, y + j, 1, 1);
						for (FillDescription fd : this.fills) {
							if (fd.getMatrix().get(i + j * CaptchaConstants.capcha_width)) {
								g.setColor(fd.getColor());
								g.fillRect(x + i, y + j, 1, 1);
							}
						}
					}
				}
				g.setColor(org_color);
			}

			@Override
			public int getIconWidth() {
				return CaptchaConstants.capcha_width;
			}

			@Override
			public int getIconHeight() {
				return CaptchaConstants.capcha_height;

			}
		};
	}

	public static void setPoint(BitSet matrix, int x, int y) {
		BaseCaptchaSolver.setPoint(matrix, x, y, true);
	}

	public static void setPoint(BitSet matrix, int x, int y, boolean value) {
		matrix.set(x + y * CaptchaConstants.capcha_width, value);
	}

	public static boolean getPoint(BitSet matrix, int x, int y) {
		return matrix.get(x + y * CaptchaConstants.capcha_width);
	}

	public abstract Result solve();

	/** The best-scoring permutation, or the identity if the constraints rule everything out. */
	protected Result iterate(Algorithm algorithm) {
		List<Result> ranked = this.rank(algorithm);
		if (ranked.isEmpty()) {
			LOG.warning("boundary constraints rejected every permutation; falling back to identity");
			return new Result(Long.MAX_VALUE, identity());
		}
		Result best = ranked.get(0);
		LOG.log(Level.FINE, "solved: {0}", best);
		return best;
	}

	/**
	 * Every permutation the constraints allow, scored and sorted best first.
	 * <p>
	 * Scoring is the expensive part and each permutation is independent of the others, so it runs
	 * in parallel. The list is what you want in order to measure a scorer — "how often is the true
	 * arrangement in the top k" — which used to be computed inline in the production path.
	 */
	public List<Result> rank(Algorithm algorithm) {
		List<byte[]> candidates = new ArrayList<byte[]>(PERMUTATIONS.length);
		for (byte[] permutation : PERMUTATIONS) {
			if (algorithm.accepts(permutation)) {
				candidates.add(permutation);
			}
		}

		LOG.log(Level.FINE, "scoring {0} of {1} permutations",
				new Object[] { Integer.valueOf(candidates.size()),
						Integer.valueOf(PERMUTATIONS.length) });

		List<Result> ranked = candidates.parallelStream()
				.map(a -> new Result(algorithm.calculateWeight(a), a.clone()))
				.sorted()
				.collect(java.util.stream.Collectors.toList());
		return ranked;
	}

	protected Collection<byte[][]> calculateBoundaries(byte[][] boundaries) {
		return Arrays.asList(new byte[][][] { boundaries });
	}

	private static byte[] identity() {
		return new byte[] { 0, 1, 2, 3, 4, 5 };
	}

	/** All 720 permutations of {0..5}, in lexicographic order. */
	private static byte[][] allPermutations() {
		List<byte[]> result = new ArrayList<byte[]>(720);
		byte[] a = identity();
		while (true) {
			result.add(a.clone());

			// rightmost index p with a[p] < a[p + 1]
			int p = -1;
			for (int i = a.length - 1; i >= 1; --i) {
				if (a[i - 1] < a[i]) {
					p = i - 1;
					break;
				}
			}
			if (p == -1) {
				return result.toArray(new byte[result.size()][]);
			}

			// smallest a[q] among a[p + 1..] that is still greater than a[p]
			byte min = Byte.MAX_VALUE;
			int q = 0;
			for (int i = p + 1; i < a.length; ++i) {
				if (min > a[i] && a[i] > a[p]) {
					min = a[i];
					q = i;
				}
			}

			byte tmp = a[p];
			a[p] = a[q];
			a[q] = tmp;

			for (int lo = p + 1, hi = a.length - 1; lo < hi; ++lo, --hi) {
				tmp = a[lo];
				a[lo] = a[hi];
				a[hi] = tmp;
			}
		}
	}
}
