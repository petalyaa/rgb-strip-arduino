package org.pet.rgbstriparduino;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pet.rgbstriparduino.communication.ArduinoSerial;

/**
 * 
 * Main Class to perform all the dirty work. This will get current snapshot of
 * the desktop and send it to arduino through serial port. This require JNI.
 * 
 * @author Khairul
 * @version 0.1
 * 
 */
public class RGBScreen {

	public static final String RED = "R";

	public static final String GREEN = "G";

	public static final String BLUE = "B";

	public static final int SLEEP_DURATION = 100;
	
	private int prevRed;
	
	private int prevGreen;
	
	private int prevBlue;

	private ArduinoSerial arduinoSerial;
	
	/**
	 * Main constructor for the RGB Screen
	 * 
	 * @author Khairul
	 * @param port
	 *            Port that will be use to connect to serial (eg : windows =
	 *            "com4", linux = "ttyS01")
	 * @param timeout
	 *            Timeout before throwing exception during opening serial
	 *            connection
	 * @param dataRate
	 *            Data rate use to connect to serial (eg : 9600)
	 */
	public RGBScreen(String port, int timeout, int dataRate) {
		arduinoSerial = new ArduinoSerial(port, timeout, dataRate);
		arduinoSerial.open();
	}
	
	/**
	 * 
	 * Start performing all the work. Looping is forever, only break if any exception
	 * occur or program terminate.
	 * 
	 * @author Khairul
	 */
	public void start() {
		while (true) {
			try {
				performWork();
				Thread.sleep(SLEEP_DURATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			} catch (AWTException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	/**
	 * 
	 * Getting current snapshot of the desktop and read each pixels of the image.
	 * After that, getting red, green blue of the image and calculate the avarage
	 * of it.
	 * 
	 * @author Khairul
	 */
	private final void performWork() throws AWTException {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit()
				.getScreenSize());
		BufferedImage image = new Robot().createScreenCapture(screenRect);
		int height = image.getHeight();
		int width = image.getWidth();
		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = image.getRGB(i, j);
				int[] rgbArr = getRGBArr(rgb);
				if (!isGray(rgbArr)) { // Filter out grays....
					Integer counter = (Integer) m.get(rgb);
					if (counter == null)
						counter = 0;
					counter++;
					m.put(rgb, counter);
				}
			}
		}
		int[] colourHex = getMostCommonColour(m);
		int red = colourHex[0];
		int green = colourHex[1];
		int blue = colourHex[2];
		if(red == prevRed && green == prevGreen && blue == prevBlue) {
			return;
		}
		prevRed = red;
		prevGreen = green;
		prevBlue = blue;
		String redCmd = getSerialCommand(RED, red);
		String greenCmd = getSerialCommand(GREEN, green);
		String blueCmd = getSerialCommand(BLUE, blue);
		log("Sending to arduino => " + redCmd + "; " + greenCmd + "; " + blueCmd + ";");
		sendToArduino(redCmd);
		sendToArduino(greenCmd);
		sendToArduino(blueCmd);
	}
	
	/**
	 * Log any message to console only.
	 * 
	 * @param log
	 *            Message to log.
	 */
	private final void log(String log) {
		System.err.println(log);
	}

	/**
	 * Construct command to send to arduino
	 * 
	 * @author Khairul
	 * @param colour
	 *            Color to send to arduino (eg : r,g,b)
	 * @param value
	 *            Decimal value of the color.
	 * @return Command to send to arduino. (eg : R=12, G=44)
	 */
	private final String getSerialCommand(String colour, int value) {
		StringBuilder sb = new StringBuilder();
		sb.append(colour).append("=").append(value);
		return sb.toString();
	}

	/**
	 * 
	 * Send command constructed to arduino
	 * 
	 * @author Khairul
	 * @param s
	 *            Command in string (eg : R=12, G=44)
	 */
	private final void sendToArduino(String s) {
		arduinoSerial.write(s);
	}

	/**
	 * Getting average color of the desktop
	 * 
	 * @author Khairul
	 * @param map
	 *            Map of RGB color integer
	 * @return integer array of average R,G,B
	 */
	private int[] getMostCommonColour(Map<Integer, Integer> map) {
		List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> o1,
					Entry<Integer, Integer> o2) {
				Integer i1 = o1.getValue();
				Integer i2 = o2.getValue();
				return i1.compareTo(i2);
			}
		});
		Entry<Integer, Integer> me = (Entry<Integer, Integer>) list.get(list
				.size() - 1);
		return getRGBArr((Integer) me.getKey());
	}

	/**
	 * Getting RGB array from pixel
	 * 
	 * @author Khairul
	 * @param pixel
	 *            Pixel to get the RGB
	 * @return Integer array of RGB
	 * 
	 */
	private int[] getRGBArr(int pixel) {
		// int alpha = (pixel >> 24) & 0xff;
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return new int[] { red, green, blue };
	}

	/**
	 * 
	 * Check if the RGB array is gray or not. Filter out black, white and
	 * grays... (tolerance within 10 pixels)
	 * 
	 * @author Khairul
	 * @param rgbArr
	 *            array of RGB to check for gray
	 * @return boolean either RGB is gray
	 */
	private boolean isGray(int[] rgbArr) {
		int rgDiff = rgbArr[0] - rgbArr[1];
		int rbDiff = rgbArr[0] - rgbArr[2];
		// Filter out black, white and grays...... (tolerance within 10 pixels)
		int tolerance = 10;
		if (rgDiff > tolerance || rgDiff < -tolerance)
			if (rbDiff > tolerance || rbDiff < -tolerance) {
				return false;
			}
		return true;
	}

}
