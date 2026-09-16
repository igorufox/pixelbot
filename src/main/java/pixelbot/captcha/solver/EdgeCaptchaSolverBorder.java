package pixelbot.captcha.solver;

import pixelbot.captcha.algorithm.BorderClusterAlgorithm;
import pixelbot.elements.ElementDescriptor;

public class EdgeCaptchaSolverBorder extends EdgeCaptchaSolverBase {

	public EdgeCaptchaSolverBorder(byte[][] boundaries, ElementDescriptor[] elems) {
		super(boundaries, new BorderClusterAlgorithm(elems));
	}

}
