package pixelbot.captcha;

import pixelbot.elements.ElementDescriptorBase;
import pixelbot.elements.PixelDescriptor;

/**
 * Dense ARGB view of one captcha tile.
 * <p>
 * {@link ElementDescriptorBase#getImage()} rasterises through the global {@code ElemTools.factor},
 * which the application sets to 1 at start-up but which defaults to 2. Reading a tile through that
 * image therefore samples the top-left quarter at double scale whenever the field has not been
 * reset yet — from a unit test, for instance. This reads the descriptor's own pixel list, so a tile
 * is always 1:1 and independent of global state.
 */
public final class TileImage {

	private final int width;
	private final int height;
	private final int[] argb;

	public TileImage(ElementDescriptorBase descriptor) {
		this.width = descriptor.getWidth();
		this.height = descriptor.getHeight();
		this.argb = new int[Math.max(this.width * this.height, 0)];

		for (PixelDescriptor pixel : descriptor) {
			if (pixel.offset_x >= 0 && pixel.offset_x < this.width && pixel.offset_y >= 0
					&& pixel.offset_y < this.height) {
				this.argb[pixel.offset_x + pixel.offset_y * this.width] = pixel.pixel_color;
			}
		}
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	/** Packed colour at (x, y), or 0 outside the tile. */
	public int rgb(int x, int y) {
		if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
			return 0;
		}
		return this.argb[x + y * this.width];
	}

	public int red(int x, int y) {
		return (this.rgb(x, y) >> 16) & 0xFF;
	}

	public int green(int x, int y) {
		return (this.rgb(x, y) >> 8) & 0xFF;
	}

	public int blue(int x, int y) {
		return this.rgb(x, y) & 0xFF;
	}

	/** Channel {@code c} (0 = red, 1 = green, 2 = blue) at (x, y). */
	public int channel(int x, int y, int c) {
		return (this.rgb(x, y) >> (16 - 8 * c)) & 0xFF;
	}
}
