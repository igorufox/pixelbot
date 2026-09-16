package pixelbot.captcha.algorithm;

import java.awt.*;
import java.util.*;

import pixelbot.captcha.*;
import pixelbot.captcha.solver.BaseCaptchaSolver;
import pixelbot.elements.ElementDescriptor;

/**
 * Scores one candidate assembly of the six captcha tiles: lower is better.
 * <p>
 * The search itself lives in {@link BaseCaptchaSolver}; an algorithm only has to say how good a
 * given permutation looks. Scorers that work on the assembled black-and-white canvas override
 * {@link #calculateWeight(BitSet)}; scorers that work from precomputed per-tile data — see
 * {@link FastDividedVarianceAlgorithm} and the seam scorers — override
 * {@link #calculateWeight(byte[])} directly and never build the canvas at all.
 */
public abstract class Algorithm {

	/** A scored permutation. {@code v} is the weight, {@code a} maps grid position to tile. */
	public static class Result implements Comparable<Result> {
		private long v;
		private byte[] a;

		public Result(long v, byte[] a) {
			this.v = v;
			this.a = a;
		}

		public long getV() {
			return this.v;
		}

		public byte[] getA() {
			return this.a;
		}

		/**
		 * Orders by weight, then by the permutation itself. The tie-break matters: results used to
		 * be collected in a TreeSet keyed on the weight alone, which silently discarded every
		 * permutation that happened to score the same as one already seen.
		 */
		@Override
		public int compareTo(Result o) {
			int byWeight = Long.compare(this.v, o.v);
			if (byWeight != 0) {
				return byWeight;
			}
			if (this.a == null || o.a == null) {
				return this.a == o.a ? 0 : (this.a == null ? -1 : 1);
			}
			return Arrays.compare(this.a, o.a);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Result && this.compareTo((Result) obj) == 0;
		}

		@Override
		public int hashCode() {
			return Long.hashCode(this.v) * 31 + Arrays.hashCode(this.a);
		}

		@Override
		public String toString() {
			return this.v + ": " + (this.a == null ? "<none>" : Arrays.toString(this.a));
		}
	}

	/** A coloured overlay the debug UI paints on top of an assembly. */
	public static class FillDescription implements Comparable<FillDescription> {

		private String id;
		private BitSet matrix;
		private Color color;

		public FillDescription(String id, BitSet matrix, Color color) {
			this.id = id;
			this.matrix = matrix;
			this.color = color;
		}

		public BitSet getMatrix() {
			return this.matrix;
		}

		public Color getColor() {
			return this.color;
		}

		@Override
		public int compareTo(FillDescription o) {
			return this.id.compareTo(o.id);
		}

		@Override
		public String toString() {
			return this.id;
		}
	}

	private Collection<byte[][]> boundaries = null;

	protected CaptchaElementDescriptor[] ceds = new CaptchaElementDescriptor[6];

	public Algorithm(ElementDescriptor[] elems) {
		Objects.requireNonNull(elems, "elems");
		if (elems.length < 6) {
			throw new IllegalArgumentException("a captcha needs 6 tiles, got " + elems.length);
		}
		for (int i = 0; i < 6; ++i) {
			this.ceds[i] = new CaptchaElementDescriptor(
					Objects.requireNonNull(elems[i], "tile " + i));
		}
	}

	public void setBoundaries(Collection<byte[][]> boundaries) {
		this.boundaries = boundaries;
	}

	public boolean getXY(byte pos, int x, int y) {
		return this.ceds[pos].getPoint(x, y);
	}

	/**
	 * Whether the permutation survives the boundary constraints. Cheap, so the search applies it
	 * before it scores anything.
	 */
	public boolean accepts(byte[] a) {
		if (this.boundaries == null || this.boundaries.isEmpty()) {
			return true;
		}
		for (byte[][] boundary : this.boundaries) {
			if (Algorithm.matches(a, boundary)) {
				return true;
			}
		}
		return false;
	}

	private static boolean matches(byte[] a, byte[][] boundary) {
		if (boundary == null || boundary.length != 6) {
			return true;
		}
		for (int i = 0; i < 6; ++i) {
			if (boundary[i] == null) {
				continue;
			}
			boolean found = false;
			for (byte b : boundary[i]) {
				if (b == a[i]) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Weight of a permutation. The default assembles the canvas and defers to
	 * {@link #calculateWeight(BitSet)}; override this instead when the score can be computed
	 * without building the canvas.
	 */
	public long calculateWeight(byte[] a) {
		return this.calculateWeight(this.getMatrix(a));
	}

	protected long calculateWeight(BitSet matrix) {
		throw new UnsupportedOperationException(this.getClass().getName()
				+ " must override calculateWeight(byte[]) or calculateWeight(BitSet)");
	}

	/** The assembled canvas for a permutation, one bit per pixel, set where the tile has ink. */
	public BitSet getMatrix(byte[] a) {
		BitSet result = new BitSet(CaptchaConstants.capcha_width * CaptchaConstants.capcha_height);

		for (int k = 0; k < CaptchaConstants.offsets.length; ++k) {
			Point offset = CaptchaConstants.offsets[k];
			for (int j = 0; j < CaptchaConstants.elem_full_dimension; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_full_dimension; ++i) {
					BaseCaptchaSolver.setPoint(result, offset.x + i, offset.y + j,
							this.ceds[a[k]].getPoint(i, j));
				}
			}
		}

		return result;
	}

	public Collection<FillDescription> getFills(final byte[] a) {
		return Arrays.asList(new FillDescription[] { new FillDescription("Base", this.getMatrix(a),
				Color.red) });
	}
}
