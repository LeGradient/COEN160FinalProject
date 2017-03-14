package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.RecyclingMachine;
import edu.scu.coen160.finalproject.system.RecyclingMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class RCMPanel extends JPanel implements ActionListener{
    public RCMPanel(RecyclingMachine RCM) {
        setLayout(new BorderLayout());
        JTextArea receipt = new JTextArea();
        receipt.setEditable(true);

        add(receipt, BorderLayout.EAST);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        // TODO Make button preses do things
    }
}

class RMOSPanel extends JPanel {
    public RMOSPanel(RecyclingMonitor RMOS) {
        // TODO initialize the GUI for an RMOS
    }
}
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

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;

        setSize(width/4 , height/4);
        setLocationRelativeTo(null);

        Container container = getContentPane();
        container.add(tabs);

        tabs.addTab("RCM 1", new RCMPanel(myRCM1));
        tabs.addTab("RCM 2", new RCMPanel(myRCM2));
        tabs.addTab("RMOS", new RMOSPanel(myRMOS));

        setVisible(true);
    }
}
