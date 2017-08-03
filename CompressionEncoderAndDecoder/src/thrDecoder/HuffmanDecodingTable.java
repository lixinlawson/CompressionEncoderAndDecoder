package thrDecoder;

public class HuffmanDecodingTable {

	private static final int START_INDEX = 10;

	HuffmanDecodingNode[] table;

	private int currentDecodedValue;
	private int currentDecodedLength;

	public HuffmanDecodingTable(byte[] bytes, int numOfTableValue,
			int byteSizeForCode) {
		table = new HuffmanDecodingNode[numOfTableValue];
		int decodingIndex = START_INDEX;
		for (int i = 0; i < numOfTableValue; i++) {
			int value = (int) bytes[decodingIndex++];
			int length = (int) bytes[decodingIndex++];
			String code = "";
			for (int j = 0; j < byteSizeForCode; j++) {
				code += convertByteToBinaryString(bytes[decodingIndex++]);
			}
			code = code.substring(0, length);
			table[i] = new HuffmanDecodingNode(value, length, code);
		}
	}

	public boolean notEnoughForDecoding(String binStr) {
		boolean notDecoded = true;
		for (HuffmanDecodingNode n : table) {
			if (binStr.startsWith(n.getCode())) {
				notDecoded = false;
			}
		}
		return notDecoded;
	}
	
	public void decodeBinaryString(String binStr) {
		for (HuffmanDecodingNode n : table) {
			if (binStr.startsWith(n.getCode())) {
				currentDecodedValue = n.getValue();
				currentDecodedLength = n.getLength();
			}
		}
	}

	public int getCurrentDecodedValue() {
		return currentDecodedValue;
	}

	public int getCurrentDecodedLength() {
		return currentDecodedLength;
	}

	private String convertByteToBinaryString(byte data) {
		return String.format("%8s", Integer.toBinaryString(data & 0xFF))
				.replace(' ', '0');
	}
}
