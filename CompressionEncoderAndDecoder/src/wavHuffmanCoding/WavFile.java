package wavHuffmanCoding;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class WavFile implements Iterable<WavSample> {

	private static final int BITS_FOR_TWO_BYTE = 16;
	private static final int BINARY_BASE = 2;
	private static final int HEX_BASE = 16;

	// all header information indexes
	public static final int[] FILE_DESCRIPTION_INDEX = { 0, 1, 2, 3 };
	public static final int[] SIZE_OF_FILE_INDEX = { 4, 5, 6, 7 };
	public static final int[] WAV_DESCRIPTION_INDEX = { 8, 9, 10, 11 };
	public static final int[] FORMAT_DESCRIPTION_INDEX = { 12, 13, 14, 15 };
	public static final int[] WAVE_SECTION_CHUNCK_SIZE_INDEX = { 16, 17, 18, 19 };
	public static final int[] WAVE_TYPE_FORMAT_INDEX = { 20, 21 };
	public static final int[] NUMBER_OF_CHANNELS_INDEX = { 22, 23 };
	public static final int[] SAMPLES_PER_SECOND_INDEX = { 24, 25, 26, 27 };
	public static final int[] BYTES_PER_SECOND_INDEX = { 28, 29, 30, 31 };
	public static final int[] BLOCK_ALIGNMENT_INDEX = { 32, 33 };
	public static final int[] BITS_PER_SAMPLE_INDEX = { 34, 35 };
	public static final int[] DATA_DESCRIPTION_INDEX = { 36, 37, 38, 39 };
	public static final int[] SIZE_OF_DATA_INDEX = { 40, 41, 42, 43 };

	// if the test case have 46 or more bytes header please manually change it
	public static final int FIRST_INDEX_OF_DATA = 44;

	private byte[] allBytes;
	private int fileSize;
	private List<WavSample> samples = new ArrayList<WavSample>();

	private int maxAmplitude;
	private int numberOfSample;
	private double timeLength;

	public WavFile(String filePath) {
		Path path = Paths.get(filePath);
		try {
			allBytes = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileSize = allBytes.length;
		int bitsPerSample = getBitsPerSample();
		int samplePerSecond = getSamplesPerSecond();
		maxAmplitude = 0;
		if (bitsPerSample == BITS_FOR_TWO_BYTE) {
			for (int i = FIRST_INDEX_OF_DATA; i < allBytes.length; i++) {
				WavSample sample = new WavSample(allBytes[i], allBytes[i + 1],
						samplePerSecond, (i + 1 - FIRST_INDEX_OF_DATA) / 2);
				updateMaxAmplitude(sample);
				samples.add(sample);
				i++;
			}
		} else {
			for (int i = FIRST_INDEX_OF_DATA; i < allBytes.length; i++) {
				WavSample sample = new WavSample(allBytes[i], samplePerSecond,
						i + 1 - FIRST_INDEX_OF_DATA);
				updateMaxAmplitude(sample);
				samples.add(sample);
			}
		}
		numberOfSample = samples.size();
		setTimeLength();
	}

	public int getNumberOfSample() {
		return numberOfSample;
	}

	public int getMaxAmplitude() {
		return maxAmplitude;
	}

	public double getTimeLength() {
		return timeLength;
	}

	public int getFileSize() {
		return fileSize;
	}
	
	@Override
	public Iterator<WavSample> iterator() {
		return Collections.unmodifiableList(samples).iterator();
	}

	public String getFileDescription() {
		String res = "";
		for (int index : FILE_DESCRIPTION_INDEX) {
			res += String.format("%s", (char) allBytes[index]);
		}
		return res;
	}

	public int getSizeOfFile() {
		return getIntValueOfTheBytes(SIZE_OF_FILE_INDEX);
	}

	public String getWavDescription() {
		String res = "";
		for (int index : WAV_DESCRIPTION_INDEX) {
			res += String.format("%s", (char) allBytes[index]);
		}
		return res;
	}

	public String getFmtDescription() {
		String res = "";
		for (int index : FORMAT_DESCRIPTION_INDEX) {
			res += String.format("%s", (char) allBytes[index]);
		}
		return res;
	}

	public int getSizeOfSectionChunck() {
		return getIntValueOfTheBytes(WAVE_SECTION_CHUNCK_SIZE_INDEX);
	}

	public int getWaveTypeFormat() {
		return getIntValueOfTheBytes(WAVE_TYPE_FORMAT_INDEX);
	}

	public int getNumberOfChannels() {
		return getIntValueOfTheBytes(NUMBER_OF_CHANNELS_INDEX);
	}

	public int getSamplesPerSecond() {
		return getIntValueOfTheBytes(SAMPLES_PER_SECOND_INDEX);
	}

	public int getBytesPerSecond() {
		return getIntValueOfTheBytes(BYTES_PER_SECOND_INDEX);
	}

	public int getBlockAlignment() {
		return getIntValueOfTheBytes(BLOCK_ALIGNMENT_INDEX);
	}

	public int getBitsPerSample() {
		return getIntValueOfTheBytes(BITS_PER_SAMPLE_INDEX);
	}

	public String getDataDescription() {
		String res = "";
		for (int index : DATA_DESCRIPTION_INDEX) {
			res += String.format("%s", (char) allBytes[index]);
		}
		return res;
	}

	public int getSizeOfData() {
		return getIntValueOfTheBytes(SIZE_OF_DATA_INDEX);
	}

	private void setTimeLength() {
		timeLength = getSizeOfData() / (double) getBytesPerSecond();
	}

	private void updateMaxAmplitude(WavSample sample) {
		int currentAmplitude = sample.getAmplitude();
		if (currentAmplitude < 0) {
			currentAmplitude = currentAmplitude * (-1);
		}
		if (currentAmplitude > maxAmplitude) {
			maxAmplitude = currentAmplitude;
		}
	}

	private int getIntValueOfTheBytes(int[] indexOfBytes) {
		return Integer.parseInt(getHexString(indexOfBytes), HEX_BASE);
	}

	private String getHexString(int[] indexOfBytes) {
		String res = "";
		int size = indexOfBytes.length;
		for (int i = size - 1; i >= 0; i--) {
			// since .wav file is little-endian, use the inverse concatenate
			res += convertByteToHexString(allBytes[indexOfBytes[i]]);
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
