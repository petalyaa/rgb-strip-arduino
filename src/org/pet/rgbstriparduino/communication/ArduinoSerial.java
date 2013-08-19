package org.pet.rgbstriparduino.communication;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

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
	
	private SerialPort serialPort;
	
	private OutputStream out;
	
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
	
	public void open() {
		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			String commPortName = currPortId.getName();
			if(commPortName.equals(port)) {
				portId = currPortId;
				break;
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}
		System.err.println("Using " + port + " to read/write to arduino serial.");
		
		try {
			serialPort = (SerialPort) portId.open(this.getClass().getName(), timeout);
			serialPort.setSerialPortParams(dataRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			out = serialPort.getOutputStream();
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void write(String cmd) {
		try {
			out.write(cmd.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		serialPort.close();
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
