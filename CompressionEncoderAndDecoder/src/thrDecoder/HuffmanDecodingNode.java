package thrDecoder;

public class HuffmanDecodingNode {

	private int value;
	private int length;
	private String code;

	public HuffmanDecodingNode(int value, int length, String code) {
		this.value = value;
		this.length = length;
		this.code = code;
	}

	public int getValue() {
		return value;
	}
	
	public int getLength(){
		return length;
	}

	public String getCode() {
		return code;
	}

}
