package thrEncoderAndHDR;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class THREncoder {

	byte[] byteToWrite;
	byte[] bytesForFile1;
	byte[] bytesForFile2;
	byte[] bytesForFile3;

	double compressionRatio;

	public THREncoder(TifFile file1, TifFile file2, TifFile file3) {

		bytesForFile1 = compressTiffToByteArray(file1);
		bytesForFile2 = compressTiffToByteArray(file2);
		bytesForFile3 = compressTiffToByteArray(file3);

		byteToWrite = new byte[bytesForFile1.length + bytesForFile2.length
				+ bytesForFile3.length];

		compressionRatio = (double) (file1.getFileSize() + file2.getFileSize() + file3
				.getFileSize()) / byteToWrite.length;
		int byteIndex = 0;
		for (byte b : bytesForFile1) {
			byteToWrite[byteIndex++] = b;
		}
		for (byte b : bytesForFile2) {
			byteToWrite[byteIndex++] = b;
		}
		for (byte b : bytesForFile3) {
			byteToWrite[byteIndex++] = b;
		}
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

	public double getCompressionRatio() {
		return compressionRatio;
	}

	private byte[] compressTiffToByteArray(TifFile file) {
		int width = file.getWidthOfImage();
		int height = file.getHeightOfImage();
		PixelData[][] pixelData = file.getPixelData();

		int[][] yData = new int[height][width];
		// for down sampling for Cu Cv by 4
		int halfCelingWidth = halfCelingValue(width);
		int halfCelingHeight = halfCelingValue(height);
		int[][] cuData = new int[halfCelingHeight][halfCelingWidth];
		int[][] cvData = new int[halfCelingHeight][halfCelingWidth];

		// down sampling for Cu Cv and store Y values
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				PixelData pixel = pixelData[row][col];
				yData[row][col] = pixel.getY();
				// add all 4 values to one first
				cuData[row / 2][col / 2] += pixel.getCu();
				cvData[row / 2][col / 2] += pixel.getCv();
			}
		}

		for (int row = 0; row < halfCelingHeight; row++) {
			for (int col = 0; col < halfCelingWidth; col++) {
				// take the average of 4 pixel
				cuData[row][col] = Math.round((float) cuData[row][col] / 4);
				cvData[row][col] = Math.round((float) cvData[row][col] / 4);
			}
		}

		// Quantization all y divided by 2, Cu Cv divide by 4
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				yData[row][col] /= 2;
			}
		}

		for (int row = 0; row < halfCelingHeight; row++) {
			for (int col = 0; col < halfCelingWidth; col++) {
				cuData[row][col] /= 4;
				cvData[row][col] /= 4;
			}
		}

		// Huffman coding and store data to byte array
		HuffmanTree yTree = new HuffmanTree(yData);
		HuffmanTree uvTree = new HuffmanTree(cuData, cvData);

		byte[] yBytes = yTree.getEncodedBytes();
		byte[] uvBytes = uvTree.getEncodedBytes();

		byte[] byteData = new byte[yBytes.length + uvBytes.length];
		int tempIndex = 0;
		for (byte b : yBytes) {
			byteData[tempIndex++] = b;
		}
		for (byte b : uvBytes) {
			byteData[tempIndex++] = b;
		}
		return byteData;
	}

	private int halfCelingValue(int value) {
		int half = value / 2;
		if (value % 2 != 0) {
			half++;
		}
		return half;
	}
}
