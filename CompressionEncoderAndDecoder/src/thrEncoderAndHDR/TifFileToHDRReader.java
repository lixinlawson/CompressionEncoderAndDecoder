package thrEncoderAndHDR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TifFileToHDRReader extends JFrame {

	private String filePath1;
	private String filePath2;
	private String filePath3;

	private TifFile file1 = null;
	private TifFile file2 = null;
	private TifFile file3 = null;

	private JLabel file1Label;
	private JLabel file2Label;
	private JLabel file3Label;

	public TifFileToHDRReader() {
		super("HDR For Three .tif Files");

		setLayout(new BorderLayout());
		add(createTitlePanel(), BorderLayout.WEST);
		add(createFilePanel(), BorderLayout.CENTER);
		add(createActionPanel(), BorderLayout.SOUTH);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createFilePanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(createSelectButtonPanel());
		panel.add(Box.createHorizontalGlue());
		panel.add(createFilePathPanel());
		panel.add(Box.createHorizontalGlue());
		return panel;
	}

	private JPanel createFilePathPanel() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(400, 200));
		file1Label = new JLabel("Select A .tif File With Button");
		file2Label = new JLabel("Select A .tif File With Button");
		file3Label = new JLabel("Select A .tif File With Button");
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(Box.createVerticalGlue());
		panel.add(file1Label);
		panel.add(Box.createVerticalGlue());
		panel.add(file2Label);
		panel.add(Box.createVerticalGlue());
		panel.add(file3Label);
		panel.add(Box.createVerticalGlue());

		return panel;
	}

	private JPanel createSelectButtonPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(Box.createVerticalGlue());
		panel.add(createFirstFileButton());
		panel.add(Box.createVerticalGlue());
		panel.add(createSecondFileButton());
		panel.add(Box.createVerticalGlue());
		panel.add(createThirdFileButton());
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	private JButton createFirstFileButton() {
		JButton openFile = new JButton("SELECT A TIF FILE");
		openFile.setForeground(Color.BLUE);
		openFile.setPreferredSize(new Dimension(240, 30));
		openFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser openFile = new JFileChooser(".//");
				int option = openFile.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					filePath1 = openFile.getSelectedFile().getAbsolutePath();
					file1 = new TifFile(filePath1);
					file1Label.setText(filePath1);
				}
			}

		});
		return openFile;
	}

	private JButton createSecondFileButton() {
		JButton openFile = new JButton("SELECT A TIF FILE");
		openFile.setForeground(Color.BLUE);
		openFile.setPreferredSize(new Dimension(240, 30));
		openFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser openFile = new JFileChooser(".//");
				int option = openFile.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					filePath2 = openFile.getSelectedFile().getAbsolutePath();
					file2 = new TifFile(filePath2);
					file2Label.setText(filePath2);
				}
			}

		});
		return openFile;
	}

	private JButton createThirdFileButton() {
		JButton openFile = new JButton("SELECT A TIF FILE");
		openFile.setForeground(Color.BLUE);
		openFile.setPreferredSize(new Dimension(240, 30));
		openFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser openFile = new JFileChooser(".//");
				int option = openFile.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					filePath3 = openFile.getSelectedFile().getAbsolutePath();
					file3 = new TifFile(filePath3);
					file3Label.setText(filePath3);
				}
			}

		});
		return openFile;
	}

	private JPanel createActionPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(Box.createHorizontalGlue());
		panel.add(createHDRButton(this));
		panel.add(Box.createHorizontalGlue());
		panel.add(createCloseButton());
		return panel;
	}

	private JButton createHDRButton(final TifFileToHDRReader hdr) {
		JButton displayHDR = new JButton("DISPLAY High Dynamic Range IMAGE");
		displayHDR.setForeground(Color.BLUE);
		displayHDR.setPreferredSize(new Dimension(300, 30));
		displayHDR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new HDRDisplayer(file1, file2, file3);
				hdr.dispose();
			}
		});
		return displayHDR;
	}

	private JButton createCloseButton() {
		JButton close = new JButton("CLOSE AND EXIT");
		close.setForeground(Color.RED);
		close.setPreferredSize(new Dimension(200, 30));
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		return close;
	}

	private JPanel createTitlePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBackground(Color.WHITE);
		JPanel subPanel = new JPanel();
		subPanel.setPreferredSize(new Dimension(180, 150));
		subPanel.setBackground(Color.WHITE);
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.PAGE_AXIS));
		
		subPanel.add(new JLabel(" @author"));
		subPanel.add(new JLabel(" Lawson Li"));

		panel.add(Box.createVerticalGlue());
		panel.add(subPanel);
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	public static void main(String[] args) {
		new TifFileToHDRReader();
	}
}
