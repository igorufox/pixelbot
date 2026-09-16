package pixelbot.captcha;

import java.awt.*;

public class CaptchaConstants {

	public static final short sensivity = 230;
	public static final byte elem_intersection_width = 7;
	public static final byte elem_dimension = 118;
	public static final byte elem_border = 5;

	public static final short elem_full_dimension = elem_dimension + 2 * elem_border;
	public static final short capcha_width = elem_full_dimension * 3;
	public static final short capcha_height = elem_full_dimension * 2;

	public static final byte thumb_dimension = 40;

	public static final Point[] offsets = new Point[] {
			new Point(0 * CaptchaConstants.elem_full_dimension,
					0 * CaptchaConstants.elem_full_dimension),
			new Point(1 * CaptchaConstants.elem_full_dimension,
					0 * CaptchaConstants.elem_full_dimension),
			new Point(2 * CaptchaConstants.elem_full_dimension,
					0 * CaptchaConstants.elem_full_dimension),
			new Point(0 * CaptchaConstants.elem_full_dimension,
					1 * CaptchaConstants.elem_full_dimension),
			new Point(1 * CaptchaConstants.elem_full_dimension,
					1 * CaptchaConstants.elem_full_dimension),
			new Point(2 * CaptchaConstants.elem_full_dimension,
					1 * CaptchaConstants.elem_full_dimension) };

	public static final Color divide_color = new Color(176, 122, 66);
}
