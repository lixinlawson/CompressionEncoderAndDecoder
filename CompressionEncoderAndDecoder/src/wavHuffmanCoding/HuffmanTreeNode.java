package wavHuffmanCoding;

public class HuffmanTreeNode {

	private HuffmanTreeNode leftChild;
	private HuffmanTreeNode rightChild;
	private HuffmanTreeNode parent;

	private int leafValue; // leaf value is the value sample value
	private int freqCount;
	private String code;
	public HuffmanTreeNode(int value, int count) {
		leafValue = value;
		freqCount = count;
		leftChild = null;
		rightChild = null;
		parent = null;
		code = "";
	}

	public HuffmanTreeNode(HuffmanTreeNode leftChild, HuffmanTreeNode rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		leftChild.setParent(this);
		rightChild.setParent(this);
		freqCount = leftChild.getFrequencyCount()
				+ rightChild.getFrequencyCount();
		code = "";
	}

	public int getLeafValue() {
		return leafValue;
	}

	public int getFrequencyCount() {
		return freqCount;
	}

	public String getHuffmanCode() {
		return code;
	}
	public void setParent(HuffmanTreeNode parent) {
		this.parent = parent;
	}
	
	public HuffmanTreeNode getLeftChild() {
		return leftChild;
	}

	public HuffmanTreeNode getRightChild() {
		return rightChild;
	}

	public HuffmanTreeNode getParent() {
		return parent;
	}
	
	public void setCodeAsLeftChild() {
		code = parent.getHuffmanCode() + "0";
	}
	
	public void setCodeAsRightChild() {
		code = parent.getHuffmanCode() + "1";
	}


}
