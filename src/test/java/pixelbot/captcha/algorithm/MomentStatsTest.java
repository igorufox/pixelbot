package pixelbot.captcha.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.junit.jupiter.api.Test;

class MomentStatsTest {

	@Test
	void matchesCommonsMathForOrdinaryInput() {
		Random random = new Random(20140701);
		int n = 500;
		double[] xs = new double[n];
		double[] ys = new double[n];
		MomentStats stats = new MomentStats();

		for (int i = 0; i < n; ++i) {
			int x = random.nextInt(354);
			int y = random.nextInt(246);
			xs[i] = x;
			ys[i] = y;
			stats.add(x, y);
		}

		assertEquals(n, stats.count());
		assertEquals(new Variance().evaluate(xs), stats.varianceX(), 1e-6);
		assertEquals(new Variance().evaluate(ys), stats.varianceY(), 1e-6);
		assertEquals(new Covariance().covariance(xs, ys), stats.covariance(), 1e-6);
	}

	@Test
	void degenerateInputScoresZeroRatherThanNaN() {
		// The original computed these through commons-math, where an empty sample yields NaN.
		// (long) NaN is 0, the lowest possible weight, so a blank region beat every real one.
		MomentStats empty = new MomentStats();
		assertEquals(0, empty.varianceX());
		assertEquals(0, empty.varianceY());
		assertEquals(0, empty.covariance());
		assertEquals(0, empty.generalisedVariance());

		MomentStats single = new MomentStats();
		single.add(17, 42);
		assertEquals(0, single.generalisedVariance());
	}

	@Test
	void statisticsAreAdditive() {
		Random random = new Random(7);
		MomentStats whole = new MomentStats();
		MomentStats left = new MomentStats();
		MomentStats right = new MomentStats();

		for (int i = 0; i < 300; ++i) {
			int x = random.nextInt(100);
			int y = random.nextInt(100);
			whole.add(x, y);
			(i % 2 == 0 ? left : right).add(x, y);
		}

		MomentStats combined = new MomentStats();
		combined.add(left);
		combined.add(right);

		assertEquals(whole.count(), combined.count());
		assertEquals(whole.generalisedVariance(), combined.generalisedVariance(), 1e-6);
	}

	@Test
	void generalisedVarianceIsSmallForACollinearCloud() {
		MomentStats line = new MomentStats();
		MomentStats blob = new MomentStats();
		for (int i = 0; i < 100; ++i) {
			line.add(i, 2 * i);
			blob.add(i % 10, i / 10);
		}

		// A straight stroke has no area; a 10x10 patch spreads over roughly 9.17 * 9.17.
		assertEquals(0, line.generalisedVariance(), 1e-6);
		org.junit.jupiter.api.Assertions.assertTrue(blob.generalisedVariance() > 50,
				"a square cloud should have a large dispersion ellipse, got "
						+ blob.generalisedVariance());
	}
}
