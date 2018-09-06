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
import java.awt.Font;
import javax.swing.SwingConstants;


/**
 *
 * @author Ben
 */
@SuppressWarnings("serial")
public final class View extends javax.swing.JFrame {
	private Controller controller; //Instance of controller so events can be passed back to controller
	
    //GUI variables
    private static final DecimalFormat df1 = new DecimalFormat("##.#");
    private ButtonGroup group; //List of buttons in Connect menu
    private JRadioButtonMenuItem rbMenuItem; //Holder for current menu item

    
    //Serial variables
    private boolean initializeComplete = false; //Identifies if initial startup was complete (prevents things like IDs to be rewritten in connection menu)
    private byte[] packetArray; //Array for storing a data packet from serial
    private int packetID; //Packet ID number from serial 
    private static final byte IDPACKET = 1; //Identifies packet as device identification packet
    private static final byte TEMPPACKET = 2; //Identifies packet as temperature recordings
    private static final byte PANELPACKET = 3; //Identifies packet as panel status
    private static final byte WAVEPACKET = 4; //Identifies packet as recorded analog waveform
	
    
    public void setController(Controller controller) {
    	this.controller = controller;
    }
    /**
     * Creates new form User_Interface
     * @throws java.lang.InterruptedException
     */
    View() throws InterruptedException {
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
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        initComponents(); //Initialize interface components
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
        
        //Conditional directory is necessary because Eclipse and executable JAR look in different directories for images
        if(Main_class.class.getResource("/resources/images/Dialscale2.png") == null) jLabel1.setIcon(new ImageIcon(Main_class.class.getResource("/images/Dialscale2.png")));
        else jLabel1.setIcon(new ImageIcon(Main_class.class.getResource("/resources/images/Dialscale2.png")));
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
        
        //Conditional directory is necessary because Eclipse and executable JAR look in different directories for images
        if(Main_class.class.getResource("/resources/images/knob2-resized.png") == null) 
        	rotatePanel1.setImage(Toolkit.getDefaultToolkit().getImage(Main_class.class.getResource("/images/knob2-resized.png")));
        else rotatePanel1.setImage(Toolkit.getDefaultToolkit().getImage(Main_class.class.getResource("/resources/images/knob2-resized.png")));
        
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
        
        JLabel lblSyncMode = new JLabel();
        lblSyncMode.setText("Trigger Mode");
        lblSyncMode.setHorizontalAlignment(SwingConstants.CENTER);
        lblSyncMode.setFont(new Font("Tahoma", Font.PLAIN, 18));

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
        					.addComponent(jProgressBar1, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
        				.addGroup(layout.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(lblSyncMode, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)))
        			.addContainerGap())
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(tempPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
        					.addComponent(jProgressBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(jLayeredPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(lblSyncMode, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))
        			.addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */

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
        	//initializeComplete = initializeSerial();
            return 100;
        }
    };

    //Add frame listener for window opening, closing, etc. events
    void initSelfListeners(WindowListener taskStarterWindowListener) {
    	this.addWindowListener(taskStarterWindowListener);   	
    }
    
    
    public void updateProgress(int progress, String message) {
        jProgressBar1.setValue(progress);
        jProgressBar1.setString(message);
    }
    
    
    public void resetDisplay() {
        
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
    

    
    private void updateID() {
    	//Only add to menu during initialization
    	if(!initializeComplete) { 		
	    	rbMenuItem = new JRadioButtonMenuItem(new String(packetArray));
//.	    	rbMenuItem.setToolTipText(serial.getPortID());
	        group.add(rbMenuItem);
	        connectMenu.add(rbMenuItem);
	        
	    	//Add an action listener to the radio button so it can check when clicked
	        rbMenuItem.addActionListener((ActionEvent e) -> {
	            //If a radio button is selected connect to that device
//.	            serial.connectDevice(group);
	        });
    	}
    }
    

   

    private void updateWave() {
    	
    }
    

    
}



