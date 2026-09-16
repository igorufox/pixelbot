package pixelbot.captcha.solver;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import pixelbot.captcha.CaptchaConstants;
import pixelbot.captcha.algorithm.seam.MahalanobisGradientAlgorithm;
import pixelbot.captcha.algorithm.seam.SeamDissimilarityAlgorithm;
import pixelbot.elements.*;
import pixelbot.elements.IElementDescriptor.ColorType;

/**
 * Builds a solver for a puzzle kind, and cuts a captcha image into its six tiles.
 */
public class CaptchaSolverFactory {

	private static final Logger LOG = Logger.getLogger(CaptchaSolverFactory.class.getName());

	/**
	 * A solver for the named puzzle kind.
	 * <p>
	 * "edge", "tile", "texture" and "relic" are the original mask-based scorers, kept because they
	 * are tuned to a particular source of images. "seam" and "mgc" work on colour instead and need
	 * no tuning: prefer "mgc" for anything new.
	 *
	 * @return the solver, or null when the kind is unknown
	 */
	public static BaseCaptchaSolver produce(String type, byte[][] boundaries,
			ElementDescriptor[] elems) {
		if (type == null || elems == null || elems.length < 6) {
			LOG.log(Level.WARNING, "cannot build a solver for type {0}", type);
			return null;
		}

		switch (type) {
		case "edge":
			return new EdgeCaptchaSolverBorder(boundaries, elems);
		case "tile":
			return new TileCaptchaSolver(boundaries, elems);
		case "texture":
		case "relic":
			return new TextureCaptchaSolver(boundaries, elems);
		case "seam":
			return new SeamCaptchaSolver(boundaries, new SeamDissimilarityAlgorithm(elems));
		case "mgc":
			return new SeamCaptchaSolver(boundaries, new MahalanobisGradientAlgorithm(elems));
		default:
			LOG.log(Level.WARNING, "unknown captcha type {0}", type);
			return null;
		}
	}

	public static ElementDescriptor[] parseDescriptor(BufferedImage image) {
		final int s = CaptchaConstants.elem_dimension;
		final int b = CaptchaConstants.elem_border;

		ElementDescriptor[] result = new ElementDescriptor[6];

		if (image == null) {
			return null;
		}

		// Find the grid's top-left corner by walking in from the edges until the divider colour
		// appears. The scan is bounded: an image that does not contain the grid used to run off
		// the end and be reported as a generic exception.
		int color = CaptchaConstants.divide_color.getRGB();
		int probeRow = Math.min(50, image.getHeight() - 1);
		int probeColumn = Math.min(50, image.getWidth() - 1);
		if (probeRow < 0 || probeColumn < 0) {
			return null;
		}

		int x = 0;
		while (x < image.getWidth() && color != image.getRGB(x, probeRow)) {
			++x;
		}
		int y = 0;
		while (y < image.getHeight() && color != image.getRGB(probeColumn, y)) {
			++y;
		}

		if (x + s * 3 + b * 6 > image.getWidth() || y + s * 2 + b * 4 > image.getHeight()) {
			LOG.log(Level.FINE, "no captcha grid in a {0}x{1} image", new Object[] {
					Integer.valueOf(image.getWidth()), Integer.valueOf(image.getHeight()) });
			return null;
		}

		result[0] = CaptchaSolverFactory.subElement(image, x + b, y + b, s, s);
		result[1] = CaptchaSolverFactory.subElement(image, x + s + b * 3, y + b, s, s);
		result[2] = CaptchaSolverFactory.subElement(image, x + s * 2 + b * 5, y + b, s, s);
		result[3] = CaptchaSolverFactory.subElement(image, x + b, y + s + b * 3, s, s);
		result[4] = CaptchaSolverFactory.subElement(image, x + s + b * 3, y + s + b * 3, s, s);
		result[5] = CaptchaSolverFactory.subElement(image, x + s * 2 + b * 5, y + s + b * 3, s, s);

		return result;
	}

	public static BufferedImage generateDescriptor(ElementDescriptor[] elems) {
		BufferedImage result = new BufferedImage(CaptchaConstants.capcha_width,
				CaptchaConstants.capcha_height, BufferedImage.TYPE_INT_ARGB);

		Graphics g = result.getGraphics();
		g.drawImage(elems[0].getImage(), 0 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, 0 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, CaptchaConstants.elem_dimension,
				CaptchaConstants.elem_dimension, null);
		g.drawImage(elems[1].getImage(), 1 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, 0 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, CaptchaConstants.elem_dimension,
				CaptchaConstants.elem_dimension, null);
		g.drawImage(elems[2].getImage(), 2 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, 0 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, CaptchaConstants.elem_dimension,
				CaptchaConstants.elem_dimension, null);
		g.drawImage(elems[3].getImage(), 0 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, 1 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, CaptchaConstants.elem_dimension,
				CaptchaConstants.elem_dimension, null);
		g.drawImage(elems[4].getImage(), 1 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, 1 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, CaptchaConstants.elem_dimension,
				CaptchaConstants.elem_dimension, null);
		g.drawImage(elems[5].getImage(), 2 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, 1 * CaptchaConstants.elem_full_dimension
				+ CaptchaConstants.elem_border, CaptchaConstants.elem_dimension,
				CaptchaConstants.elem_dimension, null);

		g.setColor(CaptchaConstants.divide_color);

		g.drawLine(0, 0, 0, CaptchaConstants.capcha_height);

		g.drawLine(CaptchaConstants.elem_full_dimension - 1, 0,
				CaptchaConstants.elem_full_dimension - 1, CaptchaConstants.capcha_height);
		g.drawLine(CaptchaConstants.elem_full_dimension, 0, CaptchaConstants.elem_full_dimension,
				CaptchaConstants.capcha_height);

		g.drawLine(CaptchaConstants.elem_full_dimension * 2 - 1, 0,
				CaptchaConstants.elem_full_dimension * 2 - 1, CaptchaConstants.capcha_height);
		g.drawLine(CaptchaConstants.elem_full_dimension * 2, 0,
				CaptchaConstants.elem_full_dimension * 2, CaptchaConstants.capcha_height);

		g.drawLine(CaptchaConstants.elem_full_dimension * 3 - 1, 0,
				CaptchaConstants.elem_full_dimension * 3 - 1, CaptchaConstants.capcha_height);

		g.drawLine(0, 0, CaptchaConstants.capcha_width, 0);

		g.drawLine(0, CaptchaConstants.elem_full_dimension - 1, CaptchaConstants.capcha_width,
				CaptchaConstants.elem_full_dimension - 1);
		g.drawLine(0, CaptchaConstants.elem_full_dimension, CaptchaConstants.capcha_width,
				CaptchaConstants.elem_full_dimension);

		g.drawLine(0, CaptchaConstants.elem_full_dimension * 2 - 1, CaptchaConstants.capcha_width,
				CaptchaConstants.elem_full_dimension * 2 - 1);

		return result;
	}

	private static ElementDescriptor subElement(BufferedImage image, int x, int y, int width,
			int heigth) {
		ElementDescriptor ed = new ElementDescriptor(width, heigth,
				EnumSet.allOf(IElementDescriptor.Locale.class), ColorType.CT_MULTICOLOR, 0,
				new Date());

		for (int i = 0; i < ed.getWidth(); ++i) {
			for (int j = 0; j < ed.getHeight(); ++j) {
				ed.addPixel(i, j, image.getRGB(i + x, j + y));
			}
		}
		return ed;
	}
}
