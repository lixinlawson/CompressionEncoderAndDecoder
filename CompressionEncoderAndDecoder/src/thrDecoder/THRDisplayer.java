package thrDecoder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class THRDisplayer extends JFrame {

	PixelData[][] fileData1;
	PixelData[][] fileData2;
	PixelData[][] fileData3;

	public THRDisplayer(THRFile thrFile) {
		super("Three Images From .thr File");
		fileData1 = thrFile.getDataForFile1();
		fileData2 = thrFile.getDataForFile2();
		fileData3 = thrFile.getDataForFile3();

		setLayout(new BorderLayout());
		add(createImagePanel(), BorderLayout.CENTER);
		add(createBottomButtonPanel(), BorderLayout.SOUTH);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createImagePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(createPanelForImage(fileData1));
		panel.add(Box.createHorizontalGlue());
		panel.add(createPanelForImage(fileData2));
		panel.add(Box.createHorizontalGlue());
		panel.add(createPanelForImage(fileData3));
		panel.add(Box.createHorizontalGlue());
		return panel;
	}

	private JPanel createPanelForImage(PixelData[][] fileData) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.GRAY));
		panel.add(new ImagePanel(fileData));
		return panel;
	}

	private JPanel createBottomButtonPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(createOpenFileButton(this, false));
		panel.add(createCloseButton());
		return panel;
	}

	private JButton createOpenFileButton(final THRDisplayer displayer,
			final boolean fileError) {

		JButton openFile = new JButton("OPEN ANOTHER THR FILE");
		openFile.setForeground(Color.BLUE);
		if (fileError) {
			openFile.setForeground(Color.RED);
		}
		openFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser openFile = new JFileChooser(".//");
				int option = openFile.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					String path = openFile.getSelectedFile().getAbsolutePath();
					if (path.endsWith(".THR") || path.endsWith(".thr")) {
						THRFile thrFile = new THRFile(path);
						new THRDisplayer(thrFile);
					} else {
						new THRFileReader(true);
					}
					displayer.dispose();
				}
			}

		});
		return openFile;
	}

	private JButton createCloseButton() {
		JButton close = new JButton("CLOSE AND EXIT");
		close.setForeground(Color.RED);
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		return close;
	}
}
