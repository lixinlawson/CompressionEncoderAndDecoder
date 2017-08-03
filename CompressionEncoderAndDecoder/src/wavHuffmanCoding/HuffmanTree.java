package wavHuffmanCoding;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class HuffmanTree {

	private static final int BITS_FOR_TWO_BYTE = 16;
	private static final int BITS_PER_BYTE = 8;
	private static final int NUM_OF_VALUE_FOR_ONE_BYTE = 256;
	private static final int NUM_OF_VALUE_FOR_TWO_BYTE = 65536;
	private static final int NUM_OF_NRG_VALUE_FOR_TWO_BYTE = 32768;
	private static final int BYTE_FOR_TABLE_INFOR = 6;
	private static final int BINARY_BASE = 2;

	private int[] amplitudeCounter;
	private ArrayList<HuffmanTreeNode> amplitudeNodes = new ArrayList<HuffmanTreeNode>();
	private ArrayList<HuffmanTreeNode> huffmanTree = new ArrayList<HuffmanTreeNode>();
	private HuffmanTreeNode root;

	private int initialCodeWordLength;
	private double huffmanCodeWordLength;
	private int maxHuffmanCodeLength = 0;

	private int originalFileSize;
	private int compressedFileSize;

	private int numOfPossibleValue;
	private int byteForValueColSize;
	private int byteForCodeColSize;

	private int bytesForTable;
	private int bytesForData;

	private byte[] byteToWrite;
	private int byteIndex = 0;

	public HuffmanTree(WavFile wavFile) {
		originalFileSize = wavFile.getSizeOfFile();
		countAllSamples(wavFile);
		createLeafNodeForTree();
		buildHuffmanTree();
		setHuffmanCode(root);
		setAveMaxCodeLengthAndByteForData();
		sortLeafNodeByFrequency();
		setBytesNeedForTable(wavFile);
		initializeByteToWrtie();

		storeTableDataToByte();
		storeEncodedDataToByte(wavFile);
		
		// you can change the file name and extension here
		writeToFile("compressedWavFile");
	}

	public void writeToFile(String fileName) {
		try {
			FileOutputStream writer = new FileOutputStream(fileName);
			writer.write(byteToWrite);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getTableSize() {
		return bytesForTable;
	}

	public int getDataSize() {
		return bytesForData;
	}

	public int getInitialCodeWordLength() {
		return initialCodeWordLength;
	}

	public double getHuffmanCodeWordLength() {
		return huffmanCodeWordLength;
	}

	public int getOriginalFileSize() {
		return originalFileSize;
	}

	public int getCompressedFileSize() {
		return compressedFileSize;
	}

	public double getFileCompressionRatio() {
		return (double) originalFileSize / compressedFileSize;
	}

	public double getDataCompressionRatio() {
		return initialCodeWordLength / huffmanCodeWordLength;
	}

	public ArrayList<HuffmanTreeNode> getAllLeafNodes() {
		return amplitudeNodes;
	}

	private void setAveMaxCodeLengthAndByteForData() {
		int totalCount = 0;
		int totalLength = 0;
		for (HuffmanTreeNode node : amplitudeNodes) {
			int frequencyCount = node.getFrequencyCount();
			int codeLength = node.getHuffmanCode().length();
			totalCount = totalCount + frequencyCount;
			totalLength = totalLength + frequencyCount * codeLength;
			if (codeLength > maxHuffmanCodeLength) {
				maxHuffmanCodeLength = codeLength;
			}
		}

		bytesForData = totalLength / BITS_PER_BYTE;
		if (totalLength % BITS_PER_BYTE != 0) {
			bytesForData++;
		}

		huffmanCodeWordLength = (double) totalLength / totalCount;
	}

	private void setBytesNeedForTable(WavFile wavFile) {
		numOfPossibleValue = amplitudeNodes.size();
		if (wavFile.getBitsPerSample() == BITS_FOR_TWO_BYTE) {
			byteForValueColSize = 2;
		} else {
			byteForValueColSize = 1;
		}
		byteForCodeColSize = maxHuffmanCodeLength / BITS_PER_BYTE;
		if (maxHuffmanCodeLength % BITS_PER_BYTE != 0) {
			byteForCodeColSize++;
		}
		bytesForTable = BYTE_FOR_TABLE_INFOR
				+ (byteForValueColSize + 1 + byteForCodeColSize) * numOfPossibleValue;
		// extra one byte for length of bits
	}

	private void initializeByteToWrtie() {
		compressedFileSize = bytesForTable + bytesForData;
		byteToWrite = new byte[compressedFileSize];
	}

	private void sortLeafNodeByFrequency() {
		Collections.sort(amplitudeNodes, new HuffmanTreeNodeComparator());
		Collections.reverse(amplitudeNodes);
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

	private void createLeafNodeForTree() {
		int count = 0;
		if (amplitudeCounter.length == NUM_OF_VALUE_FOR_TWO_BYTE) {

			for (int i = 0; i < NUM_OF_VALUE_FOR_TWO_BYTE; i++) {
				count = amplitudeCounter[i];
				if (count != 0) {
					HuffmanTreeNode huffmanTreeNode = new HuffmanTreeNode(
							(i - NUM_OF_NRG_VALUE_FOR_TWO_BYTE), count);
					amplitudeNodes.add(huffmanTreeNode);
					huffmanTree.add(huffmanTreeNode);
				}
			}
		} else {
			for (int i = 0; i < NUM_OF_VALUE_FOR_ONE_BYTE; i++) {
				count = amplitudeCounter[i];
				if (count != 0) {
					HuffmanTreeNode huffmanTreeNode = new HuffmanTreeNode(i,
							count);
					amplitudeNodes.add(huffmanTreeNode);
					huffmanTree.add(huffmanTreeNode);
				}
			}
		}
	}

	private void countAllSamples(WavFile wavFile) {
		if (wavFile.getBitsPerSample() == BITS_FOR_TWO_BYTE) {
			initialCodeWordLength = BITS_FOR_TWO_BYTE;
			amplitudeCounter = new int[NUM_OF_VALUE_FOR_TWO_BYTE];
			for (WavSample sample : wavFile) {
				amplitudeCounter[sample.getAmplitude()
						+ NUM_OF_NRG_VALUE_FOR_TWO_BYTE]++;
			}
		} else {
			initialCodeWordLength = BITS_PER_BYTE;
			amplitudeCounter = new int[NUM_OF_VALUE_FOR_ONE_BYTE];
			for (WavSample sample : wavFile) {
				amplitudeCounter[sample.getAmplitude()]++;
			}
		}
	}

	private void storeTableDataToByte() {
		String tableSize = Integer.toBinaryString(bytesForTable);
		while (tableSize.length() < 4 * BITS_PER_BYTE) {
			// fill to 4 bytes with 0 in front
			tableSize = "0" + tableSize;
		}
		// 6 bytes to store table information
		byteToWrite[byteIndex++] = convert8BitsStrToByte(tableSize.substring(0,
				8));
		byteToWrite[byteIndex++] = convert8BitsStrToByte(tableSize.substring(8,
				16));
		byteToWrite[byteIndex++] = convert8BitsStrToByte(tableSize.substring(
				16, 24));
		byteToWrite[byteIndex++] = convert8BitsStrToByte(tableSize
				.substring(24));
		byteToWrite[byteIndex++] = (byte) byteForValueColSize;
		byteToWrite[byteIndex++] = (byte) byteForCodeColSize;

		if (byteForValueColSize == 1) {
			for (HuffmanTreeNode node : amplitudeNodes) {
				byteToWrite[byteIndex++] = (byte) node.getLeafValue();
				String hCode = node.getHuffmanCode();
				byteToWrite[byteIndex++] = (byte) hCode.length();
				while (hCode.length() < byteForCodeColSize * BITS_PER_BYTE) {
					hCode += "0";
				}
				for (int i = 0; i < byteForCodeColSize * BITS_PER_BYTE; i += 8) {
					byteToWrite[byteIndex++] = convert8BitsStrToByte(hCode
							.substring(i, i + BITS_PER_BYTE));
				}

			}
		} else {
			for (HuffmanTreeNode node : amplitudeNodes) {
				String value = Integer.toBinaryString(node.getLeafValue());
				if (value.length() > byteForValueColSize * BITS_PER_BYTE) {
					value = value.substring(0, byteForValueColSize
							* BITS_PER_BYTE);
				} else {
					while (value.length() < byteForValueColSize * BITS_PER_BYTE) {
						value = "0" + value;
					}
				}

				for (int i = 0; i < byteForValueColSize * BITS_PER_BYTE; i += 8) {
					byteToWrite[byteIndex++] = convert8BitsStrToByte(value
							.substring(i, i + BITS_PER_BYTE));
				}

				String hCode = node.getHuffmanCode();
				byteToWrite[byteIndex++] = (byte) hCode.length();
				while (hCode.length() < byteForCodeColSize * BITS_PER_BYTE) {
					hCode += "0";
				}
				for (int i = 0; i < byteForCodeColSize * BITS_PER_BYTE; i += 8) {
					byteToWrite[byteIndex++] = convert8BitsStrToByte(hCode
							.substring(i, i + BITS_PER_BYTE));
				}

			}
		}
	}

	private void storeEncodedDataToByte(WavFile wavFile) {
		String encoding = "";
		for (WavSample sample : wavFile) {
			int value = sample.getAmplitude();
			for (int i = 0; i < amplitudeNodes.size(); i++) {
				HuffmanTreeNode node = amplitudeNodes.get(i);
				if (value == node.getLeafValue()) {
					encoding += node.getHuffmanCode();
					break;
				}
				while (encoding.length() >= 8) {
					byteToWrite[byteIndex++] = convert8BitsStrToByte(encoding
							.substring(0, BITS_PER_BYTE));
					encoding = encoding.substring(BITS_PER_BYTE);
				}
			}
		}

		if (encoding.length() != 0) {
			byteToWrite[byteIndex++] = convert8BitsStrToByte(encoding);
		}
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
