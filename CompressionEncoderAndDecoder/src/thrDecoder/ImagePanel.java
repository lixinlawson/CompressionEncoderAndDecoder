package thrDecoder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

	private static final int MARGIN = 20;

	private PixelData[][] pixelData;
	private int width;
	private int height;

	public ImagePanel(PixelData[][] data) {
		// constructor for pixel data
		pixelData = data;
		width = data[0].length;
		height = data.length;

		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(width + 2 * MARGIN, height + 2 * MARGIN));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D point = (Graphics2D) g;
		PixelData temp;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				temp = pixelData[row][col];
				point.setColor(temp.getColor());
				point.drawLine(col + MARGIN, row + MARGIN, col + MARGIN, row
						+ MARGIN);
			}
		}
	}
}
