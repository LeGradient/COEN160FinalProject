package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.*;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

class RCMPanel extends JPanel {
    private JButton toggleSessionButton;
    private JButton endSessionButton;
    private JButton weighButton;
    private JButton submitButton;
    private JTextArea receipt;
    private JTextArea info;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private RecyclingMachine myRCM;

    private RecyclableItem myItem;

    private void toggleButtons() {
        if (myRCM.isSession()) {
            toggleSessionButton.setText("Cancel Session");
            endSessionButton.setEnabled(true);
        } else {
            endSessionButton.setEnabled(false);
            toggleSessionButton.setText("Start Session");
        }
    }

    public RCMPanel(RecyclingMachine RCM) {
        myRCM = RCM;

        this.setLayout(new BorderLayout());
        receipt = new JTextArea();
        receipt.setEditable(false);
        receipt.append("Receipt\n");

        info = new JTextArea();
        info.setEditable(false);

        info.append("ID: " + String.valueOf(myRCM.getId()) + "\n");
        info.append("Location: " + myRCM.getLocation() + "\n");

        for(int i = 0; i < 3; i++)
            info.append("\n");

        info.append("Prices\n");

        info.append(myRCM.printPrices());

        toggleSessionButton = new JButton("Start Session");
        toggleSessionButton.addActionListener(actionEvent -> {
            if (!myRCM.isSession()) {
                myRCM.startSession();
                toggleButtons();
            } else {
              myRCM.cancelSession();
              toggleButtons();
            }
        });

        submitButton = new JButton("Submit Item");
        submitButton.addActionListener(actionEvent -> {
            // TODO pay out for 1 item

            myRCM.recycleItem(myItem);
            myRCM.payOut(myRCM.calculatePrice(myItem));
            receipt.setText("Receipt\n");
        });

        endSessionButton = new JButton("End Session & Submit Items");
        endSessionButton.setEnabled(false);
        endSessionButton.addActionListener(actionEvent -> {
            if (myRCM.isSession()) {
                myRCM.submitSession();
                toggleButtons();
                myRCM.payOut();
                receipt.setText("Receipt\n");
            }
        });

        weighButton = new JButton("Weigh Item");
        weighButton.addActionListener(actionEvent -> {
            String[] items = myRCM.getAcceptableItems().toArray(new String[0]);

            int size = myRCM.getAcceptableItems().size();

            Random r = new Random();
            int materialIndex = r.nextInt(size);

            myItem = new RecyclableItem(items[materialIndex]);
            receipt.append(myItem.toString() + " ");
            receipt.append("$" + String.valueOf(myRCM.calculatePrice(myItem)) + "\n");
        });

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());

        centerPanel.add(weighButton);
        centerPanel.add(submitButton);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        bottomPanel.add(toggleSessionButton);
        bottomPanel.add(endSessionButton);

        this.add(centerPanel, BorderLayout.PAGE_START);
        this.add(bottomPanel, BorderLayout.PAGE_END);
        this.add(info, BorderLayout.LINE_START);
        this.add(receipt, BorderLayout.LINE_END);
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
