package pixelbot.captcha.solver;

import java.awt.Point;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import pixelbot.captcha.*;
import pixelbot.captcha.algorithm.*;
import pixelbot.captcha.algorithm.Algorithm.Result;

/**
 * Adds edge constraints before the search runs.
 * <p>
 * Which column a tile can sit in is visible from the tile alone: one whose ink never
 * reaches its left edge cannot have a neighbour on that side, and so on. Working that out
 * costs a scan of four tile borders and throws away most of the 720 arrangements before any
 * of them is scored — constraint propagation ahead of search, and the reason this solver
 * finishes in a fraction of the time the unconstrained ones take.
 */
public abstract class EdgeCaptchaSolverBase extends BaseCaptchaSolver {

	private static final Logger LOG = Logger.getLogger(EdgeCaptchaSolverBase.class
			.getName());

	public EdgeCaptchaSolverBase(byte[][] boundaries, Algorithm... algorithms) {
		super(boundaries, algorithms);
	}

	@Override
	public Result solve() {
		return this.iterate(this.algorithms.get(0));
	}

	private boolean getXY(byte pos, int x, int y) {
		return this.algorithms.get(0).getXY(pos, x, y);
	}

	@Override
	protected Collection<byte[][]> calculateBoundaries(byte[][] boundaries) {

		byte[][] result = new byte[6][];
		if (boundaries != null) {
			for (int i = 0; i < 6; ++i) {
				result[i] = boundaries[i];
			}
		}

		if (result[0] == null)
			result[0] = this.getBoundary0();
		if (result[1] == null)
			result[1] = this.getBoundary1();
		if (result[2] == null)
			result[2] = this.getBoundary2();
		if (result[4] == null)
			result[4] = this.getBoundary4();

		LOG.log(Level.FINE, "edge constraints: {0}", Arrays.deepToString(result));
		return Arrays.asList(new byte[][][] { result });
	}

	private byte[] getBoundary0() {
		ArrayList<Byte> pre_res = new ArrayList<Byte>();
		for (byte k = 0; k < 6; ++k) {
			for (int j = 0; j < CaptchaConstants.elem_dimension; ++j) {
				if (this.getXY(k, CaptchaConstants.elem_dimension - 1
						+ CaptchaConstants.elem_border, j + CaptchaConstants.elem_border)) {
					pre_res.add(Byte.valueOf(k));
					break;
				}
			}
		}

		byte[] res_array = new byte[pre_res.size()];
		for (int i = 0; i < res_array.length; ++i) {
			res_array[i] = pre_res.get(i).byteValue();
		}
		return res_array;
	}

	private byte[] getBoundary2() {
		ArrayList<Byte> pre_res = new ArrayList<Byte>();
		for (byte k = 0; k < 6; ++k) {
			for (int j = 0; j < CaptchaConstants.elem_dimension; ++j) {
				if (this.getXY(k, CaptchaConstants.elem_border, j + CaptchaConstants.elem_border)) {
					pre_res.add(Byte.valueOf(k));
					break;
				}
			}
		}

		byte[] res_array = new byte[pre_res.size()];
		for (int i = 0; i < res_array.length; ++i) {
			res_array[i] = pre_res.get(i).byteValue();
		}
		return res_array;
	}

	private byte[] getBoundary1() {
		ArrayList<Byte> pre_res = new ArrayList<Byte>();
		for (byte k = 0; k < 6; ++k) {
			BitSet fillmatrix = new BitSet();
			Stack<Point> stack = new Stack<Point>();
			for (int j = 0; j < CaptchaConstants.elem_dimension; ++j) {
				if (this.getXY(k, CaptchaConstants.elem_border, j + CaptchaConstants.elem_border)) {
					stack.push(new Point(0, j));
				}
			}
			boolean r = false;
			while (stack.size() > 0) {
				Point p = stack.pop();

				if (this.getXY(k, p.x + CaptchaConstants.elem_border, p.y
						+ CaptchaConstants.elem_border)
						&& p.x == CaptchaConstants.elem_dimension - 1) {
					r = true;
					break;
				}
				if (this.getXY(k, p.x + CaptchaConstants.elem_border, p.y
						+ CaptchaConstants.elem_border)
						&& !BaseCaptchaSolver.getPoint(fillmatrix, p.x, p.y)) {
					BaseCaptchaSolver.setPoint(fillmatrix, p.x, p.y);

					if (p.x > 0)
						stack.push(new Point(p.x - 1, p.y));
					if (p.x < CaptchaConstants.elem_dimension - 1)
						stack.push(new Point(p.x + 1, p.y));
					if (p.y > 0)
						stack.push(new Point(p.x, p.y - 1));
					if (p.y < CaptchaConstants.elem_dimension - 1)
						stack.push(new Point(p.x, p.y + 1));
				}
			}
			if (r) {
				pre_res.add(Byte.valueOf(k));
			}
		}

		ArrayList<Byte> res = new ArrayList<Byte>();
		for (Byte b : pre_res) {
			int t_weight = 0;
			int b_weight = 0;

			for (int j = 0; j < CaptchaConstants.elem_dimension / 2; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_dimension; ++i) {
					if (this.getXY(b.byteValue(), i, j)) {
						t_weight++;
					}
				}
			}
			for (int j = CaptchaConstants.elem_dimension / 2; j < CaptchaConstants.elem_dimension; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_dimension; ++i) {
					if (this.getXY(b.byteValue(), i, j)) {
						b_weight++;
					}
				}
			}
			if (b_weight > t_weight) {
				res.add(b);
			}
		}

		if (res.size() == 0)
			res = pre_res;

		byte[] res_array = new byte[res.size()];
		for (int i = 0; i < res_array.length; ++i) {
			res_array[i] = res.get(i).byteValue();
		}
		return res_array;
	}

	private byte[] getBoundary4() {
		ArrayList<Byte> pre_res = new ArrayList<Byte>();
		for (byte k = 0; k < 6; ++k) {
			boolean r = false;

			BitSet fillmatrix = new BitSet();
			Stack<Point> stack = new Stack<Point>();

			for (int j = 0; j < CaptchaConstants.elem_dimension; ++j) {
				if (this.getXY(k, CaptchaConstants.elem_border, j + CaptchaConstants.elem_border)) {
					stack.push(new Point(0, j));
				}
			}
			while (stack.size() > 0) {
				Point p = stack.pop();

				if (this.getXY(k, p.x + CaptchaConstants.elem_border, p.y
						+ CaptchaConstants.elem_border)
						&& p.x == CaptchaConstants.elem_dimension - 1) {
					r = true;
					break;
				}
				if (this.getXY(k, p.x + CaptchaConstants.elem_border, p.y
						+ CaptchaConstants.elem_border)
						&& p.y == 0 && p.x > CaptchaConstants.elem_dimension * .70) {
					r = true;
					break;
				}
				if (this.getXY(k, p.x + CaptchaConstants.elem_border, p.y
						+ CaptchaConstants.elem_border)
						&& !BaseCaptchaSolver.getPoint(fillmatrix, p.x, p.y)) {
					BaseCaptchaSolver.setPoint(fillmatrix, p.x, p.y);

					if (p.x > 0)
						stack.push(new Point(p.x - 1, p.y));
					if (p.x < CaptchaConstants.elem_dimension - 1)
						stack.push(new Point(p.x + 1, p.y));
					if (p.y > 0)
						stack.push(new Point(p.x, p.y - 1));
					if (p.y < CaptchaConstants.elem_dimension - 1)
						stack.push(new Point(p.x, p.y + 1));
				}
			}
			if (r) {
				pre_res.add(Byte.valueOf(k));
				continue;
			}
			fillmatrix = new BitSet();
			stack = new Stack<Point>();

			for (int j = 0; j < CaptchaConstants.elem_dimension; ++j) {
				if (this.getXY(k, CaptchaConstants.elem_dimension - 1
						+ CaptchaConstants.elem_border, j + CaptchaConstants.elem_border)) {
					stack.push(new Point(CaptchaConstants.elem_dimension - 1, j));
				}
			}
			while (stack.size() > 0) {
				Point p = stack.pop();

				if (this.getXY(k, p.x + CaptchaConstants.elem_border, p.y
						+ CaptchaConstants.elem_border)
						&& p.x == 0) {
					r = true;
					break;
				}
				if (this.getXY(k, p.x + CaptchaConstants.elem_border, p.y
						+ CaptchaConstants.elem_border)
						&& p.y == 0 && p.x < CaptchaConstants.elem_dimension * .30) {
					r = true;
					break;
				}

				if (this.getXY(k, p.x + CaptchaConstants.elem_border, p.y
						+ CaptchaConstants.elem_border)
						&& !BaseCaptchaSolver.getPoint(fillmatrix, p.x, p.y)) {
					BaseCaptchaSolver.setPoint(fillmatrix, p.x, p.y);

					if (p.x > 0)
						stack.push(new Point(p.x - 1, p.y));
					if (p.x < CaptchaConstants.elem_dimension - 1)
						stack.push(new Point(p.x + 1, p.y));
					if (p.y > 0)
						stack.push(new Point(p.x, p.y - 1));
					if (p.y < CaptchaConstants.elem_dimension - 1)
						stack.push(new Point(p.x, p.y + 1));
				}
			}
			if (r) {
				pre_res.add(Byte.valueOf(k));
				continue;
			}

		}

		ArrayList<Byte> res = new ArrayList<Byte>();
		for (Byte b : pre_res) {
			int t_weight = 0;
			int b_weight = 0;

			for (int j = 0; j < CaptchaConstants.elem_dimension / 2; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_dimension; ++i) {
					if (this.getXY(b.byteValue(), i, j)) {
						t_weight++;
					}
				}
			}
			for (int j = CaptchaConstants.elem_dimension / 2; j < CaptchaConstants.elem_dimension; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_dimension; ++i) {
					if (this.getXY(b.byteValue(), i, j)) {
						b_weight++;
					}
				}
			}
			if (b_weight < t_weight + 200) {
				res.add(b);
			}
		}

		if (res.size() == 0)
			res = pre_res;

		byte[] res_array = new byte[res.size()];
		for (int i = 0; i < res_array.length; ++i) {
			res_array[i] = res.get(i).byteValue();
		}
		return res_array;
	}

}
