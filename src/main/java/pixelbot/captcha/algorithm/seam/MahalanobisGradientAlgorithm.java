package pixelbot.captcha.algorithm.seam;

import pixelbot.captcha.TileImage;
import pixelbot.elements.ElementDescriptor;

/**
 * Mahalanobis Gradient Compatibility: seam cost measured against how each tile's own colour
 * gradients behave near that edge.
 * <p>
 * Plain colour difference (see {@link SeamDissimilarityAlgorithm}) asks "are these two edges the
 * same colour?", which flat regions answer yes to no matter what they are glued to. The question
 * worth asking is "is the step across this seam the kind of step this picture normally takes?" —
 * a picture is full of colour changes, and a real edge continues the statistics of its
 * neighbourhood rather than holding still.
 * <p>
 * For a left-right seam between tiles <em>i</em> and <em>j</em>:
 * <ol>
 * <li>Take the gradient just inside i's right edge, {@code G(y) = i[w-1,y] - i[w-2,y]}, over all
 * rows, and summarise it as a mean and a 3x3 colour covariance.</li>
 * <li>Take the actual step across the seam, {@code S(y) = j[0,y] - i[w-1,y]}.</li>
 * <li>Cost is the Mahalanobis distance of S from that distribution, summed over rows: a step is
 * cheap when it matches the direction and spread of changes i was already making.</li>
 * </ol>
 * The measure is asymmetric, so the cost of the pair adds the same quantity computed from j's side
 * looking back. Because the covariance is a full 3x3 it also captures the correlation between
 * channels, which is what lets it tell a genuine continuation from a coincidental colour match.
 * <p>
 * This is Gallagher's compatibility measure (CVPR 2012), the thing that made square-piece jigsaw
 * solvers work in practice. It needs no training data and no thresholds, and it costs one pass
 * over 60 tile edges.
 */
public class MahalanobisGradientAlgorithm extends SeamAlgorithm {

	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final int TOP = 2;
	private static final int BOTTOM = 3;

	/**
	 * Ridge added to the covariance diagonal. Flat edges produce a singular covariance — every
	 * gradient is exactly zero — and without this the inverse does not exist and the distance
	 * blows up to infinity for any neighbour at all.
	 */
	private static final double RIDGE = 1.0;

	/** Mean and inverse covariance of the gradients along one edge of one tile. */
	private static final class EdgeStats {
		private final double[] mean = new double[3];
		private final double[][] inverseCovariance = new double[3][3];
	}

	private EdgeStats[][] edges;

	public MahalanobisGradientAlgorithm(ElementDescriptor[] elems) {
		super(elems);
	}

	@Override
	protected void prepare() {
		this.edges = new EdgeStats[this.tiles.length][4];
		for (int t = 0; t < this.tiles.length; ++t) {
			TileImage tile = this.tiles[t];
			this.edges[t][LEFT] = stats(gradients(tile, LEFT));
			this.edges[t][RIGHT] = stats(gradients(tile, RIGHT));
			this.edges[t][TOP] = stats(gradients(tile, TOP));
			this.edges[t][BOTTOM] = stats(gradients(tile, BOTTOM));
		}
	}

	@Override
	protected double costRightOf(TileImage left, TileImage right) {
		int i = this.indexOf(left);
		int j = this.indexOf(right);
		int height = Math.min(left.getHeight(), right.getHeight());
		if (height <= 0 || left.getWidth() < 2 || right.getWidth() < 2) {
			return 0;
		}

		int lastColumn = left.getWidth() - 1;
		double[][] forward = new double[height][3];
		double[][] backward = new double[height][3];
		for (int y = 0; y < height; ++y) {
			for (int c = 0; c < 3; ++c) {
				int step = right.channel(0, y, c) - left.channel(lastColumn, y, c);
				forward[y][c] = step;
				backward[y][c] = -step;
			}
		}

		return distance(forward, this.edges[i][RIGHT]) + distance(backward, this.edges[j][LEFT]);
	}

	@Override
	protected double costBelow(TileImage top, TileImage bottom) {
		int i = this.indexOf(top);
		int j = this.indexOf(bottom);
		int width = Math.min(top.getWidth(), bottom.getWidth());
		if (width <= 0 || top.getHeight() < 2 || bottom.getHeight() < 2) {
			return 0;
		}

		int lastRow = top.getHeight() - 1;
		double[][] forward = new double[width][3];
		double[][] backward = new double[width][3];
		for (int x = 0; x < width; ++x) {
			for (int c = 0; c < 3; ++c) {
				int step = bottom.channel(x, 0, c) - top.channel(x, lastRow, c);
				forward[x][c] = step;
				backward[x][c] = -step;
			}
		}

		return distance(forward, this.edges[i][BOTTOM]) + distance(backward, this.edges[j][TOP]);
	}

	private int indexOf(TileImage tile) {
		for (int i = 0; i < this.tiles.length; ++i) {
			if (this.tiles[i] == tile) {
				return i;
			}
		}
		throw new IllegalArgumentException("tile does not belong to this algorithm");
	}

	/**
	 * Colour gradients just inside one edge of a tile, pointing outwards: the changes the picture
	 * was making as it approached that edge.
	 */
	private static double[][] gradients(TileImage tile, int side) {
		int w = tile.getWidth();
		int h = tile.getHeight();
		if (w < 2 || h < 2) {
			return new double[0][3];
		}

		int length = (side == LEFT || side == RIGHT) ? h : w;
		double[][] result = new double[length][3];
		for (int k = 0; k < length; ++k) {
			for (int c = 0; c < 3; ++c) {
				switch (side) {
				case LEFT:
					result[k][c] = tile.channel(0, k, c) - tile.channel(1, k, c);
					break;
				case RIGHT:
					result[k][c] = tile.channel(w - 1, k, c) - tile.channel(w - 2, k, c);
					break;
				case TOP:
					result[k][c] = tile.channel(k, 0, c) - tile.channel(k, 1, c);
					break;
				default:
					result[k][c] = tile.channel(k, h - 1, c) - tile.channel(k, h - 2, c);
					break;
				}
			}
		}
		return result;
	}

	private static EdgeStats stats(double[][] samples) {
		EdgeStats result = new EdgeStats();
		int n = samples.length;
		if (n == 0) {
			for (int c = 0; c < 3; ++c) {
				result.inverseCovariance[c][c] = 1 / RIDGE;
			}
			return result;
		}

		for (double[] sample : samples) {
			for (int c = 0; c < 3; ++c) {
				result.mean[c] += sample[c];
			}
		}
		for (int c = 0; c < 3; ++c) {
			result.mean[c] /= n;
		}

		double[][] covariance = new double[3][3];
		if (n > 1) {
			for (double[] sample : samples) {
				for (int a = 0; a < 3; ++a) {
					for (int b = 0; b < 3; ++b) {
						covariance[a][b] += (sample[a] - result.mean[a])
								* (sample[b] - result.mean[b]);
					}
				}
			}
			for (int a = 0; a < 3; ++a) {
				for (int b = 0; b < 3; ++b) {
					covariance[a][b] /= n - 1;
				}
			}
		}
		for (int c = 0; c < 3; ++c) {
			covariance[c][c] += RIDGE;
		}

		invert3x3(covariance, result.inverseCovariance);
		return result;
	}

	/** Sum over samples of the Mahalanobis distance from the edge's gradient distribution. */
	private static double distance(double[][] samples, EdgeStats stats) {
		if (samples.length == 0) {
			return 0;
		}

		double total = 0;
		double[] d = new double[3];
		for (double[] sample : samples) {
			for (int c = 0; c < 3; ++c) {
				d[c] = sample[c] - stats.mean[c];
			}
			for (int a = 0; a < 3; ++a) {
				for (int b = 0; b < 3; ++b) {
					total += d[a] * stats.inverseCovariance[a][b] * d[b];
				}
			}
		}
		return total / samples.length;
	}

	/**
	 * Inverse of a 3x3 matrix through its adjugate. The ridge above keeps the determinant away
	 * from zero; should it get there anyway, fall back to a scaled identity, which turns the
	 * distance back into a plain sum of squared differences rather than producing infinities.
	 */
	private static void invert3x3(double[][] m, double[][] out) {
		double c00 = m[1][1] * m[2][2] - m[1][2] * m[2][1];
		double c01 = m[1][2] * m[2][0] - m[1][0] * m[2][2];
		double c02 = m[1][0] * m[2][1] - m[1][1] * m[2][0];

		double det = m[0][0] * c00 + m[0][1] * c01 + m[0][2] * c02;
		if (!Double.isFinite(det) || Math.abs(det) < 1e-9) {
			for (int a = 0; a < 3; ++a) {
				for (int b = 0; b < 3; ++b) {
					out[a][b] = a == b ? 1 / RIDGE : 0;
				}
			}
			return;
		}

		out[0][0] = c00 / det;
		out[1][0] = c01 / det;
		out[2][0] = c02 / det;
		out[0][1] = (m[0][2] * m[2][1] - m[0][1] * m[2][2]) / det;
		out[1][1] = (m[0][0] * m[2][2] - m[0][2] * m[2][0]) / det;
		out[2][1] = (m[0][1] * m[2][0] - m[0][0] * m[2][1]) / det;
		out[0][2] = (m[0][1] * m[1][2] - m[0][2] * m[1][1]) / det;
		out[1][2] = (m[0][2] * m[1][0] - m[0][0] * m[1][2]) / det;
		out[2][2] = (m[0][0] * m[1][1] - m[0][1] * m[1][0]) / det;
	}
}
