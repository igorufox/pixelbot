package pixelbot.captcha.algorithm;

/**
 * Sufficient statistics of a 2-D point cloud: count and the first and second moments.
 * <p>
 * Everything the variance-based scorers need can be derived from these six numbers, and they are
 * additive. That is what makes the fast scorers possible: the statistics of one tile at one grid
 * position are computed once, and scoring a permutation becomes a handful of additions instead of a
 * fresh scan of the whole canvas.
 * <p>
 * The sums stay exact in {@code long} arithmetic for any canvas this program can produce (a
 * 354x246 canvas gives at most ~1.1e10 for the squared terms).
 */
public final class MomentStats {

	private long n;
	private long sumX;
	private long sumY;
	private long sumXX;
	private long sumYY;
	private long sumXY;

	public void add(int x, int y) {
		this.n++;
		this.sumX += x;
		this.sumY += y;
		this.sumXX += (long) x * x;
		this.sumYY += (long) y * y;
		this.sumXY += (long) x * y;
	}

	public void add(MomentStats other) {
		this.n += other.n;
		this.sumX += other.sumX;
		this.sumY += other.sumY;
		this.sumXX += other.sumXX;
		this.sumYY += other.sumYY;
		this.sumXY += other.sumXY;
	}

	public void clear() {
		this.n = this.sumX = this.sumY = this.sumXX = this.sumYY = this.sumXY = 0;
	}

	public long count() {
		return this.n;
	}

	/**
	 * Bias-corrected variance of x, matching commons-math's {@code Variance}. Returns 0 for fewer
	 * than two points instead of NaN: the original code let that NaN reach {@code (long) result},
	 * where it becomes 0 — the best possible score — so a blank or single-pixel region silently won
	 * the search.
	 */
	public double varianceX() {
		return this.variance(this.sumX, this.sumXX);
	}

	public double varianceY() {
		return this.variance(this.sumY, this.sumYY);
	}

	/** Bias-corrected covariance of x and y; 0 for fewer than two points. */
	public double covariance() {
		if (this.n < 2) {
			return 0;
		}
		double mean = (double) this.sumX * this.sumY / this.n;
		return (this.sumXY - mean) / (this.n - 1);
	}

	/**
	 * Determinant of the covariance matrix, {@code Dx*Dy - cov^2} — the squared area of the
	 * dispersion ellipse. Small means the points form a thin, oriented structure, which is what a
	 * stroke continuing across a tile seam looks like.
	 */
	public double generalisedVariance() {
		double cov = this.covariance();
		return this.varianceX() * this.varianceY() - cov * cov;
	}

	private double variance(long sum, long sumSquares) {
		if (this.n < 2) {
			return 0;
		}
		double mean = (double) sum * sum / this.n;
		return (sumSquares - mean) / (this.n - 1);
	}
}
