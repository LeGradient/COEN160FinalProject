package edu.scu.coen160.finalproject.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class RCMPanel extends JPanel {
    public RCMPanel() {
        // TODO initialize the GUI for an RCM
    }
}

class RMOSPanel extends JPanel {
    public RMOSPanel() {
        // TODO initialize the GUI for an RMOS
    }
}
public class RecyclingUI extends JFrame {
    private JTabbedPane tabs = new JTabbedPane();
    public RecyclingUI() {
        super("Recycling Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        setSize(width/4 , height/4);
        setLocationRelativeTo(null);

        tabs.addTab("RCM 1", new RCMPanel());
        tabs.addTab("RCM 2", new RCMPanel());
        tabs.addTab("RMOS", new RMOSPanel());
    }
}
