package pixelbot.legacy;

import java.io.*;

public class PixelDescriptor implements Serializable {
	static final long serialVersionUID = 67348L;
	private int offset_x;
	private int offset_y;
	private int pixel_color;

	private Object readResolve() {
		return new pixelbot.elements.PixelDescriptor(this.offset_x, this.offset_y,
				this.pixel_color);
	}
}
