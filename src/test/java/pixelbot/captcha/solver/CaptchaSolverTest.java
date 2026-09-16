package pixelbot.captcha.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pixelbot.captcha.CaptchaFixtures;
import pixelbot.captcha.algorithm.Algorithm;
import pixelbot.captcha.algorithm.Algorithm.Result;
import pixelbot.captcha.algorithm.DevidedVarianceAlgorithm;
import pixelbot.captcha.algorithm.FastDividedVarianceAlgorithm;
import pixelbot.captcha.algorithm.seam.MahalanobisGradientAlgorithm;
import pixelbot.captcha.algorithm.seam.SeamDissimilarityAlgorithm;
import pixelbot.elements.ElementDescriptor;

class CaptchaSolverTest {

	@BeforeAll
	static void unscaledTiles() {
		CaptchaFixtures.useUnscaledTiles();
	}

	@Test
	void theSearchCoversEveryArrangementAndKeepsTies() {
		ElementDescriptor[] tiles = CaptchaFixtures.tiles(1, false);
		SeamCaptchaSolver solver = new SeamCaptchaSolver(null,
				new SeamDissimilarityAlgorithm(tiles));

		List<Result> ranked = solver.rank(new ConstantAlgorithm(tiles));

		assertEquals(720, ranked.size(), "6! arrangements, none dropped");

		// The old code collected results in a set keyed on the weight alone, so equal scores
		// silently collapsed. With every weight identical, that left exactly one entry.
		Set<String> distinct = new HashSet<String>();
		for (Result result : ranked) {
			distinct.add(java.util.Arrays.toString(result.getA()));
		}
		assertEquals(720, distinct.size(), "every arrangement must be distinct");
	}

	@Test
	void seamScorerReassemblesAShuffledPicture() {
		assertReassembles(false, tiles -> new SeamDissimilarityAlgorithm(tiles));
	}

	@Test
	void gradientScorerReassemblesAShuffledPicture() {
		assertReassembles(false, tiles -> new MahalanobisGradientAlgorithm(tiles));
	}

	private void assertReassembles(boolean lineArt,
			java.util.function.Function<ElementDescriptor[], Algorithm> scorer) {
		Random random = new Random(4242);

		for (int round = 0; round < 5; ++round) {
			ElementDescriptor[] correct = CaptchaFixtures.tiles(round + 1, lineArt);
			int[] order = CaptchaFixtures.randomOrder(random);
			ElementDescriptor[] scrambled = CaptchaFixtures.shuffle(correct, order);

			Result result = new SeamCaptchaSolver(null, scorer.apply(scrambled)).solve();
			assertNotNull(result.getA());

			for (int position = 0; position < 6; ++position) {
				assertEquals(position, order[result.getA()[position]],
						"round " + round + ": tile at grid position " + position);
			}
		}
	}

	@Test
	void theFastScorerAgreesWithTheReferenceOne() {
		ElementDescriptor[] tiles = CaptchaFixtures.tiles(7, true);

		DevidedVarianceAlgorithm reference = new DevidedVarianceAlgorithm(tiles);
		FastDividedVarianceAlgorithm fast = new FastDividedVarianceAlgorithm(tiles);

		Random random = new Random(11);
		for (int trial = 0; trial < 40; ++trial) {
			int[] order = CaptchaFixtures.randomOrder(random);
			byte[] a = new byte[6];
			for (int i = 0; i < 6; ++i) {
				a[i] = (byte) order[i];
			}

			long expected = reference.calculateWeight(a);
			long actual = fast.calculateWeight(a);

			// Same arithmetic in a different order, so only floating-point rounding may differ.
			assertTrue(Math.abs(expected - actual) <= Math.max(2, Math.abs(expected) / 1_000_000),
					"arrangement " + java.util.Arrays.toString(a) + ": expected " + expected
							+ " but the fast scorer said " + actual);
		}
	}

	/** Scores everything the same, to show the search keeps arrangements it cannot tell apart. */
	private static final class ConstantAlgorithm extends Algorithm {
		ConstantAlgorithm(ElementDescriptor[] elems) {
			super(elems);
		}

		@Override
		public long calculateWeight(byte[] a) {
			return 1;
		}
	}
}
