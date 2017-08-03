package wavHuffmanCoding;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class HuffmanSummaryPanel extends JPanel {

	public HuffmanSummaryPanel(HuffmanTree huffmanTree) {
		setBasic();
		addTitle();
		add(createSummaryPanel(huffmanTree), BorderLayout.CENTER);
	}

	private JPanel createSummaryPanel(HuffmanTree huffmanTree) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JLabel codeLength = new JLabel(
				String.format(
						" Initial C.W. Length:  %d bits     Huffman C.W. Length:  %.6f bits",
						huffmanTree.getInitialCodeWordLength(),
						huffmanTree.getHuffmanCodeWordLength()));
		JLabel codeCompRatio = new JLabel(String.format(
				" Data Compression Ratio:  %.6f",
				huffmanTree.getDataCompressionRatio()));

		JLabel fileLength = new JLabel(
				String.format(
						" Original File Size: %d bytes     Compressed File Size: %d bytes",
						huffmanTree.getOriginalFileSize(),
						huffmanTree.getCompressedFileSize()));
		JLabel talbleAndData = new JLabel(String.format(
				" Table Size: %d bytes     Data Size: %d bytes",
				huffmanTree.getTableSize(), huffmanTree.getDataSize()));
		JLabel fileCompRatio = new JLabel(String.format(
				" File Compression Ratio:  %.6f",
				huffmanTree.getFileCompressionRatio()));

		codeLength.setFont(new Font("Serif", Font.BOLD, 16));
		fileLength.setFont(new Font("Serif", Font.BOLD, 16));
		talbleAndData.setFont(new Font("Serif", Font.BOLD, 16));
		codeCompRatio.setFont(new Font("Serif", Font.BOLD, 16));
		fileCompRatio.setFont(new Font("Serif", Font.BOLD, 16));

		panel.add(codeLength);
		panel.add(codeCompRatio);
		panel.add(fileLength);
		panel.add(talbleAndData);
		panel.add(fileCompRatio);

		return panel;
	}

	private void setBasic() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.GRAY));
	}

	private void addTitle() {
		JLabel title = createTitle("Huffman Coding Compression Result");
		add(title, BorderLayout.NORTH);
	}

	private JLabel createTitle(String titleName) {
		JLabel title = new JLabel(titleName);
		title.setForeground(Color.BLUE);
		return title;
	}
}
