package wavHuffmanCoding;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class HuffmanDisplayer extends JFrame {

	private HuffmanTree huffmanTree;

	public HuffmanDisplayer(WavFile wavFile) {
		super("Huffman Coding Result For Seleted File");

		huffmanTree = new HuffmanTree(wavFile);
		setLayout(new BorderLayout());

		add(new HuffmanSummaryPanel(huffmanTree), BorderLayout.NORTH);
		add(new HuffmanCodePanel(huffmanTree), BorderLayout.CENTER);
		add(createButtonPanel(this), BorderLayout.SOUTH);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtonPanel(final HuffmanDisplayer huffmanDisplayer) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(createOpenFileButton(this, false));
		panel.add(createCloseButton());
		return panel;
	}

	private JButton createOpenFileButton(
			final HuffmanDisplayer huffmanDisplayer, final boolean fileError) {

		JButton openFile = new JButton("OPEN ANOTHER WAV FILE");
		openFile.setForeground(Color.BLUE);
		if (fileError) {
			openFile.setForeground(Color.RED);
		}
		openFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser openFile = new JFileChooser(".//");// current
																// directory
				int option = openFile.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION) {
					String path = openFile.getSelectedFile().getAbsolutePath();

					if (path.endsWith(".WAV") || path.endsWith(".wav")) {
						WavFile wavFile = new WavFile(path);
						new HuffmanDisplayer(wavFile);
					} else {
						new EncoderForWav(true);
					}
					huffmanDisplayer.dispose();
				}
			}

		});
		return openFile;
	}

	private JButton createCloseButton() {
		JButton close = new JButton(" CLOSE AND EXIT ");
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
