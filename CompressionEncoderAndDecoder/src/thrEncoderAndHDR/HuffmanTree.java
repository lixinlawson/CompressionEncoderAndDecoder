package thrEncoderAndHDR;

import java.util.ArrayList;
import java.util.Collections;

public class HuffmanTree {

	private static final int NUM_OF_NEG_CU_CV = 63;
	private static final int HALF_256 = 128;
	private static final int BINARY_BASE = 2;
	private static final int BITS_PER_BYTE = 8;
	private static final int BYTES_NEED_FOR_HEADER = 10;

	private int[] frequencyCount;
	private ArrayList<HuffmanTreeNode> huffmanTree = new ArrayList<HuffmanTreeNode>();
	private HuffmanTreeNode root;
	private HuffmanTreeNode[] valueOrderOfNode;
	private String[] huffmanCodeForValue;

	private int numOfPossibleValue;

	private int byteSizeForCode;

	private int width;
	private int height;
	private int numOfTableValue;
	private int extraBits = 0;

	private int bytesNeedForTable;
	private int bytesNeedForData;

	private byte[] encodedByte;
	private int encodingIndex = 0;

	public HuffmanTree(int[][] yData) {
		width = yData[0].length;
		height = yData.length;
		setHuffmanTreeForY(yData);
		encodingHeaderAndTable();
		encodeYData(yData);
	}

	public HuffmanTree(int[][] cuData, int[][] cvData) {
		width = cuData[0].length;
		height = cuData.length;
		setHuffmanTreeForUV(cuData, cvData);
		encodingHeaderAndTable();
		encodeCuCvData(cuData, cvData);
	}

	public byte[] getEncodedBytes() {
		return encodedByte;
	}

	private void encodingHeaderAndTable() {
		setTableInfor();
		setDataInfor();
		initializeEncodedByte();
		storeHeaderDataToByte();
		storeTableDataToByte();
	}

	private void storeTableDataToByte() {
		for (int i = 0; i < numOfPossibleValue; i++) {
			// one byte for value
			// one byte for code length
			// byteSizeForCode bytes for code
			int value = i;
			String code = huffmanCodeForValue[i];
			int length = code.length();
			if (length != 0) {
				encodedByte[encodingIndex++] = (byte) value;
				encodedByte[encodingIndex++] = (byte) length;
				while (code.length() < byteSizeForCode * BITS_PER_BYTE) {
					code += "0";
				}
				for (int j = 0; j < byteSizeForCode * BITS_PER_BYTE; j += BITS_PER_BYTE) {
					encodedByte[encodingIndex++] = convert8BitsStrToByte(code
							.substring(j, j + BITS_PER_BYTE));
				}
			}
		}
	}

	private void encodeYData(int[][] yData) {
		String encodingStr = "";
		for (int[] row : yData) {
			for (int data : row) {
				encodingStr += huffmanCodeForValue[data];
				while (encodingStr.length() > BITS_PER_BYTE) {
					encodedByte[encodingIndex++] = convert8BitsStrToByte(encodingStr
							.substring(0, BITS_PER_BYTE));
					encodingStr = encodingStr.substring(BITS_PER_BYTE);
				}
			}
		}
		if (encodingStr.length() != 0) {
			for (int i = 0; i < extraBits; i++) {
				encodingStr += "0";
			}
			encodedByte[encodingIndex++] = convert8BitsStrToByte(encodingStr);
		}
	}

	private void encodeCuCvData(int[][] cuData, int[][] cvData) {
		String encodingStr = "";
		for (int[] row : cuData) {
			for (int data : row) {
				encodingStr += huffmanCodeForValue[data + NUM_OF_NEG_CU_CV];
				while (encodingStr.length() > BITS_PER_BYTE) {
					encodedByte[encodingIndex++] = convert8BitsStrToByte(encodingStr
							.substring(0, BITS_PER_BYTE));
					encodingStr = encodingStr.substring(BITS_PER_BYTE);
				}
			}
		}

		for (int[] row : cvData) {
			for (int data : row) {
				encodingStr += huffmanCodeForValue[data + NUM_OF_NEG_CU_CV];
				while (encodingStr.length() > BITS_PER_BYTE) {
					encodedByte[encodingIndex++] = convert8BitsStrToByte(encodingStr
							.substring(0, BITS_PER_BYTE));
					encodingStr = encodingStr.substring(BITS_PER_BYTE);
				}
			}
		}

		if (encodingStr.length() != 0) {
			for (int i = 0; i < extraBits; i++) {
				encodingStr += "0";
			}
			encodedByte[encodingIndex++] = convert8BitsStrToByte(encodingStr);
		}
	}

	private void storeHeaderDataToByte() {
		// For encodedByte
		// index 0 to 2 for size of encoded byte
		// index 3 and 4 for width of matrix
		// index 5 and 6 for height of matrix
		// index 7 for number of table values
		// index 8 byteSizeForCode
		// index 9 for number of extra bits at end of the array
		String byteNumStr = String.format("%24s",
				Integer.toBinaryString(encodedByte.length)).replace(' ',
				'0');
		encodedByte[encodingIndex++] = convert8BitsStrToByte(byteNumStr
				.substring(0, 8));
		encodedByte[encodingIndex++] = convert8BitsStrToByte(byteNumStr
				.substring(8, 16));
		encodedByte[encodingIndex++] = convert8BitsStrToByte(byteNumStr
				.substring(16, 24));

		
		String widthStr = String.format("%16s",
				Integer.toBinaryString(width)).replace(' ', '0');
		encodedByte[encodingIndex++] = convert8BitsStrToByte(widthStr
				.substring(0, 8));
		encodedByte[encodingIndex++] = convert8BitsStrToByte(widthStr
				.substring(8, 16));

		
		String heightStr = String.format("%16s",
				Integer.toBinaryString(height)).replace(' ', '0');
		encodedByte[encodingIndex++] = convert8BitsStrToByte(heightStr
				.substring(0, 8));
		encodedByte[encodingIndex++] = convert8BitsStrToByte(heightStr
				.substring(8, 16));

		encodedByte[encodingIndex++] = (byte) numOfTableValue;
		encodedByte[encodingIndex++] = (byte) byteSizeForCode;
		encodedByte[encodingIndex++] = (byte) extraBits;
	}

	private void setTableInfor() {
		numOfTableValue = 0;
		for (String s : huffmanCodeForValue) {
			if (s.length() != 0) {
				numOfTableValue++;
			}
		}
		bytesNeedForTable = (1 + 1 + byteSizeForCode) * numOfTableValue;
		// one byte for the value
		// one byte for the length of code
		// byteSizeForCode bytes for code
	}

	private void setDataInfor() {
		int numOfBitsNeed = 0;
		for (int i = 0; i < numOfPossibleValue; i++) {
			numOfBitsNeed += frequencyCount[i]
					* huffmanCodeForValue[i].length();
		}
		bytesNeedForData = numOfBitsNeed / BITS_PER_BYTE;
		if (numOfBitsNeed % BITS_PER_BYTE != 0) {
			bytesNeedForData++;
			extraBits = BITS_PER_BYTE - numOfBitsNeed % BITS_PER_BYTE;
		}

	}

	private void initializeEncodedByte() {
		encodedByte = new byte[BYTES_NEED_FOR_HEADER + bytesNeedForTable
				+ bytesNeedForData];
	}

	private void setHuffmanCodeForValue() {
		int maxCodeLength = 0;
		for (int i = 0; i < numOfPossibleValue; i++) {
			HuffmanTreeNode node = valueOrderOfNode[i];
			if (node != null) {
				String huffmanCode = node.getHuffmanCode();
				huffmanCodeForValue[i] = huffmanCode;
				if (huffmanCode.length() > maxCodeLength) {
					maxCodeLength = huffmanCode.length();
				}

			} else {
				huffmanCodeForValue[i] = "";
			}
		}
		byteSizeForCode = maxCodeLength / BITS_PER_BYTE;
		if (maxCodeLength % BITS_PER_BYTE != 0) {
			byteSizeForCode++;
		}
	}

	private void setHuffmanCode(HuffmanTreeNode node) {
		HuffmanTreeNode leftChild = node.getLeftChild();
		HuffmanTreeNode rightChild = node.getRightChild();
		if (leftChild == null && rightChild == null) {
			return;
		} else {
			leftChild.setCodeAsLeftChild();
			rightChild.setCodeAsRightChild();
			setHuffmanCode(leftChild);
			setHuffmanCode(rightChild);
		}
	}

	private void buildHuffmanTree() {
		Collections.sort(huffmanTree, new HuffmanTreeNodeComparator());
		while (huffmanTree.size() != 1) {
			HuffmanTreeNode leftChild = huffmanTree.get(0);
			HuffmanTreeNode rightChild = huffmanTree.get(1);
			huffmanTree.remove(0);
			huffmanTree.remove(0);

			HuffmanTreeNode parent = new HuffmanTreeNode(leftChild, rightChild);
			huffmanTree.add(parent);

			Collections.sort(huffmanTree, new HuffmanTreeNodeComparator());
		}
		root = huffmanTree.get(0);
	}

	private void createLeafNodeForTree(int[] frequencyCount) {
		for (int i = 0; i < numOfPossibleValue; i++) {
			int count = frequencyCount[i];
			if (count != 0) {
				HuffmanTreeNode leafNode = new HuffmanTreeNode(i, count);
				huffmanTree.add(leafNode);
				valueOrderOfNode[i] = leafNode;
			} else {
				valueOrderOfNode[i] = null;
			}
		}
	}

	private void setHuffmanTreeForY(int[][] yData) {
		numOfPossibleValue = HALF_256;
		frequencyCount = new int[numOfPossibleValue];
		valueOrderOfNode = new HuffmanTreeNode[numOfPossibleValue];
		huffmanCodeForValue = new String[numOfPossibleValue];

		for (int[] row : yData) {
			for (int data : row) {
				frequencyCount[data]++;
			}
		}
		createLeafNodeForTree(frequencyCount);
		buildHuffmanTree();

		if (frequencyCount.length != 1) {
			setHuffmanCode(root);
		} else {
			root.setCode("0");
		}
		setHuffmanCodeForValue();
	}

	private void setHuffmanTreeForUV(int[][] cuData, int[][] cvData) {
		numOfPossibleValue = HALF_256;
		frequencyCount = new int[numOfPossibleValue];
		valueOrderOfNode = new HuffmanTreeNode[numOfPossibleValue];
		huffmanCodeForValue = new String[numOfPossibleValue];
		for (int[] row : cuData) {
			for (int data : row) {
				frequencyCount[data + NUM_OF_NEG_CU_CV]++;
			}
		}
		for (int[] row : cvData) {
			for (int data : row) {
				frequencyCount[data + NUM_OF_NEG_CU_CV]++;
			}
		}
		createLeafNodeForTree(frequencyCount);
		buildHuffmanTree();

		if (frequencyCount.length != 1) {
			setHuffmanCode(root);
		} else {
			root.setCode("0");
		}
		setHuffmanCodeForValue();
	}

	private byte convert8BitsStrToByte(String s) {
		return (byte) convertSingnedBinaryToInteger(s);
	}

	private int convertSingnedBinaryToInteger(String binaryString) {
		int num = 0;
		if (binaryString.startsWith("0")) {
			// start with 0 means it is a positive number
			num = Integer.parseInt(binaryString, BINARY_BASE);
		} else {
			// negative number
			// the twos complement is ones complement + 1
			// when do the inverse calculation we multiply -1 and - 1
			num = Integer.parseInt(OnesComplement(binaryString), BINARY_BASE)
					* (-1) - 1;
		}
		return num;
	}

	private String OnesComplement(String binaryString) {
		String onesC = "";
		for (int i = 0; i < binaryString.length(); i++) {
			String bit = String.format("%s", binaryString.charAt(i));
			if (bit.equals("1")) {
				onesC += "0";
			} else {
				onesC += "1";
			}
		}
		return onesC;
	}
}
