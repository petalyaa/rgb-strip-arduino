package org.pet.rgbstriparduino.communication;

/**
 * Utility class to read/write serial to arduino
 * 
 * @author Khairul
 * @version 0.1
 */
public class ArduinoSerial {
	
	private String port;
	
	private int timeout;
	
	private int dataRate;
	
	/**
	 * Main constructor for arduino serial without parameter
	 */
	public ArduinoSerial() {
		
	}
	
	/**
	 * 
	 * Main constructor for arduino serial without parameter
	 * 
	 * @param port
	 *            Port that will be use to connect to serial
	 * @param timeout
	 *            Timeout that will be use to connect to serial
	 * @param dataRate
	 *            Data Rate that will be use to connect to serial
	 */
	public ArduinoSerial(String port, int timeout, int dataRate) {
		this.setPort(port);
		this.setTimeout(timeout);
		this.setDataRate(dataRate);
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the dataRate
	 */
	public int getDataRate() {
		return dataRate;
	}

	/**
	 * @param dataRate the dataRate to set
	 */
	public void setDataRate(int dataRate) {
		this.dataRate = dataRate;
	}

}
