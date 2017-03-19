package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.RecyclingMachine;
import edu.scu.coen160.finalproject.system.RecyclingMonitor;

import javax.swing.*;

/**
 * Top-level UI Class
 * Contains two RCM UI panels and an RMOS UI panel
 */
public class RecyclingUI extends JFrame {

    /**
     * Constructor.
     * Binds the UI to the relevant back-end components and initializes
     * a tabbed layout to present them all.
     * @param RCM1  RecyclingMachine #1 back-end object
     * @param RCM2  RecyclingMachine #2 back-end object
     * @param RMOS  RecyclingMonitor back-end object
     */
    public RecyclingUI(RecyclingMachine RCM1, RecyclingMachine RCM2, RecyclingMonitor RMOS) {
        super("Recycling Simulation");

        // Create a tabbed layout at the top level
        JTabbedPane tabs = new JTabbedPane();
        this.getContentPane().add(tabs);

        // Construct two RCM UIs and an RMOS UI
        RCMPanel myRCMPanel1 = new RCMPanel(RCM1);
        RCMPanel myRCMPanel2 = new RCMPanel(RCM2);
        RMOSPanel myRMOSPanel = new RMOSPanel(RMOS);

        // Bind the RMOS UI as an observer of both RCMs
        RCM1.addObserver(myRMOSPanel);
        RCM2.addObserver(myRMOSPanel);

        // Add the UIs to the tabbed layout
        tabs.addTab("RCM 0", myRCMPanel1);
        tabs.addTab("RCM 1", myRCMPanel2);
        tabs.addTab("RMOS", myRMOSPanel);

        setSize(1920, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
