package pixelbot.captcha;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;

import pixelbot.elements.ElementDescriptor;

public class CaptchaElementDescriptor {

	private ElementDescriptor orig;
	private BitSet matrix = new BitSet();

	public CaptchaElementDescriptor(ElementDescriptor elem) {
		this.orig = elem;
		this.initMatrix();

	}

	private void setPoint(int x, int y) {
		this.setPoint(x, y, true);
	}

	private void setPoint(int x, int y, boolean value) {
		this.matrix.set(x + y * CaptchaConstants.elem_full_dimension, value);
	}

	public boolean getPoint(int x, int y) {
		return this.matrix.get(x + y * CaptchaConstants.elem_full_dimension);
	}

	private void initMatrix() {

		BufferedImage image = this.orig.getImage();

		for (int j = 0; j < CaptchaConstants.elem_dimension; ++j) {
			for (int i = 0; i < CaptchaConstants.elem_dimension; ++i) {
				Color c = new Color(image.getRGB(i, j));

				if (c.getRed() < CaptchaConstants.sensivity
						&& c.getGreen() < CaptchaConstants.sensivity
						&& c.getBlue() < CaptchaConstants.sensivity) {
					this.setPoint(i + CaptchaConstants.elem_border, j
							+ CaptchaConstants.elem_border);
				}
			}
		}

		
	}
}
