package org.pet.rgbstriparduino;

public class Main {
	
	public static void main(String[] args) {
		
		String port = "COM4";
		int timeout = 1000;
		int dataRate = 9600;
		
		RGBScreen rgbScreen = new RGBScreen(port, timeout, dataRate);
		rgbScreen.start();
	}

}
