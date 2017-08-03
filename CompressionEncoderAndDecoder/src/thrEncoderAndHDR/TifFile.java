package thrEncoderAndHDR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TifFile {
	private static final int TAG_OF_HEIGHT = 257;
	private static final int TAG_OF_WIDTH = 256;
	private static final String BIG_ENDIAN = "MM";
	private static final String LITTLE_ENDIAN = "II";
	private static final int BINARY_BASE = 2;
	private static final int HEX_BASE = 16;
	private static final int BYTE_OF_DE = 12;

	private static final int[] BYTE_ORDER_INDEX = { 0, 1 };
	private static final int[] VERSION_INDEX = { 2, 3 };
	private static final int[] OFFSET_TO_FIRST_IFD_INDEX = { 4, 5, 6, 7 };

	private byte[] allBytes;
	private String byteOrder = ""; // decide the byte is big or little endian
	private String filePath = "";

	private int width;
	private int height;
	private PixelData[][] pixelData;
	private int firstIFD;
	private int startOfImgData;

	public TifFile(String filePath) {
		this.filePath = filePath;
		Path path = Paths.get(filePath);
		try {
			allBytes = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		setByteOrder();
		setFirstFID();
		decodeFirstFID(firstIFD);
		setStartOfImageData();

		pixelData = new PixelData[height][width];
		int currentIndex = startOfImgData;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				pixelData[row][col] = new PixelData(allBytes[currentIndex],
						allBytes[currentIndex + 1], allBytes[currentIndex + 2]);
				currentIndex += 3;
			}
		}
	}

	public PixelData[][] getPixelData() {
		return pixelData;
	}

	public int getFileSize() {
		return allBytes.length;
	}

	public int getWidthOfImage() {
		return width;
	}

	public int getHeightOfImage() {
		return height;
	}

	public String getByteOrder() {
		return byteOrder;
	}

	private void setByteOrder() {
		for (int index : BYTE_ORDER_INDEX) {
			byteOrder += String.format("%s", (char) allBytes[index]);
		}
	}

	public int getVersion() {
		return getIntValueOfTheBytes(VERSION_INDEX);
	}

	public int getFirstFID() {
		return firstIFD;
	}

	public String getFilePath() {
		return filePath;
	}

	private void setFirstFID() {
		firstIFD = getIntValueOfTheBytes(OFFSET_TO_FIRST_IFD_INDEX);
	}

	private void setStartOfImageData() {
		if (firstIFD == 8) {
			startOfImgData = allBytes.length - height * width * 3 - 44;
		} else {
			startOfImgData = firstIFD - height * width * 3;
		}
	}

	private void decodeFirstFID(int startIndex) {
		int[] numOfDEindex = { startIndex, startIndex + 1 };
		int numOfDE = getIntValueOfTheBytes(numOfDEindex);

		for (int i = 0; i < numOfDE; i++) {
			if (getTagOfDE(startIndex + 2 + i * BYTE_OF_DE) == TAG_OF_WIDTH) {
				width = getDEValueOfShortType(startIndex + 2 + i * BYTE_OF_DE);
			}
			if (getTagOfDE(startIndex + 2 + i * BYTE_OF_DE) == TAG_OF_HEIGHT) {
				height = getDEValueOfShortType(startIndex + 2 + i * BYTE_OF_DE);
			}
		}
	}

	private int getTagOfDE(int startIndex) {
		int[] numOfDEIndex = { startIndex, startIndex + 1 };
		return getIntValueOfTheBytes(numOfDEIndex);
	}

	private int getDEValueOfShortType(int startIndex) {
		// value for wide and length type always be short to get them it is
		// enough
		int[] valueOfDEIndex = { startIndex + 8, startIndex + 9,
				startIndex + 10, startIndex + 11 };

		String hexString = getHexString(valueOfDEIndex);
		if (byteOrder.equals(BIG_ENDIAN)) {
			hexString = hexString.substring(0, 4);
		}
		return Integer.parseInt(hexString, HEX_BASE);
	}

	private int getIntValueOfTheBytes(int[] indexOfBytes) {
		return Integer.parseInt(getHexString(indexOfBytes), HEX_BASE);
	}

	private String getHexString(int[] indexOfBytes) {
		String res = "";
		int size = indexOfBytes.length;

		if (byteOrder.equals(LITTLE_ENDIAN)) {
			for (int i = size - 1; i >= 0; i--) {
				res += convertByteToHexString(allBytes[indexOfBytes[i]]);
			}
		} else if (byteOrder.equals(BIG_ENDIAN)) {
			for (int i = 0; i < size; i++) {
				res += convertByteToHexString(allBytes[indexOfBytes[i]]);
			}
		}
		return res;
	}

	private String convertByteToHexString(byte data) {
		char[] hexValues = "0123456789ABCDEF".toCharArray();
		String binaryString = convertByteToBinaryString(data);
		String frist4Bits = binaryString.substring(0, 4);
		String second4Bits = binaryString.substring(4);
		char firstValue = hexValues[Integer.parseInt(frist4Bits, BINARY_BASE)];
		char secondValue = hexValues[Integer.parseInt(second4Bits, BINARY_BASE)];
		return String.format("%s%s", firstValue, secondValue);
	}

	private String convertByteToBinaryString(byte data) {
		return String.format("%8s", Integer.toBinaryString(data & 0xFF))
				.replace(' ', '0');
	}
}
