package assign11;

public class Pixel {
	
	private int redAmount;
	private int greenAmount;
	private int blueAmount;

	public Pixel(int redAmount, int greenAmount, int blueAmount) {
		this.redAmount = redAmount;
		this.greenAmount = greenAmount;
		this.blueAmount = blueAmount;
		
		if (redAmount < 0 || redAmount > 255 || greenAmount < 0 || greenAmount > 255 || blueAmount < 0 || blueAmount > 255) {
			throw new IllegalArgumentException("Value is out of RGB range.");
		}
	}
	
	
	public int getRedAmount() {
		return redAmount;
	}

	
	public int getGreenAmount() {
		return greenAmount;
	}
	
	
	public int getBlueAmount() {
		return blueAmount;
	}
	
	
	public int getPackedRGB() {
		return (redAmount << 16) | (greenAmount << 8) | blueAmount;
	}
	
	
	public Pixel(Pixel other) {
	    this.redAmount = other.redAmount;
	    this.greenAmount = other.greenAmount;
	    this.blueAmount = other.blueAmount;
	}
}
