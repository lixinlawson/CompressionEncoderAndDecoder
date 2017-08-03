package thrDecoder;

import java.awt.Color;

public class PixelData {

	private int r;
	private int g;
	private int b;

	private int y;
	private int cu;
	private int cv;

	private Color color;

	public PixelData(int valueOfY, int valueOfCu, int vlaueOfCv) {
		y = valueOfY;
		cu = valueOfCu;
		cv = vlaueOfCv;
		setRGB();
		setColors();
	}

	private void setRGB() {
		g = y - ((cu + cv) / 4);
		r = cv + g;
		b = cu + g;
		if (r < 0) {
			r = 0;
		} else if (r > 255) {
			r = 255;
		}
		if (g < 0) {
			g = 0;
		} else if (g > 255) {
			g = 255;
		}
		if (b < 0) {
			b = 0;
		} else if (b > 255) {
			b = 255;
		}
	}

	public Color getColor() {
		return color;
	}

	public int getRedLevel() {
		return r;
	}

	public int getGreenLevel() {
		return g;
	}

	public int getBlueLevel() {
		return b;
	}

	public int getY() {
		return y;
	}

	public int getCu() {
		return cu;
	}

	public int getCv() {
		return cv;
	}

	private void setColors() {
		color = new Color(r, g, b);
	}

}
