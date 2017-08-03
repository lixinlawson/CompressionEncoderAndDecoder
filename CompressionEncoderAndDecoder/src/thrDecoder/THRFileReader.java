package thrDecoder;

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
public class THRFileReader extends JFrame {

	public THRFileReader(boolean fileError) {

		super(" Decoder For .thr");
		setLayout(new BorderLayout());
		if (fileError) {
			add(createErrorPanel(), BorderLayout.NORTH);
		}
		add(createTitlePanel(), BorderLayout.WEST);
		add(createButtonPanel(fileError), BorderLayout.CENTER);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtonPanel(boolean fileError) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(Box.createVerticalGlue());
		panel.add(createOpenFileButton(this, fileError));
		panel.add(Box.createVerticalGlue());
		panel.add(createCloseButton());
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	private JButton createOpenFileButton(final THRFileReader wavFileReader,
			final boolean fileError) {

		JButton openFile = new JButton("OPEN A THR FILE");
		openFile.setForeground(Color.BLUE);
		openFile.setPreferredSize(new Dimension(200, 60));
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
					wavFileReader.dispose();
				}
			}

		});
		return openFile;
	}

	private JButton createCloseButton() {
		JButton close = new JButton("CLOSE AND EXIT");
		close.setForeground(Color.RED);
		close.setPreferredSize(new Dimension(200, 60));
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
		subPanel.add(new JLabel("CMPT 365 Spring 2015"));
		subPanel.add(new JLabel("Project II Part II"));
		subPanel.add(new JLabel("Prof.: J.C. Liu"));
		subPanel.add(new JLabel("T.A.: Qiyun He"));
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.PAGE_AXIS));
		subPanel.add(new JLabel("@author"));
		subPanel.add(new JLabel("Li, Xin (Lawson)"));
		subPanel.add(new JLabel("301095790"));
		subPanel.add(new JLabel("lixinl@sfu.ca"));

		panel.add(Box.createVerticalGlue());
		panel.add(subPanel);
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	private JPanel createErrorPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBackground(Color.RED);
		JLabel error = new JLabel(
				"ERROR: The file you selected is NOT a thr file !");
		JLabel msg = new JLabel("Only .thr or .THR file can be selected !");
		error.setForeground(Color.WHITE);
		msg.setForeground(Color.WHITE);
		panel.add(error);
		panel.add(msg);
		return panel;
	}

	public static void main(String[] args) {
		new THRFileReader(false);
	}
}
