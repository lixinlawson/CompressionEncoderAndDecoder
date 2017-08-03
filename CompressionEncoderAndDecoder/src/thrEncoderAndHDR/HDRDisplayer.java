package thrEncoderAndHDR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class HDRDisplayer extends JFrame {

	private static final double GOLDEN_RATIO = 0.618;
	private TifFile file1;
	private TifFile file2;
	private TifFile file3;

	public HDRDisplayer(TifFile file1, TifFile file2, TifFile file3) {
		super("Three Original .tiff Images And HDR Image");
		this.file1 = file1;
		this.file2 = file2;
		this.file3 = file3;
		setLayout(new BorderLayout());
		add(createCentrePanel(), BorderLayout.CENTER);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	};

	private JPanel createCentrePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(createOriginalImagePanel());
		panel.add(createHDRIandCompPanel());
		return panel;
	}

	private JPanel createHDRIandCompPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(createHDRImagePanel());
		panel.add(Box.createHorizontalGlue());
		panel.add(createTHRPanel());
		panel.add(Box.createHorizontalGlue());
		return panel;
	}

	private JPanel createHDRImagePanel() {
		JPanel panel = new JPanel();
		PixelData[][] dataHDR = setDataForHDR();
		panel.add(createHDRPanel(dataHDR));

		return panel;
	}

	private PixelData[][] setDataForHDR() {
		int width = file1.getWidthOfImage();
		if (width > file2.getWidthOfImage()) {
			width = file2.getWidthOfImage();
		} else if (width > file3.getWidthOfImage()) {
			width = file3.getWidthOfImage();
		}

		int height = file1.getHeightOfImage();
		if (height > file2.getHeightOfImage()) {
			height = file2.getHeightOfImage();
		} else if (width > file3.getHeightOfImage()) {
			height = file3.getHeightOfImage();
		}

		PixelData[][] dataHDR = new PixelData[height][width];
		PixelData[][] dataFile1 = file1.getPixelData();
		PixelData[][] dataFile2 = file2.getPixelData();
		PixelData[][] dataFile3 = file3.getPixelData();

		double hdrRed, hdrGreen, hdrBlue;

		PixelData file1Pixel;
		int redFile1, greenFile1, blueFile1;

		PixelData file2Pixel;
		int redFile2, greenFile2, blueFile2;

		PixelData file3Pixel;
		int redFile3, greenFile3, blueFile3;

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				file1Pixel = dataFile1[row][col];
				redFile1 = file1Pixel.getRedLevel();
				greenFile1 = file1Pixel.getGreenLevel();
				blueFile1 = file1Pixel.getBlueLevel();

				file2Pixel = dataFile2[row][col];
				redFile2 = file1Pixel.getRedLevel();
				greenFile2 = file2Pixel.getRedLevel();
				blueFile2 = file2Pixel.getGreenLevel();

				file3Pixel = dataFile3[row][col];
				redFile3 = file3Pixel.getRedLevel();
				greenFile3 = file3Pixel.getGreenLevel();
				blueFile3 = file3Pixel.getBlueLevel();

				hdrRed = redFile1;
				hdrGreen = greenFile1;
				hdrBlue = blueFile1;

				hdrRed += redFile2;
				hdrGreen += greenFile2;
				hdrBlue += blueFile2;

				hdrRed += redFile3;
				hdrGreen += greenFile3;
				hdrBlue += blueFile3;

				hdrRed = hdrRed / GOLDEN_RATIO / 3;
				hdrGreen = hdrGreen / GOLDEN_RATIO / 3;
				hdrBlue = hdrBlue / GOLDEN_RATIO / 3;

				dataHDR[row][col] = new PixelData(hdrRed, hdrGreen, hdrBlue);
				if (dataHDR[row][col].getY() > 255 * GOLDEN_RATIO) {
					dataHDR[row][col] = new PixelData(
							dataHDR[row][col].getRedLevel() * 0.9,
							dataHDR[row][col].getGreenLevel() * 0.9,
							dataHDR[row][col].getBlueLevel() * 0.9);
				}

			}
		}
		return dataHDR;
	}

	private JPanel createHDRPanel(PixelData[][] dataHDR) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.GRAY));
		JLabel title = createTitle("High Dynamic Range Image");
		panel.add(title, BorderLayout.NORTH);
		panel.add(new ImagePanel(dataHDR));
		return panel;
	}

	private JPanel createTHRPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(Box.createVerticalGlue());
		panel.add(createTHRCompressionButton());
		panel.add(Box.createVerticalGlue());
		panel.add(createOtherTiffButton(this));
		panel.add(Box.createVerticalGlue());
		panel.add(createCloseButton());
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	private JButton createTHRCompressionButton() {
		JButton compress = new JButton("COMPRESS TO A .thr FILE");
		compress.setForeground(Color.BLUE);
		compress.setMaximumSize(new Dimension(300, 40));
		compress.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				THREncoder encoder = new THREncoder(file1, file2, file3);
				String name = getStringOfCurrentTime() + ".thr";
				encoder.writeToFile(name);
				new SavedAsThrFile(name, encoder.getCompressionRatio());
			}
		});
		return compress;
	}

	private JButton createOtherTiffButton(final HDRDisplayer displayer) {
		JButton other = new JButton("OPEN OTHER THREE .tiff FILES");
		other.setForeground(Color.BLUE);
		other.setMaximumSize(new Dimension(300, 40));
		other.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new TifFileToHDRReader();
				displayer.dispose();
			}
		});
		return other;
	}

	private JButton createCloseButton() {
		JButton close = new JButton("CLOSE AND EXIT");
		close.setForeground(Color.RED);
		close.setMaximumSize(new Dimension(300, 40));
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		return close;
	}

	private JPanel createOriginalImagePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(createPanelForImage(file1));
		panel.add(Box.createHorizontalGlue());
		panel.add(createPanelForImage(file2));
		panel.add(Box.createHorizontalGlue());
		panel.add(createPanelForImage(file3));
		panel.add(Box.createHorizontalGlue());
		return panel;
	}

	private JPanel createPanelForImage(TifFile file) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.GRAY));
		JLabel title = createTitle(file.getFilePath());
		panel.add(title, BorderLayout.NORTH);
		panel.add(new ImagePanel(file));
		return panel;
	}

	private JLabel createTitle(String titleName) {
		JLabel title = new JLabel(titleName);
		title.setForeground(Color.BLUE);
		return title;
	}

	private String getStringOfCurrentTime() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Calendar cal = Calendar.getInstance();
		return format.format(cal.getTime());
	}

}
