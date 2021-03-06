package edu.berkeley.MHz_LED_Modulator_v2b;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import static java.lang.Math.random;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingWorker;
import java.awt.Toolkit;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;


/**
 *
 * @author Ben
 */
@SuppressWarnings("serial")
public class User_Interface extends javax.swing.JFrame {
	//Serial variables
	//Packet structure is: byte(0) STARTBYTE -> byte(1) packet length -> byte(2) checksum -> byte(3) packet identifier -> byte(4-n) data packet;
    private SerialPort arduinoPort; //Port object for communication to the Arduino via JSerialComm
    private SerialPort[] serialPorts; //Array of COM port objects that are currently open
    private byte[] readBuffer = new byte[1024]; //Array for storing the read buffer that can contain at least one packet (max size 256 bytes);
    private byte[] headerArray = new byte [4]; //Array for storing the header on a found data packet
    private byte[] packetArray = new byte[252]; //Array for storing the data packet contents (256 bytes - 4 byte header)
    private int packetID = 0; //Packet ID: 1-ID packet, 2-temperature packet, 3-panel packet, 4-waveform packet 
    private int packetLength = 0; //length of the packet
    private int checkSum = 0; //packet checksum
    private int readLength = 0; //Length of read Buffer
    private static final byte[] CONFIRMBYTE = {0}; //Send byte to confirm receipt of packet
    private static final byte STARTBYTE = 0; //Identifies start of packet
    private static final byte IDPACKET = 1; //Identifies packet as device identification packet
    private static final byte TEMPPACKET = 2; //Identifies packet as temperature recordings
    private static final byte PANELPACKET = 3; //Identifies packet as panel status
    private static final byte WAVEPACKET = 4; //Identifies packet as recorded analog waveform
    private static final int BAUDRATE = 250000; //Baudrate of serial communication
    private boolean arduinoConnect = false; //Whether the GUI if currently connected to a driver
    private int nArduino = 0; //Number of Arduino devices found connected to computer
    private boolean initializeComplete = false; //Identifies if initial startup was complete (prevents things like IDs to be rewritten in connection menu)
    private boolean packetFound = false; //Flag for whether a valid packet was found in the buffer
    
    //GUI variables
    private static final DecimalFormat df1 = new DecimalFormat("##.#");
    private static User_Interface GUI; //User interface frame
    private ButtonGroup group; //List of buttons in Connect menu
    private JRadioButtonMenuItem rbMenuItem; //Holder for current menu item
    private double temp1 = -9999; //Input temperature sensor - initialize to impossible value
    private double temp2; //Output temperature sensor
    private double temp3; //External temperature sensor
    private double beta1 = 3470; //Beta coefficient of input temperature sensor
    private double beta2 = 3470; //Beta coefficient of output temperature sensor
    private double beta3 = 3470; //Beta coefficient of external temperature sensor
    private double Ro1 = 10000; //Ro of input temperature sensor
    private double Ro2 = 10000; //Ro coefficient of output temperature sensor
    private double Ro3 = 10000; //Ro coefficient of external temperature sensor
    private static final double TEMPWINDOW = 2; //Size of sliding window used to smooth sensor jitter
    private static final double TEMPJITTER = 0.5; //How much temperature has to change to refresh display (in degrees C)
    private static final double DIALJITTER = 0.5; //How much knob has to change to refresh display (in percent)
    
    
    /**
     * Creates new form User_Interface
     * @throws java.lang.InterruptedException
     */
    private User_Interface() throws InterruptedException {
        initComponents(); //Initialize interface components
        initSelfListeners(); //Setup listeners for initialization events to happen when GUI appears
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        tempPanel = new javax.swing.JPanel();
        outputTempBar = new javax.swing.JProgressBar();
        outputTempBar.setMaximum(1000);
        outputBarLabel = new javax.swing.JLabel();
        inputBarLabel = new javax.swing.JLabel();
        inputTempBar = new javax.swing.JProgressBar();
        inputTempBar.setMaximum(1000);
        inputTempLabel = new javax.swing.JLabel();
        inputTempLabel.setToolTipText("-273.15");
        outputTempLabel = new javax.swing.JLabel();
        outputTempLabel.setToolTipText("-273.15");
        extTempBar = new javax.swing.JProgressBar();
        extTempBar.setMaximum(1000);
        extTempLabel = new javax.swing.JLabel();
        extTempLabel.setToolTipText("-273.15");
        ledBarLabel = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        rotatePanel1 = new edu.berkeley.MHz_LED_Modulator_v2b.RotatePanel();
        rotatePanel1.setToolTipText("-50");
        jProgressBar1 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        connectMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        outputTempBar.setOrientation(1);

        outputBarLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        outputBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        outputBarLabel.setText("Output");

        inputBarLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        inputBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inputBarLabel.setText("Input");

        inputTempBar.setOrientation(1);

        inputTempLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        inputTempLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inputTempLabel.setText("N/A");

        outputTempLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        outputTempLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        outputTempLabel.setText("N/A");

        extTempBar.setOrientation(1);

        extTempLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        extTempLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        extTempLabel.setText("N/A");

        ledBarLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ledBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ledBarLabel.setText("LED");

        javax.swing.GroupLayout tempPanelLayout = new javax.swing.GroupLayout(tempPanel);
        tempPanel.setLayout(tempPanelLayout);
        tempPanelLayout.setHorizontalGroup(
            tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tempPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(tempPanelLayout.createSequentialGroup()
                        .addComponent(inputBarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(outputBarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(ledBarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tempPanelLayout.createSequentialGroup()
                        .addGroup(tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(inputTempBar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inputTempLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(outputTempLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(outputTempBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(extTempLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(extTempBar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        tempPanelLayout.setVerticalGroup(
            tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tempPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputBarLabel)
                    .addComponent(outputBarLabel)
                    .addComponent(ledBarLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inputTempBar, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(extTempBar, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputTempBar, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tempPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(inputTempLabel)
                        .addComponent(outputTempLabel))
                    .addComponent(extTempLabel))
                .addContainerGap())
        );

        jLayeredPane2.setMaximumSize(new java.awt.Dimension(382, 382));
        jLayeredPane2.setMinimumSize(new java.awt.Dimension(382, 382));

        jPanel3.setMaximumSize(new java.awt.Dimension(382, 382));
        jPanel3.setMinimumSize(new java.awt.Dimension(382, 382));
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(382, 382));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new ImageIcon(User_Interface.class.getResource("/images/Dialscale2.png"))); // NOI18N
        jLabel1.setToolTipText("");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("N/A");
        jLabel2.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(111, 111, 111)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(107, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(316, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(33, 33, 33))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        rotatePanel1.setImage(Toolkit.getDefaultToolkit().getImage(User_Interface.class.getResource("/images/knob2-resized.png")));
        rotatePanel1.setOpaque(false);
        rotatePanel1.rotateWithParam(270);

        javax.swing.GroupLayout rotatePanel1Layout = new javax.swing.GroupLayout(rotatePanel1);
        rotatePanel1.setLayout(rotatePanel1Layout);
        rotatePanel1Layout.setHorizontalGroup(
            rotatePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );
        rotatePanel1Layout.setVerticalGroup(
            rotatePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 382, Short.MAX_VALUE)
        );

        jLayeredPane2.setLayer(jPanel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(rotatePanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane2Layout.createSequentialGroup()
                    .addComponent(rotatePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(rotatePanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jProgressBar1.setValue(0);
        jProgressBar1.setStringPainted(true);

        connectMenu.setText("Connect");
        connectMenu.setToolTipText("");
        jMenuBar1.add(connectMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(jLayeredPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addGap(126)
        					.addComponent(tempPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addGap(0, 0, Short.MAX_VALUE))
        				.addGroup(layout.createSequentialGroup()
        					.addGap(478)
        					.addComponent(jProgressBar1, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)))
        			.addContainerGap())
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jLayeredPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(tempPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
        					.addComponent(jProgressBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
        			.addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(User_Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(User_Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(User_Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(User_Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI = new User_Interface();
                    GUI.setVisible(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(User_Interface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JMenu connectMenu;
    private javax.swing.JLabel inputBarLabel;
    private javax.swing.JLabel inputTempLabel;
    private javax.swing.JProgressBar inputTempBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel ledBarLabel;
    private javax.swing.JProgressBar extTempBar;
    private javax.swing.JLabel extTempLabel;
    private javax.swing.JLabel outputBarLabel;
    private javax.swing.JProgressBar outputTempBar;
    private javax.swing.JLabel outputTempLabel;
    private edu.berkeley.MHz_LED_Modulator_v2b.RotatePanel rotatePanel1;
    private javax.swing.JPanel tempPanel;
    // End of variables declaration                   
    
    //Code is from: https://stackoverflow.com/questions/39565472/how-to-automatically-execute-a-task-after-jframe-is-displayed-from-within-it
    //Perform handshaking on backgorund thread so as not to lock-up the GUI
    SwingWorker<Integer, Integer> StartupLoader = new SwingWorker<Integer, Integer>() {
        @Override
        protected Integer doInBackground() throws Exception {
            initializeSerial();
            return 100;
        }
    };

    //This method is used to avoid calling an overridable method ('addWindowListener()') from within the constructor.
    //Code is from: https://stackoverflow.com/questions/39565472/how-to-automatically-execute-a-task-after-jframe-is-displayed-from-within-it
    private void initSelfListeners() {
        WindowListener taskStarterWindowListener = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                System.out.println("Performing task..."); //Perform task here. In this case, we are simulating a startup (only once) time-consuming task that would use a worker.
                StartupLoader.execute();
            }

            @Override
            public void windowClosing(WindowEvent e) {
            	
                if(arduinoPort != null) {
                	if(arduinoPort.isOpen()) arduinoPort.closePort();  //Close port connection when JFrame is closed
                	arduinoConnect = false;
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                //Do nothing...Or drink coffee...NVM; always drink coffee!
            }

            @Override
            public void windowIconified(WindowEvent e) {
                //Do nothing...Or do EVERYTHING!
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                //Do nothing...Or break the law...
            }

            @Override
            public void windowActivated(WindowEvent e) {
                //Do nothing...Procrastinate like me!
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                //Do nothing...And please don't notice I have way too much free time today...
            }
        };

        //Here is where the magic happens! We make (a listener within) the frame start listening to the frame's own events!
        this.addWindowListener(taskStarterWindowListener);
    }
    
    private void initializeSerial() throws InterruptedException{
        //Generate an array of available ports on system
        int nPorts = SerialPort.getCommPorts().length;
        serialPorts = SerialPort.getCommPorts();


        //Toggle each port checking for any that send an ID packet
        group = new ButtonGroup();
        for(int a = 0; a < nPorts; a++){
            arduinoPort = serialPorts[a];
            jProgressBar1.setValue(100*(a+1)/(nPorts));
            jProgressBar1.setString("Testing " +  arduinoPort.getDescriptivePortName());
            arduinoPort.setBaudRate(BAUDRATE);
            arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 2000); //Blocking means wait the full 2000ms to catch the set number of bytes
            arduinoPort.openPort();
            readSerial();
			arduinoPort.closePort();			
	     }
        
        //Add disconnect button to menu options
        rbMenuItem = new JRadioButtonMenuItem("Disconnect"); 
        rbMenuItem.setToolTipText("Disconnect from current device");
        rbMenuItem.setSelected(true);
        //Add an action listener to the radio button so it can check when clicked
        rbMenuItem.addActionListener((ActionEvent e) -> {
            //If a radio button is selected connect to that device
            connectDevice();
        });
        group.add(rbMenuItem);
        connectMenu.add(rbMenuItem);
     
        //Inform user if no devices were found
        if(nPorts == 0) jProgressBar1.setString("No available COM ports found on this computer.");
        else if(nArduino == 0) jProgressBar1.setString("Arduino not found.");
        else if(nArduino == 1) jProgressBar1.setString(nArduino + " device found.");
        else jProgressBar1.setString("Disconnected: " + nArduino + " devices available.");
        jProgressBar1.setValue(0); //Reset progress bar
        
        initializeComplete = true;
    }
    
    private void connectDevice(){
        if(arduinoPort != null) { //Close active open port if one is open
        	if(arduinoPort.isOpen()) arduinoPort.closePort();
        	arduinoConnect = false;
        }
        
        Iterable<AbstractButton> arl = Collections.list(group.getElements()); //Create a list of buttons in connect menu
        for(AbstractButton ab:arl){
            if(ab.isSelected()){
                for(SerialPort b:serialPorts){ //Search all COM ports for on that matches radioButton (using toolTipText which contains COM port name)
                    if(ab.getToolTipText().equals(b.getDescriptivePortName())){
                        System.out.println(b.getDescriptivePortName() + random());
                        arduinoPort = b;
                        arduinoPort.setBaudRate(BAUDRATE);
                        arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 2000, 2000); //Blocking means wait the full 2000ms to catch the set number of bytes
                        arduinoPort.openPort(); //Connect to matching port if found
                        
                        //Add a data listener to the port to catch any incoming packets
                        arduinoPort.addDataListener(new SerialPortDataListener() {
                    	   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
                    	   @Override
                    	   public void serialEvent(SerialPortEvent event)
                    	   {
                    	      readSerial();
                    	   }
                        });
                        arduinoConnect = true;
                        jProgressBar1.setString("Connected to: " + ab.getText());
                        break;
                    }
                }
            }
        }
        if(!arduinoConnect) {
            if(nArduino == 1) jProgressBar1.setString("Disconnected: " + nArduino + " device available.");
            else jProgressBar1.setString("Disconnected: " + nArduino + " devices available.");
            resetDisplay();
        }
    }
    
    private void resetDisplay() {
        
		inputTempLabel.setToolTipText("-273.15");
		outputTempLabel.setToolTipText("-273.15");
		extTempLabel.setToolTipText("-273.15");
	
		outputTempBar.setOrientation(1);
		outputTempBar.setValue(0);

        outputBarLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        outputBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        outputBarLabel.setText("Output");

        inputBarLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        inputBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inputBarLabel.setText("Input");

        inputTempBar.setOrientation(1);
        inputTempBar.setValue(0);

        inputTempLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        inputTempLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        inputTempLabel.setText("N/A");

        outputTempLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        outputTempLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        outputTempLabel.setText("N/A");

        extTempBar.setOrientation(1);
        extTempBar.setValue(0);

        extTempLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        extTempLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        extTempLabel.setText("N/A");

        ledBarLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ledBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ledBarLabel.setText("LED");

        jProgressBar1.setValue(0);
        jProgressBar1.setStringPainted(true);
        
        jLabel2.setText("N/A");
        rotatePanel1.rotateWithParam(270);
        rotatePanel1.setToolTipText("-50");
    }
    
    private boolean readSerial() {
    	packetFound = false; //Reset pack found flag
        readLength = arduinoPort.readBytes(readBuffer, readBuffer.length);
System.out.println("Buffer: " + Arrays.toString(readBuffer));
        
        //If minimal packet size is received then verify contents
		if(readLength > 4) {
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
	            			//If checksum is valid then valid packet structure - send packet to appropriate function based on packetID
	            			switch (packetID) {
	            				case IDPACKET: updateID();
	            					break;
	            				case TEMPPACKET: updateTemp();
	            					break;
	            				case PANELPACKET: updatePanel();
	            					break;
	            				case WAVEPACKET: updateWave();
	            					break;
	            			}
	            			//Move buffer index to end of packet
	            			a += packetLength + headerArray.length-1;
	            		}
            		}
            	}
            	if(!initializeComplete && packetFound) {
            		break; //If device was initializing and ID packet was found, stop looking for more packets
            	}
            }
		}
		return packetFound;
    }
    
    private void updateID() {
    	//Only add to menu during initialization
    	packetFound = true; //Set packet found flag to true
		arduinoPort.writeBytes(CONFIRMBYTE, 1); //Send confirmation byte that ID packet was received
    	if(!initializeComplete) {
	    	nArduino += 1; //Add one to number of found devices
	    	rbMenuItem = new JRadioButtonMenuItem(new String(packetArray));
	    	rbMenuItem.setToolTipText(arduinoPort.getDescriptivePortName());
	        group.add(rbMenuItem);
	        connectMenu.add(rbMenuItem);
	        
	    	//Add an action listener to the radio button so it can check when clicked
	        rbMenuItem.addActionListener((ActionEvent e) -> {
	            //If a radio button is selected connect to that device
	            connectDevice();
	        });
    	}
    }
    
    private void updateTemp() {
    	//Only read packet if device is initialized
    	if(initializeComplete) {
    		packetFound = true; //Set packet found flag to true
    		
    		//Extract temperature bytes and convert to unsigned ints
    		if(temp1 == -9999) { //If this is the first reading - simply load values
    			temp1 = packetArray[0] & 0xFF;
        		temp2 = packetArray[1] & 0xFF;
        		temp3 = packetArray[2] & 0xFF;
    		}
    		else { //Otherwise use sliding window
    			temp1 = (packetArray[0] & 0xFF)/TEMPWINDOW + temp1 * ((TEMPWINDOW-1)/TEMPWINDOW);
        		temp2 = (packetArray[1] & 0xFF)/TEMPWINDOW + temp2 * ((TEMPWINDOW-1)/TEMPWINDOW);
        		temp3 = (packetArray[2] & 0xFF)/TEMPWINDOW + temp3 * ((TEMPWINDOW-1)/TEMPWINDOW);
    		}
    		ADCtoCelcius(temp1, inputTempBar, inputTempLabel);
    		ADCtoCelcius(temp2, outputTempBar, outputTempLabel);
    		ADCtoCelcius(temp3, extTempBar, extTempLabel);
    	}
    }
    private void updatePanel() {
    	//Only read packet if device is initialized
    	if(initializeComplete) {
    		double currentPercent = Double.parseDouble(rotatePanel1.getToolTipText());
	    	double dialADC = (double) (packetArray[0] & 0xFF);
	     	double dialPercent = dialADC/255*100D;
	       	double dialAngle = (dialPercent*1.7D)+25D;
	       	if(dialPercent > (currentPercent + DIALJITTER) || dialPercent< (currentPercent - DIALJITTER) || dialPercent == 0 || dialPercent == 100) {
		        jLabel2.setText(df1.format(dialPercent) + "%");
		        rotatePanel1.rotateWithParam((int) dialAngle);
		        rotatePanel1.setToolTipText(Double.toString(dialPercent));
	       	}
    	}
    }
    private void updateWave() {
    	
    }
    
    void ADCtoCelcius(double ADC, JProgressBar bar, JLabel label) {
    	//If thermistor is sending valid measurement, output result
    	double currentTemp = Double.parseDouble(label.getToolTipText());
    	if(ADC > 2) {    	
	    	//Math from: https://learn.adafruit.com/thermistor/using-a-thermistor
	    	double conversion = (-4700D*ADC) / (ADC-255D);
	    	conversion = conversion/Ro1;
	    	conversion = Math.log(conversion);
	    	conversion /= beta1;
	    	conversion += 1D/(25D+273.15D);
	    	conversion = 1D/conversion;
	    	conversion -= 273.15D;
	    	if(conversion > (currentTemp + TEMPJITTER) || conversion < (currentTemp - TEMPJITTER)) { //If temp has sufficiently changed - update temp
		    	label.setToolTipText(Double.toString(conversion));
		    	bar.setValue((int) (conversion*10D));
		    	label.setText(Math.round(conversion) + "°C");
	    	}

    	}
    	//If measurement is 0, thermistor is disconnected
    	else {
    		bar.setValue((int) 0);
    		label.setText("N/A");
    	}
    }
}



