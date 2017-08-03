package wavHuffmanCoding;

import java.util.Comparator;

public class HuffmanTreeNodeComparator implements Comparator<HuffmanTreeNode> {

	@Override
	public int compare(HuffmanTreeNode node1, HuffmanTreeNode node2) {
		int freqCount1 = node1.getFrequencyCount();
		int freqCount2 = node2.getFrequencyCount();
		return freqCount1 - freqCount2;
	}
}
