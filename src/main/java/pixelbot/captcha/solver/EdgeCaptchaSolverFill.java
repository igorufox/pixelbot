package pixelbot.captcha.solver;

import pixelbot.captcha.algorithm.FillAlgorithm;
import pixelbot.elements.ElementDescriptor;

public class EdgeCaptchaSolverFill extends EdgeCaptchaSolverBase {

	public EdgeCaptchaSolverFill(byte[][] boundaries, ElementDescriptor[] elems) {
		super(boundaries, new FillAlgorithm(elems));
	}

}
