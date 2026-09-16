package pixelbot.legacy;

import java.io.Serializable;
import java.util.*;

import pixelbot.elements.*;
import pixelbot.elements.IElementDescriptor.ColorType;

public class ElementDescriptor implements Serializable {
	static final long serialVersionUID = 67347L;

	private Vector<pixelbot.elements.PixelDescriptor> vec = new Vector<pixelbot.elements.PixelDescriptor>();
	private int center_x;
	private int center_y;
	@SuppressWarnings("unused")
	private int type = 0;
	private int color_type = 0;

	private Object readResolve() {
		return new pixelbot.elements.ElementDescriptor(this.vec, this.center_x * 2, this.center_y * 2,
				EnumSet.allOf(IElementDescriptor.Locale.class),
				ColorType.values()[this.color_type], 0, new Date());
	}
}
