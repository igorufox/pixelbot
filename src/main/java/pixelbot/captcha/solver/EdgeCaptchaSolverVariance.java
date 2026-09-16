package pixelbot.captcha.solver;

import pixelbot.captcha.algorithm.*;
import pixelbot.elements.ElementDescriptor;

public class EdgeCaptchaSolverVariance extends EdgeCaptchaSolverBase {

	public EdgeCaptchaSolverVariance(byte[][] boundaries, ElementDescriptor[] elems) {
		super(boundaries, new FastDividedVarianceAlgorithm(elems));
	}

}
