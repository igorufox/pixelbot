package pixelbot.captcha.algorithm;

import java.awt.*;
import java.util.*;

import pixelbot.captcha.CaptchaConstants;
import pixelbot.captcha.solver.BaseCaptchaSolver;
import pixelbot.elements.ElementDescriptor;

/**
 * Scores an assembly by how much of the canvas the background can still reach.
 * <p>
 * Ink near a tile edge is extended a few pixels into the seam, then the outside is flood
 * filled from the corner. A correct assembly closes its contours, so less of the canvas
 * remains reachable; a wrong one leaves gaps the fill pours through. It is the cheapest
 * scorer here and the least precise.
 */
public class FillAlgorithm extends Algorithm {

	public FillAlgorithm(ElementDescriptor[] elems) {
		super(elems);
	}

	@Override
	protected long calculateWeight(BitSet matrix) {
		return FillAlgorithm.getFillMatrix(matrix).cardinality();
	}

	@Override
	public BitSet getMatrix(byte[] a) {
		BitSet matrix = super.getMatrix(a);

		for (Point p : CaptchaConstants.offsets) {

			for (int i = CaptchaConstants.elem_border; i < CaptchaConstants.elem_dimension
					+ CaptchaConstants.elem_border; ++i) {
				if (BaseCaptchaSolver.getPoint(matrix, p.x + CaptchaConstants.elem_border, p.y + i)) {
					for (int j = -CaptchaConstants.elem_intersection_width; j < CaptchaConstants.elem_intersection_width; ++j) {
						if (i + j > 0) {
							for (int k = 0; k < CaptchaConstants.elem_border; ++k) {
								BaseCaptchaSolver.setPoint(matrix, p.x + k, p.y + i + j);
							}
						}
					}
				}
				if (BaseCaptchaSolver.getPoint(matrix, p.x + CaptchaConstants.elem_border - 1
						+ CaptchaConstants.elem_dimension, p.y + i)) {
					for (int j = -CaptchaConstants.elem_intersection_width; j < CaptchaConstants.elem_intersection_width; ++j) {
						if (i + j >= 0
								&& i + j < CaptchaConstants.elem_border * 2 - 1
										+ CaptchaConstants.elem_dimension) {
							for (int k = 0; k < CaptchaConstants.elem_border; ++k) {
								int x = p.x + CaptchaConstants.elem_border
										+ CaptchaConstants.elem_dimension + k;
								if (x < CaptchaConstants.capcha_width) {
									BaseCaptchaSolver.setPoint(matrix, x, p.y + i + j);
								}
							}
						}
					}
				}
				if (BaseCaptchaSolver.getPoint(matrix, p.x + i, p.y + CaptchaConstants.elem_border)) {
					for (int j = -CaptchaConstants.elem_intersection_width; j < CaptchaConstants.elem_intersection_width; ++j) {
						if (i + j > 0) {
							for (int k = 0; k < CaptchaConstants.elem_border; ++k) {
								BaseCaptchaSolver.setPoint(matrix, p.x + i + j, p.y + k);
							}
						}
					}
				}
				if (BaseCaptchaSolver.getPoint(matrix, p.x + i, p.y + CaptchaConstants.elem_border
						- 1 + CaptchaConstants.elem_dimension)) {
					for (int j = -CaptchaConstants.elem_intersection_width; j < CaptchaConstants.elem_intersection_width; ++j) {
						if (i + j < CaptchaConstants.elem_border * 2 - 1
								+ CaptchaConstants.elem_dimension) {
							for (int k = 0; k < CaptchaConstants.elem_border; ++k) {
								BaseCaptchaSolver.setPoint(matrix, p.x + i + j, p.y
										+ CaptchaConstants.elem_border
										+ CaptchaConstants.elem_dimension + k);
							}
						}
					}
				}
			}

			for (int j = 0; j < CaptchaConstants.capcha_height; ++j) {
				for (int i = 0; i < CaptchaConstants.elem_border; ++i) {
					BaseCaptchaSolver.setPoint(matrix, i, j, false);
					BaseCaptchaSolver.setPoint(matrix, CaptchaConstants.capcha_width - 1 - i, j,
							false);
				}
			}

			for (int j = 0; j < CaptchaConstants.elem_border; ++j) {
				for (int i = 0; i < CaptchaConstants.capcha_width; ++i) {
					BaseCaptchaSolver.setPoint(matrix, i, j, false);
					BaseCaptchaSolver.setPoint(matrix, i, CaptchaConstants.capcha_height - 1 - j,
							false);
				}
			}
		}
		return matrix;
	}

	private static BitSet getFillMatrix(BitSet matrix) {
		BitSet result = new BitSet();

		Stack<Point> stack = new Stack<Point>();
		stack.push(new Point(0, 0));
		while (stack.size() > 0) {
			Point p = stack.pop();
			if (!BaseCaptchaSolver.getPoint(matrix, p.x, p.y)
					&& !BaseCaptchaSolver.getPoint(result, p.x, p.y)) {
				BaseCaptchaSolver.setPoint(result, p.x, p.y);

				if (p.x > 0)
					stack.push(new Point(p.x - 1, p.y));
				if (p.x < CaptchaConstants.capcha_width - 1)
					stack.push(new Point(p.x + 1, p.y));
				if (p.y > 0)
					stack.push(new Point(p.x, p.y - 1));
				if (p.y < CaptchaConstants.capcha_height - 1)
					stack.push(new Point(p.x, p.y + 1));
			}
		}

		return result;
	}

	private static BitSet getFillMatrix2(BitSet matrix) {
		BitSet result = new BitSet();

		for (int j = CaptchaConstants.elem_border; j < CaptchaConstants.capcha_height
				- CaptchaConstants.elem_border; ++j) {
			for (int i = -CaptchaConstants.elem_border; i < CaptchaConstants.elem_border; ++i) {
				if (BaseCaptchaSolver.getPoint(matrix, CaptchaConstants.elem_full_dimension + i, j)) {
					BaseCaptchaSolver.setPoint(result, CaptchaConstants.elem_full_dimension + i, j);
				}
				if (BaseCaptchaSolver.getPoint(matrix,
						CaptchaConstants.elem_full_dimension * 2 + i, j)) {
					BaseCaptchaSolver.setPoint(result,
							CaptchaConstants.elem_full_dimension * 2 + i, j);
				}
			}
		}

		for (int j = -CaptchaConstants.elem_border; j < CaptchaConstants.elem_border; ++j) {
			for (int i = CaptchaConstants.elem_border; i < CaptchaConstants.capcha_width
					- CaptchaConstants.elem_border; ++i) {
				if (BaseCaptchaSolver.getPoint(matrix, i, CaptchaConstants.elem_full_dimension + j)) {
					BaseCaptchaSolver.setPoint(result, i, CaptchaConstants.elem_full_dimension + j);
				}
			}
		}

		return result;
	}

	@Override
	public Collection<FillDescription> getFills(byte[] a) {
		Vector<FillDescription> result = new Vector<FillDescription>();

		result.addAll(super.getFills(a));

		result.add(new FillDescription("Fill", FillAlgorithm.getFillMatrix(this.getMatrix(a)),
				Color.yellow));

		result.add(new FillDescription("Fill intercepts", FillAlgorithm.getFillMatrix2(this
				.getMatrix(a)), Color.magenta));

		return result;
	}

}
