package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.*;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

class RCMPanel extends JPanel {
    private JButton toggleSessionButton;
    private JButton endSessionButton;
    private JButton weighButton;
    private JButton cancelButton;
    private JTextArea receipt;
    private JTextArea info;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private RecyclingMachine myRCM;

    private RecyclableItem myItem;

    public void refresh () {
        updatePriceList();
    }

    private void updatePriceList() {
        info.setText("Prices\n");
        info.append(myRCM.printPrices());
    }
    private void submitItem() {
        myRCM.recycleItem(myItem);
        myRCM.payOut(myRCM.calculateItemPrice(myItem));
        receipt.setText("Receipt\n");
    }

    private void toggleButtons() {
        if (myRCM.isSession()) {
            toggleSessionButton.setText("Cancel Session");
            endSessionButton.setEnabled(true);
        } else {
            endSessionButton.setEnabled(false);
            toggleSessionButton.setText("Start Session");
        }
    }

    RCMPanel(RecyclingMachine RCM) {
        myRCM = RCM;

        this.setLayout(new BorderLayout());
        receipt = new JTextArea();
        receipt.setEditable(false);
        receipt.setPreferredSize(new Dimension(200, 0));
        receipt.setMargin(new Insets(10, 10, 10, 10));
        receipt.append("Receipt\n");

        info = new JTextArea();
        info.setEditable(false);
        info.setPreferredSize(new Dimension(200, 0));
        info.setMargin(new Insets(10, 10, 10, 10));

        info.append("ID: " + String.valueOf(myRCM.getId()) + "\n");
        info.append("Location: " + myRCM.getLocation() + "\n");

        for(int i = 0; i < 3; i++)
            info.append("\n");

        info.append("Prices\n");

        info.append(myRCM.printPrices());

        toggleSessionButton = new JButton("Start Session");
        toggleSessionButton.setActionCommand("start");
        toggleSessionButton.addActionListener(actionEvent -> {
            if (actionEvent.getActionCommand().equals("start")) {
                myRCM.startSession();
                toggleButtons();
                toggleSessionButton.setActionCommand("cancel");
            } else {
                myRCM.cancelSession();
                receipt.setText("Receipt\n");
                toggleButtons();
                toggleSessionButton.setActionCommand("start");
            }
        });

        endSessionButton = new JButton("End Session & Submit Items");
        endSessionButton.setEnabled(false);
        endSessionButton.addActionListener(actionEvent -> {
            if (myRCM.isSession()) {
                myRCM.submitSession();
                toggleButtons();
                myRCM.payOut();
                receipt.setText("Receipt\n");
                toggleSessionButton.setActionCommand("start");
            }
        });

        weighButton = new JButton("Weigh Item");
        weighButton.setActionCommand("weigh");

        weighButton.addActionListener(actionEvent -> {
            if (actionEvent.getActionCommand().equals("weigh")) {
                String[] items = myRCM.getAcceptableItems().toArray(new String[0]);

                int size = myRCM.getAcceptableItems().size();

                Random r = new Random();
                int materialIndex = r.nextInt(size);

                myItem = new RecyclableItem(items[materialIndex]);
                receipt.append(myItem.toString() + " ");
                receipt.append("$" + String.valueOf(myRCM.calculateItemPrice(myItem)) + "\n");
                if (!myRCM.isSession()) {
                    toggleSessionButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    weighButton.setActionCommand("submit");
                    weighButton.setText("Submit Item");
                } else {
                    myRCM.recycleItem(myItem);
                }
            } else {
                submitItem();
                toggleSessionButton.setEnabled(true);
                cancelButton.setEnabled(false);
                weighButton.setActionCommand("weigh");
                weighButton.setText("Weigh Item");
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);

        cancelButton.addActionListener(actionEvent -> {
            receipt.setText("Receipt\n");
            cancelButton.setEnabled(false);
            weighButton.setText("Weigh Item");
            toggleSessionButton.setEnabled(true);
        });

        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());

        centerPanel.add(weighButton);
        centerPanel.add(cancelButton);

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