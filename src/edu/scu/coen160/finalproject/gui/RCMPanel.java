package edu.scu.coen160.finalproject.gui;

import edu.scu.coen160.finalproject.system.RecyclableItem;
import edu.scu.coen160.finalproject.system.RecyclingMachine;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * RCM UI class
 * Contains components to recycle an item (individually and in sessions) and view the location/ID/prices of the RCM.
 * Uses the Observer/Observable design pattern; this is an observer object.
 */
class RCMPanel extends JPanel implements Observer {

    /**
     * A button to start and cancel a session.
     */
    private JButton toggleSessionButton;

    /**
     * A button to end and submit a session.
     */
    private JButton endSessionButton;

    /**
     * A button to weigh an item.
     */
    private JButton weighButton;

    /**
     * A button to cancel the submission.
     */
    private JButton cancelButton;

    /**
     * A text area to serve as a receipt for the recycled items.
     */
    private JTextArea receipt;

    /**
     * A text area that displays information about the RCM.
     */
    private JTextArea info;

    /**
     * A panel for the center of the RCM window.
     */
    private JPanel centerPanel;

    /**
     * A panel for the bottom of the RCM window.
     */
    private JPanel bottomPanel;

    /**
     * The RCM that this GUI controls.
     */
    private RecyclingMachine myRCM;

    /**
     * A recyclable item.
     */
    private RecyclableItem myItem;

    /**
     * Observer update method.
     * Called on a state change in the RCM. Updates the price list in the GUI.
     * @param o     unused
     * @param arg   unused
     */
    public void update(Observable o, Object arg) {
        info.setText("");
        info.append("ID: " + String.valueOf(myRCM.getId()) + "\n");
        info.append("Location: " + myRCM.getLocation() + "\n");

        info.append("Prices\n");
        info.append(myRCM.printPrices());
    }

    /**
     * Submits a recycled item. Blanks the receipt.
     */
    private void submitItem() {
        myRCM.recycleItem(myItem);
        myRCM.payOut(myRCM.calculateItemPrice(myItem));
        receipt.setText("Receipt\n");
    }

    /**
     * Toggles the enabled/disabled state of the submit session button.
     */
    private void toggleButtons() {
        if (myRCM.isSession()) {
            toggleSessionButton.setText("Cancel Session");
            endSessionButton.setEnabled(true);
        } else {
            endSessionButton.setEnabled(false);
            toggleSessionButton.setText("Start Session");
        }
    }

    /**
     * Initializes the RCM UI. Adds the components to the window and sets up handlers for action events.
     *
     * @param RCM   The RCM that this UI controls.
     */
    RCMPanel(RecyclingMachine RCM) {
        myRCM = RCM;

        // add the GUI as an observer of the RCM
        myRCM.addObserver(this);

        // this panel has a border layout
        this.setLayout(new BorderLayout());

        // create the receipt text area
        receipt = new JTextArea();
        receipt.setEditable(false);
        receipt.setPreferredSize(new Dimension(200, 0));
        receipt.setMargin(new Insets(10, 10, 10, 10));
        receipt.append("Receipt\n");

        // create the information display area
        info = new JTextArea();
        info.setEditable(false);
        info.setPreferredSize(new Dimension(200, 0));
        info.setMargin(new Insets(10, 10, 10, 10));

        info.append("ID: " + String.valueOf(myRCM.getId()) + "\n");
        info.append("Location: " + myRCM.getLocation() + "\n");

        info.append("Prices\n");

        // add the prices to the info area
        info.append(myRCM.printPrices());

        // set up the button to start and/or cancel sessions
        toggleSessionButton = new JButton("Start Session");
        toggleSessionButton.setActionCommand("start");

        // lambda function
        toggleSessionButton.addActionListener(actionEvent -> {
            // if we are starting a session, start it and toggle the buttons
            if (actionEvent.getActionCommand().equals("start")) {
                myRCM.startSession();
                toggleButtons();
                toggleSessionButton.setActionCommand("cancel");
            } else {
                // we are canceling a session; wipe the receipt area and toggle buttons
                myRCM.cancelSession();
                receipt.setText("Receipt\n");
                toggleButtons();
                toggleSessionButton.setActionCommand("start");
            }
        });

        // set up the button to end the session and submit items
        endSessionButton = new JButton("End Session & Submit Items");
        endSessionButton.setEnabled(false);

        // lambda function
        endSessionButton.addActionListener(actionEvent -> {
            // if we are in a session, submit it, toggle buttons, pay out, and wipe the receipt
            if (myRCM.isSession()) {
                myRCM.submitSession();
                toggleButtons();
                myRCM.payOut();
                receipt.setText("Receipt\n");
                toggleSessionButton.setActionCommand("start");
            }
        });

        // set up the button to weigh items
        weighButton = new JButton("Weigh Item");
        weighButton.setActionCommand("weigh");

        // lambda function
        weighButton.addActionListener(actionEvent -> {
            // if we are weighing an item
            if (actionEvent.getActionCommand().equals("weigh")) {
                // Generate an item with random material and weight
                String[] items = myRCM.getAcceptableItems().toArray(new String[0]);

                int size = myRCM.getAcceptableItems().size();

                Random r = new Random();
                int materialIndex = r.nextInt(size);

                myItem = new RecyclableItem(items[materialIndex]);

                // add the item to the receipt
                receipt.append(myItem.toString() + " ");
                receipt.append("$" + String.valueOf(myRCM.calculateItemPrice(myItem)) + "\n");

                // if we're not in a session, toggle the buttons
                if (!myRCM.isSession()) {
                    toggleSessionButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    weighButton.setActionCommand("submit");
                    weighButton.setText("Submit Item");
                }
            } else {
                // we are submitting an item, so submit it and toggle the buttons
                submitItem();
                toggleSessionButton.setEnabled(true);
                cancelButton.setEnabled(false);
                weighButton.setActionCommand("weigh");
                weighButton.setText("Weigh Item");
            }
        });

        // set up the cancel button
        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);

        // lambda function
        cancelButton.addActionListener(actionEvent -> {
            // cancel the submission, blank the receipt
            receipt.setText("Receipt\n");
            cancelButton.setEnabled(false);
            weighButton.setText("Weigh Item");
            toggleSessionButton.setEnabled(true);
        });

        // set up the center panel
        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());

        // add the weigh and cancel buttons to the center panel
        centerPanel.add(weighButton);
        centerPanel.add(cancelButton);

        // set up the bottom panel
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        // add the session buttons to the bottom panel
        bottomPanel.add(toggleSessionButton);
        bottomPanel.add(endSessionButton);

        // add all the components to this panel
        this.add(centerPanel, BorderLayout.PAGE_START);
        this.add(bottomPanel, BorderLayout.PAGE_END);
        this.add(info, BorderLayout.LINE_START);
        this.add(receipt, BorderLayout.LINE_END);
    }
}