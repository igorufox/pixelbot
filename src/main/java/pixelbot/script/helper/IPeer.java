package pixelbot.script.helper;

import java.awt.*;

public interface IPeer {
	public class Point3d {
		public int x;
		public int y;
		public int z;

		public Point3d(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Point3d) {
				Point3d other = (Point3d) obj;
				return this.x == other.x && this.y == other.y && this.z == other.z;
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		public Point3d copy() {
			return new Point3d(this.x, this.y, this.z);
		}
	}

	void dispose();

	void grabScreen(Rectangle rect);

	void grabScreen();

	int[] getPixels();

	void resetWorkingArea();

	void setWorkingArea(Rectangle r);

	Rectangle getWorkingArea();

	void mousePress(int button);

	void mouseRelease(int button);

	void mouseMove(int x, int y);

	Point3d getMousePosition();

	Point3d toDisplay3d(Point p);

	Point toDisplay2d(Point p);

	void keyPress(int keycode);

	void keyRelease(int keycode);

}
