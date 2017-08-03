package wavHuffmanCoding;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class HuffmanCodePanel extends JPanel {
	private static final int NUM_OF_COL = 3;

	private static final String[] TITLE_DATA = { "Sample Value",
			"Sample Frequency", "Huffman Code" };

	private ArrayList<HuffmanTreeNode> leafNodeList;

	public HuffmanCodePanel(HuffmanTree huffmanTree) {
		leafNodeList = huffmanTree.getAllLeafNodes();
		setBasic();
		addTitle();
		add(createPanelForEncodedData(), BorderLayout.CENTER);
	}

	private JPanel createPanelForEncodedData() {
		JPanel subPanel = new JPanel();
		int nunOfRows = leafNodeList.size() + 1;
		subPanel.setBackground(Color.WHITE);
		subPanel.setLayout(new GridLayout(nunOfRows, NUM_OF_COL));
		for (int row = 0; row < nunOfRows; row++) {
			for (int col = 0; col < NUM_OF_COL; col++) {
				JLabel content = new JLabel();
				content.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				if (row == 0) {
					content.setText(TITLE_DATA[col]);
				} else {
					if (col == 0) {
						content.setText(String.format("%d",
								leafNodeList.get(row - 1).getLeafValue()));
					} else if (col == 1) {
						content.setText(String.format("%d",
								leafNodeList.get(row - 1).getFrequencyCount()));
					} else if (col == 2) {
						content.setText(leafNodeList.get(row - 1)
								.getHuffmanCode());
					}
				}
				content.setAlignmentX(Component.CENTER_ALIGNMENT);
				subPanel.add(content);
			}
		}

		JScrollPane scrollPane = new JScrollPane(subPanel);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 10, 500, 200);

		JPanel panel = new JPanel(null);
		panel.setPreferredSize(new Dimension(520, 220));
		panel.add(scrollPane);

		return panel;
	}

	private void setBasic() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
				Color.BLACK, Color.GRAY));
	}

	private void addTitle() {
		JLabel title = createTitle("Huffman Code For Each Sample Value");
		add(title, BorderLayout.NORTH);
	}

	private JLabel createTitle(String titleName) {
		JLabel title = new JLabel(titleName);
		title.setForeground(Color.BLUE);
		return title;
	}

}
