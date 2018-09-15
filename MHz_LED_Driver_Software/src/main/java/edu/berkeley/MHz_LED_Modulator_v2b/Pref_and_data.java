package edu.berkeley.MHz_LED_Modulator_v2b;

import java.io.IOException;
import java.util.Arrays;
import java.util.prefs.Preferences;

//The model class stores the current state of all data that needs to be exchanged between the GUI and the controller
//The model also does any necessary conversions such as beta to T calculations
//The model also saves values in default and loads those values on startup
public final class Pref_and_data {
	//Default values
	//Panel
	private static final double DEFAULTSLOPE = 1.7; //slope in line fitted to dial angle vs. dial percent
	private static final double DEFAULTOFFSET = 25; //Y-axis offset in line fitted to dial angle vs. dial percent
	private static final double DEFAULTPERCENT = -100; //Value flags dial as N/A
	private static final double DEFAULTANGLE = 270; //Default angle for dial - position dial takes when not connected
	private static final String[] TOGGLEPOSITIONS = {"Manual", "Auto"}; //Names of positions for toggle switch
	//Temperature
	private static final double[] MINTEMP = {10, 10, 10}; //Minimum temperature for GUI thermometers - 0-input, 1-output, 2-external
	private static final double[] WARNTEMP = {50, 50, 50}; //Temperature at which overheat warning starts - also temp that device has to recover to before turning back on after fault - 0-input, 1-output, 2-external
	private static final double[] FAULTTEMP = {80, 80, 80}; //Temperature at which driver automatically shuts off - this is also max temp for gui thermometers - 0-input, 1-output, 2-external
	private static final double INITIALTEMP = -9999; //Initialize temps at impossible reading to flag that no reading had been made yet
	private static final double[] DEFAULTTEMP = {INITIALTEMP, INITIALTEMP, INITIALTEMP}; //Input temperature sensor - initialize to impossible value - 0-input, 1-output, 2-external
    private static final double[] DEFAULTBETA = {3470, 3470, 3470}; //Beta coefficient of input temperature sensor - 0-input, 1-output, 2-externa
    private static final double[] DEFAULTRO = {10000, 10000, 10000}; //Ro of input temperature sensor - 0-input, 1-output, 2-external
    private static final double[] DEFAULTTO = {25, 25, 25}; //Temperature at Ro (in oC) of input temperature sensor - 0-input, 1-output, 2-external
    private static final double TEMPWINDOW = 2; //Size of sliding window used to smooth sensor jitter
    private static final double TEMPHISTORESIS = 0.5; //How much temperature has to change to refresh display (in degrees C)
    private static final double DIALHISTORESIS = 0.5; //How much knob has to change to refresh display (in percent)
    private static final double SERIESR = 4700; //Value in ohms of resistor in series with thermistors
    private static final int ADCMIN = 2; //Minimum valid value on raw temp ADC readings - noise floor on readings that should be 0
    //Serial
    private static final int IDPACKET = 1; //Identifies packet as device identification packet
    private static final int PANELPACKET = 2; //Identifies packet as panel status
    private static final int TEMPPACKET = 3; //Identifies packet as temperature recordings
    private static final int WAVEPACKET = 4; //Identifies packet as recorded analog waveform
    private static final byte STARTBYTE = 0; //Identifies start of packet
    private static final int BAUDRATE = 250000; //Baud rate of serial communication
    private static final String PREFERREDPORT = null; //Set preferred port to null flagging that no preferred port is selected
	private static final int READWAIT = 2000; //Time to wait for receiving entire packet
	private static final int SENDWAIT = READWAIT; //Time to wait for sending entire packet
    
    //Preference keys
    //Panel
    private static final String SLOPEID = "Dial Slope";
    private static final String OFFSETID = "Dial Offset";
    private static final String ANGLEID = "Dial Angle";
    private static final String TOGGLE0ID = "Toggle 0";
    private static final String TOGGLE1ID = "Toggle 1";
    //Temperature
    private static final String MINID = "Minimum Temp";
    private static final String WARNID = "Warning Temp";
    private static final String FAULTID = "Fault Temp";
    private static final String BETAID = "Beta";
    private static final String ROID = "Ro";
    private static final String TOID = "To";
    private static final String TWINDOWID = "Temp Window";
    private static final String THISTID = "Temp Historesis";
    private static final String DHISTID = "Dial Historesis";
    private static final String ADCMINID = "Minimum ADC";
    //Serial
    private static final String BAUDID = "Baud rate";
    private static final String PORTID = "Preferred port";
	private static final String READWAITID = "Read wait";
	private static final String SENDWAITID = "Send wait"; 
    
    //Initialize variables to store model data
	private GUI_temp_and_panel gui; //Instance of GUI so display can be updated
	private Serial serial; //Instance of serial port communication
    private boolean initializeComplete; //State of initialization of connection
    //Panel variables
	private double currentPercent; //Position of dial in percent currently displayed on GUI
	private double newPercent; //Most recent recorded position of dial in percent
	private double dialAngle; //Most recent recorded position of dial in percent
	private double defaultAngle; //Angle the dial should go to when disconnected
	private double dialSlope; //Slope of line fitted to dial angle vs. dial percent
	private double dialOffset; //Offset of line fitted to dial angle vs. dial percent
	private String[] togglePositions = new String[2]; //Stores names of toggle positions
	private boolean currentToggle; //Current position of toggle switch
    //Temperature variables
	private double[] minTemp; //Minimum temperature for GUI thermometers - 0-input, 1-output, 2-external
	private double[] warnTemp; //Temperature at which overheat warning starts - this is also max temp for gui thermometers - 0-input, 1-output, 2-external
	private double[] faultTemp; //Temperature at which driver automatically shuts off - 0-input, 1-output, 2-external
    private double[] currentTemp; //Temperature sensor reading currently being displayed in GUI - 0-input, 1-output, 2-external
    private double[] newTemp; //Most recent temperature reading from driver - 0-input, 1-output, 2-external
    private double[] beta; //Beta coefficient of temperature sensors - 0-input, 1-output, 2-external
    private double[] Ro; //Ro of input temperature sensors - 0-input, 1-output, 2-external
    private double[] To; //Temperature at Ro (in oC) of input temperature sensor - 0-input, 1-output, 2-external
    private double tWindow; //Size of sliding window used to smooth sensor jitter
    private double tHist; //How much temperature has to change to refresh display (in degrees C)
    private double dHist; //How much knob has to change to refresh display (in percent)
    private int adcMin; //Minimum valid value on raw temp ADC readings - noise floor on readings that should be 0
    //Serial variables
    private int baudRate; //Baud rate of serial communication
    private String preferredPort; //String containing name of preferred port (not port object itself)
    private int nArduino = 0; //Number of driver devices connected to the computer
    private byte[] headerArray = new byte [4]; //Array for storing the header on a found data packet
    private byte[] packetArray = new byte[252]; //Array for storing the data packet contents (256 bytes - 4 byte header)
    private int packetID = 0; //Packet ID: 1-ID packet, 2-temperature packet, 3-panel packet, 4-waveform packet 
    private int packetLength = 0; //length of the packet
    private int checkSum = 0; //packet checksum
	private int readWait; //Time to wait for receiving entire packet
	private int sendWait; //Time to wait for sending entire packet

	//Initialize a preferences file in the model class - this will allow user settings to be saved and loaded when program loads
	Preferences prefs = Preferences.userNodeForPackage(edu.berkeley.MHz_LED_Modulator_v2b.Pref_and_data.class);

	public Pref_and_data(){
		initializeComplete = false; //On boot of model - reset the initialize flag to false 
		currentToggle = false; //Initialize the toggle switch to a state (either state can be initial state);
		initialize();
	}
	
    public void setModules(GUI_temp_and_panel gui, Serial serial) {
    	this.gui = gui;
    	this.serial = serial;
    }
    
    public void shareConstants() {
    	gui.setConstants(minTemp, warnTemp, faultTemp, DEFAULTTEMP, TOGGLEPOSITIONS, defaultAngle, DEFAULTPERCENT);
    	serial.setConstants(baudRate, preferredPort, warnTemp, faultTemp, readWait, sendWait);
    }
//    public void setControllerConstants() {
//    	controller.getModelConstants(IDPACKET, TEMPPACKET, PANELPACKET, WAVEPACKET, initializeComplete);
//    }

/////////////////////////////PREFERENCES//////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	//Load model values from preferences
	public void initialize() {
		//Panel
		dialSlope = prefs.getDouble(SLOPEID, DEFAULTSLOPE);
		dialOffset = prefs.getDouble(OFFSETID, DEFAULTOFFSET);
		dialAngle = prefs.getDouble(ANGLEID, DEFAULTANGLE);
		defaultAngle = prefs.getDouble(ANGLEID, DEFAULTANGLE);
		currentPercent = DEFAULTPERCENT;
		newPercent = DEFAULTPERCENT;
		togglePositions[0] = prefs.get(TOGGLE0ID, TOGGLEPOSITIONS[0]);
		togglePositions[1] = prefs.get(TOGGLE1ID, TOGGLEPOSITIONS[1]);
		
		//Temperature
		minTemp = stringToDoubleArray(prefs.get(MINID, doubleArrayToString(MINTEMP)));
		warnTemp = stringToDoubleArray(prefs.get(WARNID, doubleArrayToString(WARNTEMP)));
		faultTemp = stringToDoubleArray(prefs.get(FAULTID, doubleArrayToString(FAULTTEMP)));
		currentTemp = DEFAULTTEMP; //Initialize temp readings to impossible default to flag that no recording has been made
		newTemp = DEFAULTTEMP;
		beta = stringToDoubleArray(prefs.get(BETAID, doubleArrayToString(DEFAULTBETA)));
		Ro = stringToDoubleArray(prefs.get(ROID, doubleArrayToString(DEFAULTRO)));
		To = stringToDoubleArray(prefs.get(TOID, doubleArrayToString(DEFAULTTO)));
		tWindow = prefs.getDouble(TWINDOWID, TEMPWINDOW);
		tHist = prefs.getDouble(THISTID, TEMPHISTORESIS);
		dHist = prefs.getDouble(DHISTID, DIALHISTORESIS);
		adcMin = prefs.getInt(ADCMINID, ADCMIN);
		
		//Serial
		baudRate = prefs.getInt(BAUDID, BAUDRATE);
		preferredPort = prefs.get(PORTID, PREFERREDPORT);
		readWait = prefs.getInt(READWAITID, READWAIT);
		sendWait = prefs.getInt(SENDWAITID, SENDWAIT);
	}
	
	public void restoreDefaults() {
		//Reset preferences to default values
		//Panel
		prefs.putDouble(SLOPEID, DEFAULTSLOPE);
		prefs.putDouble(OFFSETID, DEFAULTOFFSET);
		prefs.putDouble(ANGLEID, DEFAULTANGLE);
		prefs.put(TOGGLE0ID, TOGGLEPOSITIONS[0]);
		prefs.put(TOGGLE1ID, TOGGLEPOSITIONS[1]);
		
		//Temperature
		prefs.put(MINID, doubleArrayToString(MINTEMP));
		prefs.put(WARNID, doubleArrayToString(WARNTEMP));
		prefs.put(FAULTID, doubleArrayToString(FAULTTEMP));
		prefs.put(BETAID, doubleArrayToString(DEFAULTBETA));
		prefs.put(ROID, doubleArrayToString(DEFAULTRO));
		prefs.put(TOID, doubleArrayToString(DEFAULTTO));
		prefs.putDouble(TWINDOWID, TEMPWINDOW);
		prefs.putDouble(THISTID, TEMPHISTORESIS);
		prefs.putDouble(DHISTID, DIALHISTORESIS);
		prefs.putInt(ADCMINID, ADCMIN);
		
		//Serial
		prefs.putInt(BAUDID, BAUDRATE);
		prefs.put(PORTID, PREFERREDPORT);
		prefs.putInt(READWAITID, READWAIT);
		prefs.putInt(SENDWAITID, SENDWAIT);
		
		//Re-initialize to enact default values
		initialize();
		shareConstants();
	}
	
	//String strategy from: https://stackoverflow.com/questions/22698751/save-an-array-of-booleans-using-shared-preference
	private String doubleArrayToString(double[] array) {
		String string = ""; //Initialize empty string
		for(int a=0; a<array.length; a++) {
			string = string + Double.toString(array[a]) + "|$|SEPARATOR|$|"; //Concatenate doubles into single string with safe separator
		}
		return string;
	}
	
	private double[] stringToDoubleArray(String string) {
		String[] stringArray = string.split("\\|\\$\\|SEPARATOR\\|\\$\\|"); //Split concatenated string into discrete units
		double[] doubleArray = new double[stringArray.length];  //Initialize double array to store results
		for(int a=0; a<stringArray.length; a++) {
			doubleArray[a] = Double.parseDouble(stringArray[a]);
		}
		return doubleArray;
	}
	
///////////////////////////////////////SERIAL//////////////////////////////////////////////////////////////////////////////////////////////////	
	public boolean parseSerial(byte[] readBuffer, int readLength) {
		boolean packetFound = false;
		//Search entire buffer for all valid packets
	    for(int a=0; a<(readLength-headerArray.length); a++) {
	    	if(readBuffer[a] == STARTBYTE) { //Search for startbyte
	    		//Copy putative header starting at STARTBYTE
	    		System.arraycopy(readBuffer, a, headerArray, 0, headerArray.length); 
	    	    
	    		//Extract header bytes and convert uint8_t to int (variable & 0xFF) - https://stackoverflow.com/questions/14071361/java-how-to-parse-uint8-in-java 
	    	    packetLength = headerArray[1] & 0xFF; //length of the packet
	    		packetID = headerArray[3] & 0xFF; //Packet ID
	    		checkSum = packetID; //Reset checksum value to start at packet ID
	    		
	    		//Copy putative packet starting at end of header
	    		if((a+1+headerArray.length+packetLength) < readBuffer.length && packetLength <= packetArray.length && checkSum > 0) { //Check that packet is complete before trying to copy - ignore fragmented packets at end of buffer or packets that are longer than the packetArray they will be stored in
	    			Arrays.fill(packetArray, (byte) 0); //Clear contents of packet array
	    			System.arraycopy(readBuffer, a+headerArray.length, packetArray, 0, packetLength); //Copy putative header starting at STARTBYTE
	        		
	        		//Extract checksum from packet and verify it against checksum in data packet
	        		for(int b=0; b<packetLength; b++) checkSum += (packetArray[b] & 0xFF);
	System.out.println("Checksums: " + (checkSum % 256) + " " + (headerArray[2] & 0xFF));
	        		if((checkSum % 256) == (headerArray[2] & 0xFF)) { //See if checksum matches checksum in datapacket
	        			//If checksum is valid then valid packet structure - convert packet to int array and send to GUI
	        			packetFound = packetProcessor(packetArray, packetID);
	        			
	        			//Move buffer index to end of packet
	        			a += packetLength + headerArray.length-1;
	        		}
	    		}
	    	}

	    	if(!initializeComplete && packetFound) {
				
////NOTE: Install handshake code here to exchange critical parameters with arduino such as fault temps, etc.+++++++++++++++++++++++++++++++++++++	    		    	
//	    		serial.reply(new byte[] {CONFIRMBYTE, headerArray[2]}); //Tell Arduino that it's ID was found and to boot into loop
	    		nArduino++; //Add one to the number of devices found
	    		break; //If device was initializing and ID packet was found, stop looking for more packets
	    	}
	    }
	    return packetFound;
	}
	public int getNarduino() {
		return nArduino;
	}
	public boolean packetProcessor(byte[] packet, int packetID) {
		switch (packetID) {
			case IDPACKET: updateID(packet);
				return true;
			case TEMPPACKET: updateTemp(packet);
				return true;
			case PANELPACKET: updatePanel(packet);
				return true;
			case WAVEPACKET: updateWave(packet);
				return true;
			default: return false; // If the packet ID is invalid, return false
		}
    }
	
	public void initializeFinished() {
		initializeComplete = true;
	}
///////////////////////////////////////////////////ID//////////////////////////////////////////////////////////////////////////////////////////	
	private void updateID(byte[] packetArray) {
    	//Only add to menu during initialization
    	if(!initializeComplete) {
    		String ID = new String(packetArray);
    		gui.addMenuItem(ID);
    	}
    }
//////////////////////////////////////PANEL////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean[] updatePanel(byte[] packetArray) {
    	boolean[] returnArray = {false, false}; //Initialize array that flags to controller if panel needs to be updated in GUI
    	
    	//Only read packet if device is initialized
    	if(initializeComplete) {
	    	double dialADC = (double) (packetArray[0] & 0xFF);
	     	newPercent = dialADC/255D*100D;
	       	dialAngle = (newPercent*dialSlope)+dialOffset;
	       	
	       	//If dial has sufficiently changed, or is at 0 or 100, then update GUI
	       	if(newPercent > (currentPercent + dHist) || newPercent< (currentPercent - dHist) || newPercent == 0 || newPercent == 100) {
	       		currentPercent = newPercent;
	       		returnArray[0] = true;
	       	}
	       	else;
	       	
	       	//Check if toggle switch position has changed
	       	boolean toggleState = packetArray[1]!=0; //Convert byte to boolean
	       	if(toggleState^currentToggle) { // ^ = XOR - only if values are unequal returns true
	       		currentToggle = toggleState;
	       		returnArray[1] = true;
	       	}   	
    	}
    	return returnArray;
    }
	
//////////////////////////////////////TEMPERATURE//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
   public boolean[] updateTemp(byte[] packetArray) {
    	boolean[] returnArray = {false, false, false}; //Initialize array that flags to controller if temp needs to be updated in GUI
    	
    	//Only read packet if device is initialized
    	if(initializeComplete) {
    		
    		//Extract temperature bytes and convert to double
    		for(int a=0; a<newTemp.length; a++) {
    			double temp = ADCtoCelcius(packetArray[a], a); //Convert ADC reading to temperature in oC
    			if(temp != INITIALTEMP) {  //Check that valid temperature was returned  			
	    			if(newTemp[a] == INITIALTEMP) newTemp[a] = temp; //If this is the first reading - simply load values
	    			else newTemp[a] = temp/tWindow + newTemp[a] * ((tWindow-1)/tWindow); //Otherwise use sliding window
	    			
	    			//Check if current reading should be sent to GUI - i.e. has significantly changed
	    			if(newTemp[a] > (currentTemp[a] + tHist) || newTemp[a] < (currentTemp[a] - tHist)) { 
	    				currentTemp[a] = newTemp[a];
	    				returnArray[a] = true;  				
	    	    	}
    			}
    			else; //Otherwise, skip conversion and leave GUI flag false
    		}
    	}
    	return returnArray;
    }
    
    private double ADCtoCelcius(byte adcByte, int a) {
    	double adcDouble = (double) (adcByte & 0xFF); //Convert unsigned byte to double
    	double conversion = INITIALTEMP; //Initialize temp to impossible value to flag if conversion was not done
    	
    	if(adcDouble > adcMin) {   //Minimum threshold for valid ADC recording  	
	    	//Math from: https://learn.adafruit.com/thermistor/using-a-thermistor
	    	conversion = (-1*SERIESR*adcDouble) / (adcDouble-255D);
	    	conversion = conversion/Ro[a];
	    	conversion = Math.log(conversion);
	    	conversion /= beta[a];
	    	conversion += 1D/(To[a]+273.15D);
	    	conversion = 1D/conversion;
	    	conversion -= 273.15D;
    	}
     	return conversion;
    }
///////////////////////////////////////WAVE///////////////////////////////////////////////////////////////////////////////////////////////////
    private void updateWave(byte[] packetArray) {
    	
    }
}