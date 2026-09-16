package pixelbot.captcha.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Groups the ink of a tile into connected blobs.
 * <p>
 * The scorers here used commons-math's {@code DBSCANClusterer} with {@code minPts = 0}. With that
 * setting every point is a core point, so DBSCAN degenerates exactly into the connected components
 * of the graph that joins any two points within {@code eps} of each other — no density test, no
 * noise class. That is worth knowing, because the general clusterer compares every point against
 * every other: a 123x123 tile holds up to fifteen thousand ink pixels, so one tile cost on the
 * order of 2.3e8 distance computations, and six tiles of that dominated the whole solve — far more
 * than the 720 permutations the search was actually there to evaluate.
 * <p>
 * The points are not arbitrary, though: they sit on an integer grid. Two ink pixels are joined
 * exactly when they are within a fixed stencil of each other, so the components can be built by one
 * pass with a union-find, visiting only the half of the stencil that has already been scanned.
 * That is about forty checks per pixel — some six hundred thousand operations per tile instead of
 * two hundred and thirty million, and it produces the same components.
 */
public final class GridClusters {

	private GridClusters() {
	}

	/**
	 * Connected components of {@code mask}, joining pixels no further apart than {@code radius}.
	 *
	 * @param mask row-major, {@code width * height} entries
	 * @return one array of packed {@code x + y * width} indices per component, largest first is
	 *         not guaranteed; order follows the scan
	 */
	public static List<int[]> cluster(boolean[] mask, int width, int height, double radius) {
		if (width <= 0 || height <= 0 || mask.length < width * height) {
			return new ArrayList<int[]>();
		}

		int reach = (int) Math.floor(radius);
		double radiusSquared = radius * radius;

		// The half of the stencil already visited by a row-major scan.
		List<int[]> stencil = new ArrayList<int[]>();
		for (int dy = -reach; dy <= 0; ++dy) {
			for (int dx = -reach; dx <= reach; ++dx) {
				if (dy == 0 && dx >= 0) {
					continue;
				}
				if (dx * dx + dy * dy <= radiusSquared) {
					stencil.add(new int[] { dx, dy });
				}
			}
		}

		int[] parent = new int[width * height];
		java.util.Arrays.fill(parent, -1);

		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int index = x + y * width;
				if (!mask[index]) {
					continue;
				}
				parent[index] = index;

				for (int[] offset : stencil) {
					int nx = x + offset[0];
					int ny = y + offset[1];
					if (nx < 0 || ny < 0 || nx >= width || ny >= height) {
						continue;
					}
					int neighbour = nx + ny * width;
					if (mask[neighbour]) {
						union(parent, index, neighbour);
					}
				}
			}
		}

		// Collect the members of each root, keeping the roots in scan order.
		int[] sizes = new int[width * height];
		for (int index = 0; index < parent.length; ++index) {
			if (parent[index] >= 0) {
				sizes[find(parent, index)]++;
			}
		}

		int[] slot = new int[width * height];
		java.util.Arrays.fill(slot, -1);
		List<int[]> clusters = new ArrayList<int[]>();
		int[] filled = new int[width * height];

		for (int index = 0; index < parent.length; ++index) {
			if (parent[index] < 0) {
				continue;
			}
			int root = find(parent, index);
			if (slot[root] < 0) {
				slot[root] = clusters.size();
				clusters.add(new int[sizes[root]]);
			}
			int at = slot[root];
			clusters.get(at)[filled[at]++] = index;
		}

		return clusters;
	}

	private static int find(int[] parent, int index) {
		int root = index;
		while (parent[root] != root) {
			root = parent[root];
		}
		while (parent[index] != root) {
			int next = parent[index];
			parent[index] = root;
			index = next;
		}
		return root;
	}

	private static void union(int[] parent, int a, int b) {
		int rootA = find(parent, a);
		int rootB = find(parent, b);
		if (rootA != rootB) {
			parent[rootB] = rootA;
		}
	}
}
