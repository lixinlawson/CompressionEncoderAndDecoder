package thrEncoderAndHDR;

import java.awt.Color;

public class PixelData {

	private static final int BINARY_BASE = 2;

	private int r;
	private int g;
	private int b;
	private int grayLevel;

	private int y;
	private int cu;
	private int cv;

	private Color color;
	private Color grayColor;

	public PixelData(byte red, byte green, byte blue) {
		r = convertByteToDecimal(red);
		g = convertByteToDecimal(green);
		b = convertByteToDecimal(blue);

		setYCuCv();
		calculateGrayLevel();
		setColors();
	}

	public PixelData(int valueOfY, int valueOfCu, int vlaueOfCv) {
		y = valueOfY;
		cu = valueOfCu;
		cv = vlaueOfCv;
		setRGB();
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
		calculateGrayLevel();
		setColors();
	}

	private void setRGB() {
		g = y - ((cu + cv) / 4);
		r = cv + g;
		b = cu + g;
	}

	public PixelData(double red, double green, double blue) {

		if (red > 255) {
			r = 255;
		} else if (red < 0) {
			r = 0;
		} else {
			r = (int) Math.round(red);
		}

		if (green > 255) {
			g = 255;
		} else if (green < 0) {
			g = 0;
		} else {
			g = (int) Math.round(green);
		}

		if (blue > 255) {
			b = 255;
		} else if (blue < 0) {
			b = 0;
		} else {
			b = (int) Math.round(blue);
		}
		
		
		setYCuCv();
		calculateGrayLevel();
		setColors();
	}

	private void setYCuCv() {
		cv = r - g;
		cu = b - g;
		y = g + ((cu + cv) / 4);
	}

	public Color getColor() {
		return color;
	}

	public Color getGrayColor() {
		return grayColor;
	}

	public int getGrayLevel() {
		return grayLevel;
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
		grayColor = new Color(grayLevel, grayLevel, grayLevel);
		// same for R G B values
	}

	private void calculateGrayLevel() {
		grayLevel = (int) Math.round(0.299 * r + 0.587 * g + 0.114 * b);
	}

	private int convertByteToDecimal(byte sampleByte) {
		String binaryString = convertByteToBinaryString(sampleByte);
		return Integer.parseInt(binaryString, BINARY_BASE);
	}

	private String convertByteToBinaryString(byte sampleByte) {
		return String.format("%8s", Integer.toBinaryString(sampleByte & 0xFF))
				.replace(' ', '0');
	}

}
