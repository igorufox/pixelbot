package pixelbot.captcha.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.junit.jupiter.api.Test;

class GridClustersTest {

	private static final double RADIUS = 4;

	@Test
	void producesTheSameGroupingAsTheClustererItReplaced() {
		Random random = new Random(2014);
		int size = 48;
		boolean[] mask = new boolean[size * size];

		// A few blobs plus speckle, which is what a denoised tile looks like.
		for (int blob = 0; blob < 5; ++blob) {
			int cx = 6 + random.nextInt(size - 12);
			int cy = 6 + random.nextInt(size - 12);
			for (int dy = -4; dy <= 4; ++dy) {
				for (int dx = -4; dx <= 4; ++dx) {
					if (dx * dx + dy * dy <= 16) {
						mask[(cx + dx) + (cy + dy) * size] = true;
					}
				}
			}
		}
		for (int speck = 0; speck < 40; ++speck) {
			mask[random.nextInt(size * size)] = true;
		}

		List<int[]> mine = GridClusters.cluster(mask, size, size, RADIUS);

		// minPts = 0 makes every point a core point, so DBSCAN reduces to connected components.
		List<DoublePoint> points = new ArrayList<DoublePoint>();
		for (int index = 0; index < mask.length; ++index) {
			if (mask[index]) {
				points.add(new DoublePoint(new int[] { index % size, index / size }));
			}
		}
		List<Cluster<DoublePoint>> reference = new DBSCANClusterer<DoublePoint>(RADIUS, 0)
				.cluster(points);

		assertEquals(asKeys(reference, size), asOwnKeys(mine),
				"the grid clustering must partition the ink exactly as DBSCAN did");
	}

	@Test
	void everyInkPixelLandsInExactlyOneCluster() {
		Random random = new Random(99);
		int width = 30;
		int height = 20;
		boolean[] mask = new boolean[width * height];
		int ink = 0;
		for (int i = 0; i < mask.length; ++i) {
			mask[i] = random.nextInt(4) == 0;
			if (mask[i]) {
				ink++;
			}
		}

		Set<Integer> seen = new HashSet<Integer>();
		for (int[] cluster : GridClusters.cluster(mask, width, height, RADIUS)) {
			for (int index : cluster) {
				assertTrue(mask[index], "a cluster must only contain ink");
				assertTrue(seen.add(Integer.valueOf(index)), "clusters must not overlap");
			}
		}
		assertEquals(ink, seen.size(), "every ink pixel belongs to some cluster");
	}

	@Test
	void separatedBlobsStaySeparate() {
		int size = 40;
		boolean[] mask = new boolean[size * size];
		mask[2 + 2 * size] = true;
		mask[3 + 2 * size] = true;
		// Ten pixels away, well beyond the radius.
		mask[20 + 20 * size] = true;

		List<int[]> clusters = GridClusters.cluster(mask, size, size, RADIUS);
		assertEquals(2, clusters.size());
	}

	private static Set<Set<Integer>> asOwnKeys(List<int[]> clusters) {
		Set<Set<Integer>> result = new HashSet<Set<Integer>>();
		for (int[] cluster : clusters) {
			Set<Integer> members = new HashSet<Integer>();
			for (int index : cluster) {
				members.add(Integer.valueOf(index));
			}
			result.add(members);
		}
		return result;
	}

	private static Set<Set<Integer>> asKeys(List<Cluster<DoublePoint>> clusters, int width) {
		Set<Set<Integer>> result = new HashSet<Set<Integer>>();
		for (Cluster<DoublePoint> cluster : clusters) {
			Set<Integer> members = new HashSet<Integer>();
			for (DoublePoint point : cluster.getPoints()) {
				members.add(Integer.valueOf((int) point.getPoint()[0]
						+ (int) point.getPoint()[1] * width));
			}
			result.add(members);
		}
		return result;
	}
}
