package pixelbot.captcha.solver;

import pixelbot.captcha.algorithm.Algorithm.Result;
import pixelbot.captcha.algorithm.*;
import pixelbot.elements.ElementDescriptor;

public class TextureCaptchaSolver extends BaseCaptchaSolver {

	public TextureCaptchaSolver(byte[][] boundaries, ElementDescriptor[] elems) {
		super(boundaries, new FastDividedVarianceAlgorithm(elems));
	}

	@Override
	public Result solve() {
		return this.iterate(this.algorithms.get(0));
	}
}
