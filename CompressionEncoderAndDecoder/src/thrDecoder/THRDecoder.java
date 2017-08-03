package thrDecoder;

public class THRDecoder {

	private static final int BINARY_BASE = 2;
	private static final int NUM_OF_NEG_CU_CV = 63;
	private static final int[] WIDTH_INDEX = { 3, 4 };
	private static final int[] HEIGHT_INDEX = { 5, 6 };
	private static final int NUM_OF_TABLE_VALUE_INDEX = 7;
	private static final int BYTE_SIZE_FOR_CODE_INDEX = 8;
	private static final int EXTRA_BITS_INDEX = 9;
	private static final int BYTES_NEED_FOR_HEADER = 10;

	private PixelData[][] filePixelData;
	private int[][] yPixelData;
	private int[][] cuPixelData;
	private int[][] cvPixelData;

	public THRDecoder(byte[] yData, byte[] uvData) {
		decodeYData(yData);
		decodeCuCvData(uvData);
		buildFileData();
	}

	public PixelData[][] getFilePixelData() {
		return filePixelData;
	}

	private void buildFileData() {
		int width = yPixelData[0].length;
		int height = yPixelData.length;
		filePixelData = new PixelData[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				filePixelData[row][col] = new PixelData(yPixelData[row][col],
						cuPixelData[row / 2][col / 2],
						cvPixelData[row / 2][col / 2]);
			}
		}

	}

	private void decodeYData(byte[] yData) {
		int width = getIntValueOfTheBytes(WIDTH_INDEX, yData);
		int height = getIntValueOfTheBytes(HEIGHT_INDEX, yData);
		yPixelData = new int[height][width];

		int numOfTableValue = Integer.parseInt(
				convertByteToBinaryString(yData[NUM_OF_TABLE_VALUE_INDEX]),
				BINARY_BASE);
		int byteSizeForCode = Integer.parseInt(
				convertByteToBinaryString(yData[BYTE_SIZE_FOR_CODE_INDEX]),
				BINARY_BASE);
		int extraBits = Integer
				.parseInt(convertByteToBinaryString(yData[EXTRA_BITS_INDEX]),
						BINARY_BASE);

		HuffmanDecodingTable table = new HuffmanDecodingTable(yData,
				numOfTableValue, byteSizeForCode);

		int[] decodedValues = new int[width * height];
		int decodingIndex = BYTES_NEED_FOR_HEADER + numOfTableValue
				* (1 + 1 + byteSizeForCode);
		String decodingStr = "";
		boolean notEnough = true;
		for (int i = 0; i < decodedValues.length; i++) {
			while (notEnough) {
				decodingStr += convertByteToBinaryString(yData[decodingIndex++]);
				if (decodingIndex == yData.length) {
					decodingStr = decodingStr.substring(0, decodingStr.length()
							- extraBits);
				}
				notEnough = table.notEnoughForDecoding(decodingStr);
			}

			table.decodeBinaryString(decodingStr);
			decodedValues[i] = table.getCurrentDecodedValue();
			decodingStr = decodingStr
					.substring(table.getCurrentDecodedLength());
			notEnough = table.notEnoughForDecoding(decodingStr);
		}

		int storeIndex = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				yPixelData[row][col] = decodedValues[storeIndex++] * 2;
			}
		}
	}

	private void decodeCuCvData(byte[] uvData) {
		int width = getIntValueOfTheBytes(WIDTH_INDEX, uvData);
		int height = getIntValueOfTheBytes(HEIGHT_INDEX, uvData);
		cuPixelData = new int[height][width];
		cvPixelData = new int[height][width];
		int numOfTableValue = Integer.parseInt(
				convertByteToBinaryString(uvData[NUM_OF_TABLE_VALUE_INDEX]),
				BINARY_BASE);
		int byteSizeForCode = Integer.parseInt(
				convertByteToBinaryString(uvData[BYTE_SIZE_FOR_CODE_INDEX]),
				BINARY_BASE);
		int extraBits = Integer.parseInt(
				convertByteToBinaryString(uvData[EXTRA_BITS_INDEX]),
				BINARY_BASE);
		HuffmanDecodingTable table = new HuffmanDecodingTable(uvData,
				numOfTableValue, byteSizeForCode);

		int[] decodedValues = new int[width * height * 2];

		int decodingIndex = BYTES_NEED_FOR_HEADER + numOfTableValue
				* (1 + 1 + byteSizeForCode);
		String decodingStr = "";
		boolean notDecoded = true;
		for (int i = 0; i < decodedValues.length; i++) {
			while (notDecoded) {
				decodingStr += convertByteToBinaryString(uvData[decodingIndex++]);
				if (decodingIndex == uvData.length) {
					decodingStr = decodingStr.substring(0, decodingStr.length()
							- extraBits);
				}
				notDecoded = table.notEnoughForDecoding(decodingStr);
			}

			table.decodeBinaryString(decodingStr);
			decodedValues[i] = table.getCurrentDecodedValue();
			decodingStr = decodingStr
					.substring(table.getCurrentDecodedLength());
			notDecoded = table.notEnoughForDecoding(decodingStr);
		}

		int storeIndex = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				cuPixelData[row][col] = (decodedValues[storeIndex++] - NUM_OF_NEG_CU_CV) * 4;
			}
		}
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				cvPixelData[row][col] = (decodedValues[storeIndex++] - NUM_OF_NEG_CU_CV) * 4;
			}
		}

	}

	private int getIntValueOfTheBytes(int[] indexOfBytes, byte[] bytes) {
		return Integer.parseInt(getBinaryString(indexOfBytes, bytes),
				BINARY_BASE);
	}

	private String getBinaryString(int[] indexOfBytes, byte[] bytes) {
		String res = "";
		int size = indexOfBytes.length;
		for (int i = 0; i < size; i++) {
			res += convertByteToBinaryString(bytes[indexOfBytes[i]]);
		}
		return res;
	}

	private String convertByteToBinaryString(byte data) {
		return String.format("%8s", Integer.toBinaryString(data & 0xFF))
				.replace(' ', '0');
	}

}
