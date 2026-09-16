package pixelbot.captcha.solver;

import pixelbot.captcha.algorithm.Algorithm;
import pixelbot.captcha.algorithm.Algorithm.Result;

/**
 * Runs the exhaustive search against any single scorer, with no extra constraints.
 * <p>
 * The seam scorers need this: they score a permutation from precomputed pairwise tables, so the
 * search has nothing left to prune and no per-tile preprocessing to do.
 */
public class SeamCaptchaSolver extends BaseCaptchaSolver {

	public SeamCaptchaSolver(byte[][] boundaries, Algorithm algorithm) {
		super(boundaries, algorithm);
	}

	@Override
	public Result solve() {
		return this.iterate(this.algorithms.get(0));
	}
}
