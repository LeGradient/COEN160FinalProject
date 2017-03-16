package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.*;

import javax.swing.*;
import java.awt.*;

public class RecyclingUI extends JFrame {
    private RecyclingMachine myRCM1;
    private RecyclingMachine myRCM2;
    private RecyclingMonitor myRMOS;

    private JTabbedPane tabs = new JTabbedPane();
    public RecyclingUI(RecyclingMachine RCM1, RecyclingMachine RCM2, RecyclingMonitor RMOS) {
        super("Recycling Simulation");

        myRCM1 = RCM1;
        myRCM2 = RCM2;
        myRMOS = RMOS;

        RMOSPanel myRMOSPanel = new RMOSPanel(myRMOS);
        tabs.addChangeListener(changeEvent -> {
            myRMOSPanel.refresh();
        });

        Container container = getContentPane();
        container.add(tabs);

        tabs.addTab("RCM 0", new RCMPanel(myRCM1));
        tabs.addTab("RCM 1", new RCMPanel(myRCM2));
        tabs.addTab("RMOS", myRMOSPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        int height = screenSize.height;
//        int width = screenSize.width;

        setSize(1920, 800);
        setLocationRelativeTo(null);



        setVisible(true);
    }
}
