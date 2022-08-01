package es.upm.dte.iot.devices;

public class ColorObservation {
	
	private int clear, red, green, blue;

	public int getClear() {
		return clear;
	}

	public void setClear(int clear) {
		this.clear = clear;
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public ColorObservation(int clear, int red, int green, int blue) {
		super();
		this.clear = clear;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}	
}
