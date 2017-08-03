package wavHuffmanCoding;

public class WavSample {

	private static final double NUM_OF_POS_VALUE_FOR_ONE_BYTE = (double) 127;
	private static final double NUM_OF_NRG_VALUE_FOR_ONE_BYTE = (double) 128;
	private static final double NUM_OF_POS_VALUE_FOR_TWO_BYTE = (double) 32767;
	private static final double NUM_OF_NRG_VALUE_FOR_TWO_BYTE = (double) 32768;

	private static final int BINARY_BASE = 2;

	private int amplitude;
	private double amplitudePercentage;
	private double time;

	public WavSample(byte sampleByte, int samplePerSecond, int sampleNumber) {
		setAmplitudeForOneByte(sampleByte);
		setTimeValue(samplePerSecond, sampleNumber);
	}

	public WavSample(byte lowOrderByte, byte HighOrderByte,
			int samplePerSecond, int sampleNumber) {
		setAmplitudeForTwoBytes(lowOrderByte, HighOrderByte);
		setTimeValue(samplePerSecond, sampleNumber);
	}

	public double getAmplitudePercentage() {
		return amplitudePercentage;
	}

	public double getTime() {
		return time;
	}

	public int getAmplitude() {
		return amplitude;
	}

	private void setAmplitudeForOneByte(byte sampleByte) {
		String binaryString = convertByteToBinaryString(sampleByte);
		amplitude = Integer.parseInt(binaryString, BINARY_BASE);

		int intOfByte = amplitude - (int) NUM_OF_NRG_VALUE_FOR_ONE_BYTE;
		// to make half of the value to be negative value
		if (intOfByte >= 0) {
			// normalization to 0 to 1
			amplitudePercentage = intOfByte / NUM_OF_POS_VALUE_FOR_ONE_BYTE;
		} else {
			amplitudePercentage = intOfByte / NUM_OF_NRG_VALUE_FOR_ONE_BYTE;
		}
	}

	private void setAmplitudeForTwoBytes(byte lowOrderByte, byte HighOrderByte) {
		String binaryString = convertByteToBinaryString(HighOrderByte)
				+ convertByteToBinaryString(lowOrderByte);
		amplitude = convertSingnedBinaryToInteger(binaryString);
		if (amplitude >= 0) {
			// normalization to 0 to 1
			amplitudePercentage = amplitude / NUM_OF_POS_VALUE_FOR_TWO_BYTE;
		} else {
			amplitudePercentage = amplitude / NUM_OF_NRG_VALUE_FOR_TWO_BYTE;
		}
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

	private String convertByteToBinaryString(byte sampleByte) {
		return String.format("%8s", Integer.toBinaryString(sampleByte & 0xFF))
				.replace(' ', '0');
	}

	private void setTimeValue(int samplePerSecond, int sampleNumber) {
		time = getSecondPerSample(samplePerSecond) * sampleNumber;
	}

	private double getSecondPerSample(int samplePerSecond) {
		return 1 / (double) samplePerSecond;
	}

}
