package thrDecoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class THRFile {

	private static final int BINARY_BASE = 2;

	private byte[] allBytes;

	private byte[] yDataFile1;
	private byte[] uvDataFile1;

	private byte[] yDataFile2;
	private byte[] uvDataFile2;

	private byte[] yDataFile3;
	private byte[] uvDataFile3;

	private THRDecoder decoder;
	private PixelData[][] fileData1;
	private PixelData[][] fileData2;
	private PixelData[][] fileData3;

	public THRFile(String filePath) {
		Path path = Paths.get(filePath);
		try {
			allBytes = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		storeToDataByteArray();
		decodeThrFileData();
	}

	public PixelData[][] getDataForFile1() {
		return fileData1;
	}

	public PixelData[][] getDataForFile2() {
		return fileData2;
	}

	public PixelData[][] getDataForFile3() {
		return fileData3;
	}

	private void decodeThrFileData() {
		decoder = new THRDecoder(yDataFile1, uvDataFile1);
		fileData1 = decoder.getFilePixelData();
		decoder = new THRDecoder(yDataFile2, uvDataFile2);
		fileData2 = decoder.getFilePixelData();
		decoder = new THRDecoder(yDataFile3, uvDataFile3);
		fileData3 = decoder.getFilePixelData();
	}

	private void storeToDataByteArray() {
		int dataIndex = 0;
		int tempIndex = 0;
		int[] sizeOfDataIndex = new int[3];
		sizeOfDataIndex[0] = dataIndex;
		sizeOfDataIndex[1] = dataIndex + 1;
		sizeOfDataIndex[2] = dataIndex + 2;
		int sizeOfData = getIntValueOfTheBytes(sizeOfDataIndex);

		yDataFile1 = new byte[sizeOfData];
		for (int i = dataIndex; i < dataIndex + sizeOfData; i++) {
			yDataFile1[tempIndex++] = allBytes[i];
		}

		dataIndex = dataIndex + sizeOfData;
		tempIndex = 0;
		sizeOfDataIndex[0] = dataIndex;
		sizeOfDataIndex[1] = dataIndex + 1;
		sizeOfDataIndex[2] = dataIndex + 2;
		sizeOfData = getIntValueOfTheBytes(sizeOfDataIndex);
		uvDataFile1 = new byte[sizeOfData];
		for (int i = dataIndex; i < dataIndex + sizeOfData; i++) {
			uvDataFile1[tempIndex++] = allBytes[i];
		}

		dataIndex = dataIndex + sizeOfData;
		tempIndex = 0;
		tempIndex = 0;
		sizeOfDataIndex[0] = dataIndex;
		sizeOfDataIndex[1] = dataIndex + 1;
		sizeOfDataIndex[2] = dataIndex + 2;
		sizeOfData = getIntValueOfTheBytes(sizeOfDataIndex);
		yDataFile2 = new byte[sizeOfData];
		for (int i = dataIndex; i < dataIndex + sizeOfData; i++) {
			yDataFile2[tempIndex++] = allBytes[i];
		}

		dataIndex = dataIndex + sizeOfData;
		tempIndex = 0;
		sizeOfDataIndex[0] = dataIndex;
		sizeOfDataIndex[1] = dataIndex + 1;
		sizeOfDataIndex[2] = dataIndex + 2;
		sizeOfData = getIntValueOfTheBytes(sizeOfDataIndex);
		uvDataFile2 = new byte[sizeOfData];
		for (int i = dataIndex; i < dataIndex + sizeOfData; i++) {
			uvDataFile2[tempIndex++] = allBytes[i];
		}

		dataIndex = dataIndex + sizeOfData;
		tempIndex = 0;
		tempIndex = 0;
		sizeOfDataIndex[0] = dataIndex;
		sizeOfDataIndex[1] = dataIndex + 1;
		sizeOfDataIndex[2] = dataIndex + 2;
		sizeOfData = getIntValueOfTheBytes(sizeOfDataIndex);
		yDataFile3 = new byte[sizeOfData];
		for (int i = dataIndex; i < dataIndex + sizeOfData; i++) {
			yDataFile3[tempIndex++] = allBytes[i];
		}

		dataIndex = dataIndex + sizeOfData;
		tempIndex = 0;
		sizeOfDataIndex[0] = dataIndex;
		sizeOfDataIndex[1] = dataIndex + 1;
		sizeOfDataIndex[2] = dataIndex + 2;
		sizeOfData = getIntValueOfTheBytes(sizeOfDataIndex);
		uvDataFile3 = new byte[sizeOfData];
		for (int i = dataIndex; i < dataIndex + sizeOfData; i++) {
			uvDataFile3[tempIndex++] = allBytes[i];
		}
	}

	private int getIntValueOfTheBytes(int[] indexOfBytes) {
		return Integer.parseInt(getBinaryString(indexOfBytes), BINARY_BASE);
	}

	private String getBinaryString(int[] indexOfBytes) {
		String res = "";
		int size = indexOfBytes.length;
		for (int i = 0; i < size; i++) {
			res += convertByteToBinaryString(allBytes[indexOfBytes[i]]);
		}
		return res;
	}

	private String convertByteToBinaryString(byte data) {
		return String.format("%8s", Integer.toBinaryString(data & 0xFF))
				.replace(' ', '0');
	}

}
